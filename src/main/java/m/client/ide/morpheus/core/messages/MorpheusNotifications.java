/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core.messages;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import icons.FlutterIcons;
import m.client.ide.morpheus.MessageBundle;
import org.jetbrains.annotations.NotNull;

import static m.client.ide.morpheus.framework.messages.FrameworkMessages.MORPHEUS_LOGGING_NOTIFICATION_GROUP_ID;
import static m.client.ide.morpheus.framework.messages.FrameworkMessages.MORPHEUS_NOTIFICATION_GROUP_ID;

public class MorpheusNotifications {
    public static final Topic<MorpheusNotifier> MORPHEUS_NOTIFIER_TOPIC = Topic.create("morpheus.notifier", MorpheusNotifier.class);

    private static final String RELOAD_ALREADY_RUN = "morpheus.reload.alreadyRun";

    public static void init(@NotNull Project project) {
        // Initialize the flutter run notification group.
        NotificationsConfiguration.getNotificationsConfiguration().register(
                MORPHEUS_NOTIFICATION_GROUP_ID,
                NotificationDisplayType.BALLOON,
                false);
        NotificationsConfiguration.getNotificationsConfiguration().register(
                MORPHEUS_LOGGING_NOTIFICATION_GROUP_ID,
                NotificationDisplayType.BALLOON,
                true);

        final MorpheusNotifications notifications = new MorpheusNotifications(project);

        Runnable runnable = notifications::checkForDisplayFirstReload;
        //noinspection CodeBlock2Expr
        project.getMessageBus().connect().subscribe(
                MorpheusNotifications.MORPHEUS_NOTIFIER_TOPIC);
    }

    @NotNull
    final Project myProject;

    MorpheusNotifications(@NotNull Project project) {
        this.myProject = project;
    }

    private void checkForDisplayFirstReload() {
        if (myProject.isDisposed()) {
            return;
        }
        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);

        final boolean alreadyRun = properties.getBoolean(RELOAD_ALREADY_RUN);
        if (!alreadyRun) {
            properties.setValue(RELOAD_ALREADY_RUN, true);

            final Notification notification = new Notification(
                    MORPHEUS_NOTIFICATION_GROUP_ID,
                    MessageBundle.message("morpheus.reload.firstRun.title"),
                    MessageBundle.message("morpheus.reload.firstRun.content"),
                    NotificationType.INFORMATION);
            notification.setIcon(FlutterIcons.HotReload);
            notification.addAction(new AnAction("Learn more") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    BrowserUtil.browse(MessageBundle.message("morpheus.reload.firstRun.url"));
                    notification.expire();
                }
            });
            Notifications.Bus.notify(notification, myProject);
        }
    }
}

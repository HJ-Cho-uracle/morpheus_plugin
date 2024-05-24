package m.client.ide.morpheus.core.messages;

import com.android.annotations.Nullable;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import icons.CoreIcons;
import icons.FlutterIcons;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import org.jetbrains.annotations.NotNull;

import static m.client.ide.morpheus.framework.messages.FrameworkMessages.MORPHEUS_LOGGING_NOTIFICATION_GROUP_ID;
import static m.client.ide.morpheus.framework.messages.FrameworkMessages.MORPHEUS_NOTIFICATION_GROUP_ID;

public class MorpheusNotifier {


    private static String id = MORPHEUS_NOTIFICATION_GROUP_ID;

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
    }

    public static void showError(String title, String message, @Nullable Project project) {
        // TODO: Make the project parameter not nullable.
        Notifications.Bus.notify(
                new Notification(id,
                        title,
                        message,
                        NotificationType.ERROR), project);
    }

    public static void showWarning(String title, String message, @Nullable Project project) {
        // TODO: Make the project parameter not nullable.
        Notifications.Bus.notify(
                new Notification(
                        FrameworkMessages.MORPHEUS_NOTIFICATION_GROUP_ID,
                        title,
                        message,
                        NotificationType.WARNING, NotificationListener.URL_OPENING_LISTENER), project);
    }

    public static void showInfo(String title, String message, @Nullable Project project) {
        // TODO: Make the project parameter not nullable.
        final Notification notification = new Notification(
                FrameworkMessages.MORPHEUS_NOTIFICATION_GROUP_ID,
                title,
                message,
                NotificationType.INFORMATION);
        notification.setIcon(CoreIcons.icon16);
        Notifications.Bus.notify(notification, project);
    }
    public static void notifyInfo(Project project, String content) {
        notify(project, NotificationType.INFORMATION, content);
    }

    public static void notifyWaring(Project project, String content) {
        notify(project, NotificationType.WARNING, content);
    }

    public static void notifyError(Project project, String content) {
        notify(project, NotificationType.ERROR, content);
    }

    //<notificationGroup id="Morpheus Notification Group"
//    displayType="BALLOON"
//    key="notification.group.name"/>
    public static void notify(Project project, NotificationType type, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(id)
                .createNotification(content, type)
                .notify(project);
    }

//    private static final NotificationGroup NOTIFICATION_GROUP =
//            new NotificationGroup("Custom Notification Group",
//                    NotificationDisplayType.BALLOON, true);
//
//    public static void notify(Project project, NotificationType type, String content) {
//        NOTIFICATION_GROUP.createNotification(content, type)
//                .notify(project);
//    }
}
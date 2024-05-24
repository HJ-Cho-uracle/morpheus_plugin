/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core;

import com.intellij.framework.FrameworkType;
import com.intellij.framework.detection.DetectionExcludesConfiguration;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.startup.StartupActivity;
import m.client.ide.morpheus.core.utils.CommonUtil;
import org.jetbrains.android.facet.AndroidFrameworkDetector;
import org.jetbrains.annotations.NotNull;

/**
 * Runs startup actions just after a project is opened, before it's indexed.
 *
 * @see MorpheusInitializer for actions that run later.
 */
public class ProjectOpenActivity implements StartupActivity, DumbAware {
  public static final ProjectType MORPHEUS_PROJECT_TYPE = new ProjectType("io.flutter");
  private static final Logger LOG = Logger.getInstance(ProjectOpenActivity.class);

  public ProjectOpenActivity() {
  }

  @Override
  public void runActivity(@NotNull Project project) {
//    TimeTracker.getInstance(project).onProjectOpen();
//
//    // TODO(helinx): We don't have a good way to check whether a Bazel project is using Flutter. Look into whether we can
//    // build a better Flutter Bazel check into `declaresFlutter` so we don't need the second condition.
//    if (!FlutterModuleUtils.declaresFlutter(project) && !WorkspaceCache.getInstance(project).isBazel()) {
//      return;
//    }
//
//    // Set up JxBrowser listening and check if it's already enabled.
//    JxBrowserManager.getInstance().listenForSettingChanges(project);
//    JxBrowserManager.getInstance().setUp(project);
    excludeAndroidFrameworkDetector(project);

//    final FlutterSdk sdk = FlutterSdk.getIncomplete(project);
//    if (sdk == null) {
//      // We can't do anything without a Flutter SDK.
//      // Note: this branch is taken when opening a project immediately after creating it -- not sure that is expected.
//      return;
//    }
//
//    // Report time when indexing finishes.
//    DumbService.getInstance(project).runWhenSmart(() -> {
//      FlutterInitializer.getAnalytics().sendEventMetric(
//        "startup",
//        "indexingFinished",
//        project.getService(TimeTracker.class).millisSinceProjectOpen(),
//        FlutterSdk.getFlutterSdk(project)
//      );
//    });
//
//    ApplicationManager.getApplication().executeOnPooledThread(() -> {
//      sdk.queryFlutterConfig("android-studio-dir", false);
//    });
//    if (CommonUtil.isAndroidStudio() && !FLUTTER_PROJECT_TYPE.equals(ProjectTypeService.getProjectType(project))) {
//      if (!AndroidUtils.isAndroidProject(project)) {
//        ProjectTypeService.setProjectType(project, FLUTTER_PROJECT_TYPE);
//      }
//    }
//
//    // If this project is intended as a bazel project, don't run the pub alerts.
//    if (WorkspaceCache.getInstance(project).isBazel()) {
//      return;
//    }
//
//    for (PubRoot pubRoot : PubRoots.forProject(project)) {
//      if (!pubRoot.hasUpToDatePackages()) {
//        Notifications.Bus.notify(new PackagesOutOfDateNotification(project, pubRoot), project);
//      }
//    }
  }

  private static void excludeAndroidFrameworkDetector(@NotNull Project project) {
    if (CommonUtil.isAndroidStudio()) {
      return;
    }
    IdeaPluginDescriptor desc;
    if (PluginManagerCore.getPlugin(PluginId.getId("org.jetbrains.android")) == null) {
      return;
    }
    try {
      final DetectionExcludesConfiguration excludesConfiguration = DetectionExcludesConfiguration.getInstance(project);
      try {
        final FrameworkType type = new AndroidFrameworkDetector().getFrameworkType();
        if (!excludesConfiguration.isExcludedFromDetection(type)) {
          excludesConfiguration.addExcludedFramework(type);
        }
      } catch (NullPointerException ignored) {
        // If the Android facet has not been configured then getFrameworkType() throws a NPE.
      }
    } catch (NoClassDefFoundError ignored) {
      // This should never happen. But just in case ...
    }
  }

//  private static class PackagesOutOfDateNotification extends Notification {
//    @NotNull private final Project myProject;
//    @NotNull private final PubRoot myRoot;

//    public PackagesOutOfDateNotification(@NotNull Project project, @NotNull PubRoot root) {
//      super("Morpheus Packages", CoreIcons.icon16, "Flutter pub get.",
//            null, "The pubspec.yaml file has been modified since " +
//                  "the last time 'flutter pub get' was run.",
//            NotificationType.INFORMATION, null);
//
//      myProject = project;
//      myRoot = root;
//
//      //noinspection DialogTitleCapitalization
//      addAction(new AnAction("Run 'flutter pub get'") {
//        @Override
//        public void actionPerformed(@NotNull AnActionEvent event) {
//          expire();

//          final FlutterSdk sdk = FlutterSdk.getFlutterSdk(project);
//          if (sdk == null) {
//            Messages.showErrorDialog(project, "Flutter SDK not found", "Error");
//            return;
//          }
//
//          if (sdk.startPubGet(root, project) == null) {
//            Messages.showErrorDialog("Unable to run 'flutter pub get'", "Error");
//          }
//        }
//      });
//    }
//  }
}

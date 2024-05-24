/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessor;
import icons.CoreIcons;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

public class MorpheusProjectOpenProcessor extends ProjectOpenProcessor {
  private static final Logger LOG = Logger.getInstance(MorpheusProjectOpenProcessor.class);

  @NotNull
  @Override
  public String getName() {
    return MessageBundle.message("morpheus.module.name");
  }

  @Override
  public Icon getIcon() {
    return CoreIcons.icon16;
  }

  @Override
  public boolean canOpenProject(@Nullable VirtualFile file) {
    if (file == null) return false;
    final ApplicationInfo info = ApplicationInfo.getInstance();
    if (CommonUtil.isAndroidStudio()) {
      return false;
    }
//    final PubRoot root = PubRoot.forDirectory(file);
//    return root != null && root.declaresFlutter();
    return true;
  }

  /**
   * Runs when a project is opened by selecting the project directly, possibly for import.
   * <p>
   * Doesn't run when a project is opened via recent projects menu (and so on). Actions that
   * should run every time a project is opened should be in
   * {@link ProjectOpenActivity} or {@link MorpheusInitializer}.
   */
  @Nullable
  @Override
  public Project doOpenProject(@NotNull VirtualFile file, @Nullable Project projectToClose, boolean forceOpenInNewFrame) {
    // Delegate opening to the platform open processor.
    final ProjectOpenProcessor importProvider = getDelegateImportProvider(file);
    if (importProvider == null) return null;

    final Project project = importProvider.doOpenProject(file, projectToClose, forceOpenInNewFrame);
    if (project == null || project.isDisposed()) return project;

    // Convert any modules that use Flutter but don't have IntelliJ Flutter metadata.
//    convertToFlutterProject(project);

    // Project gets reloaded; should this be: FlutterUtils.findProject(file.getPath());
    return project;
  }

  @Nullable
  protected ProjectOpenProcessor getDelegateImportProvider(@NotNull VirtualFile file) {
    return Arrays.stream(Extensions.getExtensions(EXTENSION_POINT_NAME)).filter(
      processor -> processor.canOpenProject(file) && !Objects.equals(processor.getName(), getName())
    ).findFirst().orElse(null);
  }

  /**
   * Sets up a project that doesn't have any Flutter modules.
   * <p>
   * (It probably wasn't created with "flutter create" and probably didn't have any IntelliJ configuration before.)
   */
//  private static void convertToFlutterProject(@NotNull Project project) {
//    for (Module module : FlutterModuleUtils.getModules(project)) {
//      if (FlutterModuleUtils.declaresFlutter(module) && !FlutterModuleUtils.isFlutterModule(module)) {
//        FlutterModuleUtils.setFlutterModuleAndReload(module, project);
//      }
//    }
//  }

  @Override
  public boolean isStrongProjectInfoHolder() {
    return true;
  }
}

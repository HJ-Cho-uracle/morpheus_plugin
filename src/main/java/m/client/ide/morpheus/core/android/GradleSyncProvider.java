/*
 * Copyright 2019 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core.android;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface GradleSyncProvider {
  ExtensionPointName<GradleSyncProvider> EP_NAME = ExtensionPointName.create("io.flutter.gradleSyncProvider");

  void scheduleSync(@NotNull Project project);
}

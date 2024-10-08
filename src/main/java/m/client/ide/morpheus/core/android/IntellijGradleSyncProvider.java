/*
 * Copyright 2019 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core.android;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class IntellijGradleSyncProvider implements GradleSyncProvider {

  @Override
  public void scheduleSync(@NotNull Project project) {
    // TODO(messick): Restore the next line after Android Q sources are published; it cannot be compiled until then.
    //GradleSyncInvoker.getInstance().requestProjectSync(project, GradleSyncInvoker.Request.userRequest());
  }
}

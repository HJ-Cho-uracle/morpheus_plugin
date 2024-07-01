/*
 * Copyright 2016 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Determines when we can run a Dart file as a Flutter app.
 * <p>
 * (For example, when right-clicking on main.dart in the Project view.)
 */
public class IOSRunConfigurationProducer extends RunConfigurationProducer<IOSRunConfiguration> {

    public IOSRunConfigurationProducer() {
        super(IOSRunConfigurationType.getInstance());
    }

    /**
     * <p>
     * Returns false if it wasn't a match.
     */
    @Override
    protected boolean setupConfigurationFromContext(final @NotNull IOSRunConfiguration config,
                                                    final @NotNull ConfigurationContext context,
                                                    final @NotNull Ref<PsiElement> sourceElement) {
        Project project = context.getProject();
        if (!OSUtil.isMac() || !MorpheusConfigManager.isMorpheusProject(project)) return false;

        config.setGeneratedName();

        final PsiElement elt = sourceElement.get();
        if (elt != null) {
            sourceElement.set(elt.getContainingFile());
        }
        return true;
    }

    /**
     * @param iosRunConfigState
     * @param configurationContext
     * @return
     */
    @Override
    public boolean isConfigurationFromContext(@NotNull IOSRunConfiguration iosRunConfigState, @NotNull ConfigurationContext configurationContext) {
        if (configurationContext != null)
            return true;

        return false;
    }

    /**
     * @param internalUsageOnly
     */
    public IOSRunConfigurationProducer(boolean internalUsageOnly) {
        super(internalUsageOnly);
    }

    /**
     * @param context
     * @return
     */
    @Override
    public @Nullable ConfigurationFromContext createConfigurationFromContext(@NotNull ConfigurationContext context) {
        Project project = context.getProject();
        if(OSUtil.isMac() && MorpheusConfigManager.isMorpheusProject(project))
            return super.createConfigurationFromContext(context);

        return null;
    }

    /**
     * @param self
     * @param other
     * @return
     */
    @Override
    public boolean isPreferredConfiguration(ConfigurationFromContext self, ConfigurationFromContext other) {
        return super.isPreferredConfiguration(self, other);
    }

    /**
     * @param configuration
     * @param context
     * @param startRunnable
     */
    @Override
    public void onFirstRun(@NotNull ConfigurationFromContext configuration, @NotNull ConfigurationContext context, @NotNull Runnable startRunnable) {
        super.onFirstRun(configuration, context, startRunnable);
    }

    /**
     * @param context
     * @return
     */
    @Override
    public @Nullable ConfigurationFromContext findOrCreateConfigurationFromContext(@NotNull ConfigurationContext context) {
        return super.findOrCreateConfigurationFromContext(context);
    }

    /**
     * @param context
     * @return
     */
    @Override
    public @Nullable RunnerAndConfigurationSettings findExistingConfiguration(@NotNull ConfigurationContext context) {
        return super.findExistingConfiguration(context);
    }

    /**
     * @param context
     * @return
     */
    @Override
    protected RunnerAndConfigurationSettings cloneTemplateConfiguration(@NotNull ConfigurationContext context) {
        return super.cloneTemplateConfiguration(context);
    }
}

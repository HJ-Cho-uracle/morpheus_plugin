/*
 * Copyright 2019 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */

package m.client.ide.morpheus.core.console;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.ExecutionSearchScopes;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.concurrency.QueueProcessor;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handle displaying dart:developer log messages and Flutter.Error messages in the Run and Debug
 * console.
 */
public class MorpheusConsoleLogManager {
    private static final Logger LOG = Logger.getInstance(MorpheusConsoleLogManager.class);

    private static final String consolePreferencesSetKey = "m.client.ide.morpheus.console.preferencesSet";
    private static final String DEEP_LINK_GROUP_ID = "deeplink";

    private static final ConsoleViewContentType TITLE_CONTENT_TYPE =
            new ConsoleViewContentType("title", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES.toTextAttributes());
    private static final ConsoleViewContentType NORMAL_CONTENT_TYPE = ConsoleViewContentType.NORMAL_OUTPUT;
    private static final ConsoleViewContentType SUBTLE_CONTENT_TYPE =
            new ConsoleViewContentType("subtle", SimpleTextAttributes.GRAY_ATTRIBUTES.toTextAttributes());
    private static final ConsoleViewContentType ERROR_CONTENT_TYPE = ConsoleViewContentType.ERROR_OUTPUT;

    private static QueueProcessor<Runnable> queue;
    private static final AtomicInteger queueLength = new AtomicInteger();

    /**
     * Set our preferred settings for the run console.
     */
    public static void initConsolePreferences() {
        final PropertiesComponent properties = PropertiesComponent.getInstance();
        if (!properties.getBoolean(consolePreferencesSetKey)) {
            properties.setValue(consolePreferencesSetKey, true);

            // Set our preferred default settings for console text wrapping.
            final EditorSettingsExternalizable editorSettings = EditorSettingsExternalizable.getInstance();
            editorSettings.setUseSoftWraps(true, SoftWrapAppliancePlaces.CONSOLE);
        }
    }

    @NotNull
    final ConsoleView console;
    @NotNull
    private int frameErrorCount = 0;

    public MorpheusConsoleLogManager(@NotNull Project project, @NotNull ExecutionEnvironment environment) {
        GlobalSearchScope searchScope = ExecutionSearchScopes.executionScope(project, environment.getRunProfile());
        @NotNull TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project, searchScope);
        console = consoleBuilder.getConsole();
    }

    public void handleMorpheusErrorEvent(@NotNull String message) {
        try {
            if (CoreSettingsState.getInstance().isShowDebugMessage()) {
                queueLength.incrementAndGet();

                queue.add(() -> {
                    try {
                        processErrorEvent(message);
                    } catch (Throwable t) {
                        LOG.warn(t);
                    } finally {
                        queueLength.decrementAndGet();

                        synchronized (queueLength) {
                            queueLength.notifyAll();
                        }
                    }
                });
            }
        } catch (Throwable t) {
            LOG.warn(t);
        }
    }

    /**
     * Wait until all pending work has completed.
     */
    public void flushMorpheusErrorQueue() {
        // If the queue isn't empty, then wait until the all the items have been processed.
        if (queueLength.get() > 0) {
            try {
                while (queueLength.get() > 0) {
                    synchronized (queueLength) {
                        queueLength.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int errorSeparatorLength = 100;
    private static final String errorSeparatorChar = "=";

    /**
     * Pretty print the error using the available console syling attributes.
     */
    private void processErrorEvent(@NotNull String message) {
        final String description = " " + message + " ";

        final boolean terseError = !isFirstErrorForFrame() && !CoreSettingsState.getInstance().isShowCLIDebug();

        frameErrorCount++;

        final String prefix = "========";
        final String suffix = "==";

        console.print("\n" + prefix, TITLE_CONTENT_TYPE);
        console.print(description, NORMAL_CONTENT_TYPE);
        console.print(
                StringUtil.repeat(errorSeparatorChar, Math.max(
                        errorSeparatorLength - prefix.length() - description.length() - suffix.length(), 0)),
                TITLE_CONTENT_TYPE);
        console.print(suffix + "\n", TITLE_CONTENT_TYPE);

        // TODO(devoncarew): Create a hyperlink to a widget - ala 'widget://inspector-1347'.

        console.print(StringUtil.repeat(errorSeparatorChar, errorSeparatorLength) + "\n", TITLE_CONTENT_TYPE);
    }

    private boolean isFirstErrorForFrame() {
        return frameErrorCount == 0;
    }

}

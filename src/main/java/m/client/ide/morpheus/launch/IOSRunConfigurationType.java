package m.client.ide.morpheus.launch;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import icons.CoreIcons;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IOSRunConfigurationType extends ConfigurationTypeBase {
    public static final String IOS_RUNCONFIGURATIONTYPE_ID = IOSRunConfigurationType.class.getName();

    private final Factory factory;

    public IOSRunConfigurationType() {
        super(IOS_RUNCONFIGURATIONTYPE_ID, "iOS", "iOS run configuration", CoreIcons.runIOS);

        factory = new Factory(this);
        addFactory(factory);
    }

    /**
     * Defined here for ios run configurations.
     */
    public static boolean doShowIOSRunConfigurationForProject(@NotNull Project project) {
        return OSUtil.isMac() && MorpheusConfigManager.isMorpheusProject(project);
    }

    public Factory getFactory() {
        return factory;
    }

    public static @NotNull IOSRunConfigurationType getInstance() {
        return Extensions.findExtension(CONFIGURATION_TYPE_EP, IOSRunConfigurationType.class);
    }

    public static class Factory extends ConfigurationFactory {
        public Factory(IOSRunConfigurationType type) {
            super(type);
        }

        @NotNull
        @Override
        @NonNls
        public String getId() {
            return "iOS";
        }

        @Override
        @NotNull
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            if(OSUtil.isMac() && MorpheusConfigManager.isMorpheusProject(project))
                return new IOSRunConfiguration(project, this, "iOS");

            return null;
        }

        @Override
        @NotNull
        public RunConfiguration createConfiguration(String name, @NotNull RunConfiguration template) {
            // Override the default name which is always "Unnamed".
            return super.createConfiguration(template.getProject().getName(), template);
        }

        @Override
        public boolean isApplicable(@NotNull Project project) {
            return IOSRunConfigurationType.doShowIOSRunConfigurationForProject(project);
        }

        @NotNull
        @Override
        public RunConfigurationSingletonPolicy getSingletonPolicy() {
            return RunConfigurationSingletonPolicy.MULTIPLE_INSTANCE_ONLY;
        }
    }
}

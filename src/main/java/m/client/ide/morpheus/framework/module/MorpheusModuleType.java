package m.client.ide.morpheus.framework.module;

import com.intellij.openapi.module.ModuleType;
import icons.CoreIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MorpheusModuleType extends ModuleType<MorpheusModuleBuilder> {

    private static final String id = "m.client.ide.morpheus.framework.module.MorpheusModuleType";

    protected MorpheusModuleType() {
        super(id);
    }

    @Override
    public @NotNull MorpheusModuleBuilder createModuleBuilder() {
        return new MorpheusModuleBuilder();
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
        return "Morpheus Application Project";
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
        return getName();
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean b) {
        return CoreIcons.icon16;
    }
}

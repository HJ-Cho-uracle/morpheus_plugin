package m.client.ide.morpheus.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;

public class LibraryManagerConditionClass implements Condition {
    private static LibraryManagerConditionClass instance;

    public static LibraryManagerConditionClass getInstance() {
        if(instance == null) {
            instance = new LibraryManagerConditionClass();
        }

        return instance;
    }

    @Override
    public boolean value(Object project) {
        return project instanceof Project ? MorpheusConfigManager.isMorpheusProject((Project) project) : false;
    }
}

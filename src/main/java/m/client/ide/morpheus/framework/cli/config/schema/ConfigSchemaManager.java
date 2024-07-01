package m.client.ide.morpheus.framework.cli.config.schema;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.ProjectManager;
import kotlin.TypeCastException;
import m.client.ide.morpheus.core.config.AbstractJasonFileManager;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigSchemaManager extends AbstractJasonFileManager {
    private static final String schema_key = "$schema";
    private static final String schema_value = "http://json-schema.org/draft-04/schema#";

    ConfigElement config;

    public ConfigSchemaManager() {
        try {
            @Nullable File configFile = FileUtil.getChildFile(ProjectManager.getInstance().getDefaultProject(), "schema.json");
            filePath = configFile.getAbsolutePath();
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage());
        }
    }

    @Override
    protected String makeJsonString() {
        return null;
    }

    @Override
    public void loadJsonString(String jsonString) throws ParseException, TypeCastException {
        JSONParser sp = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object jsonObject = sp.parse(jsonString);

        config = new ConfigElement(null, jsonObject);
        config.parseJSONString();
    }
}

package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractJsonElement {

    public static final int JSONPARSER_MODE = JSONParser.MODE_PERMISSIVE;

    public AbstractJsonElement(JSONObject jsonObject) {
        update(jsonObject);
    }

    public AbstractJsonElement(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser(JSONPARSER_MODE);
        Object object = parser.parse((String) jsonString);
        if(object instanceof JSONObject)
            update((JSONObject) object);
    }

    protected abstract void update(@NotNull JSONObject jsonObject);

    protected abstract Object getJSONObject();
}

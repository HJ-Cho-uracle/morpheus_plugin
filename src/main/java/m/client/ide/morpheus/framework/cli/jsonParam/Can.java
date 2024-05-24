package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

public class Can  extends AbstractJsonElement {
    private static final String key_apply = "apply";
    private static final String key_remove = "remove";

    Boolean apply;
    Boolean remove;

    public Can(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Can(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_apply);
        apply = object instanceof Boolean ? (Boolean) object : true;
        object = jsonObject.get(key_remove);
        remove = object instanceof Boolean ? (Boolean) object : false;
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_apply, apply);
        jsonObject.put(key_remove, remove);

        return jsonObject;
    }
}

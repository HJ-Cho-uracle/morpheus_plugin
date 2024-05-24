package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Version extends AbstractJsonElement{
    private static final String key_latest = "latest";
    private static final String key_current = "current";

    @Nullable String current;
    String latest;

    public Version(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Version(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_current);
        current = object != null ? object.toString() : null;
        object = jsonObject.get(key_latest);
        latest = object != null ? object.toString() : null;
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_current, current);
        jsonObject.put(key_latest, latest);

        return jsonObject;
    }
}

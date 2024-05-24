package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

public class Npm extends AbstractJsonElement {
    public static final String key = "npm";
    private static final String key_name = "name";
    private static final String key_version = "version";

    private String version;
    private String name;

    public Npm(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Npm(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_name);
        String fullname = object != null ? object.toString() : "";
        String[] tokens = fullname.split("/");
        name = tokens.length > 0 ? tokens[tokens.length -1] : fullname;
        object = jsonObject.get(key_version);
        version = object != null ? object.toString() : "";
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_name, version);
        jsonObject.put(key_version, name);

        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

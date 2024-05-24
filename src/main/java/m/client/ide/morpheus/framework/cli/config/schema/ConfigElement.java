package m.client.ide.morpheus.framework.cli.config.schema;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public class ConfigElement extends AbstractConfigElement {
    private static final String schema_key = "$schema";
    private static final String schema_value = "http://json-schema.org/draft-04/schema#";

    private String id;
    private String type;
    private String title;
    private RequiredElement required;

    public ConfigElement(AbstractConfigElement parent, Object object) throws ParseException {
        super(parent, object);
    }

    @Override
    protected Object init(Object object) throws ParseException {
        JSONObject json = (JSONObject) object;

        id = (String)json.get(jsonKey.id.getName());
        type = (String)json.get(jsonKey.type.getName());
        title = (String)json.get(jsonKey.title.getName());

        Object requiredObject = json.get(jsonKey.required.getName());
        if(requiredObject != null) {
            required = new RequiredElement(this, json.get(jsonKey.required.getName()));
            required.parseJSONString();

            initProperties(json);
        }

        return null;
    }

    private void initProperties(JSONObject json) {

    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public IConfigElement getCopy() throws ParseException {
        return null;
    }
}

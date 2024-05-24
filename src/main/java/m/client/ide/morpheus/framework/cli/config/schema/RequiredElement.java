package m.client.ide.morpheus.framework.cli.config.schema;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

import java.util.Set;

public class RequiredElement extends AbstractConfigElement {

    private Set entrySet;

    public RequiredElement(AbstractConfigElement parent, Object object) throws ParseException {
        super(parent, object);
    }

    @Override
    protected Object init(Object object) throws ParseException {
        JSONObject json = (JSONObject) object;

        entrySet = json.entrySet();
        return json;
    }

    @Override
    public String getKey() {
        return jsonKey.required.getName();
    }

    @Override
    public IConfigElement getCopy() throws ParseException {
        return null;
    }
}

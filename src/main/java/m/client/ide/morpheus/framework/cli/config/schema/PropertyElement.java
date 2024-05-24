package m.client.ide.morpheus.framework.cli.config.schema;

import groovy.json.JsonException;
import net.minidev.json.parser.ParseException;

public class PropertyElement extends AbstractConfigElement {

    public String description;
    public String type;
    public String defaultValue;

    public PropertyElement(AbstractConfigElement parent, Object object) throws ParseException {
        super(parent, object);
    }


    @Override
    public String getKey() {
        return null;
    }

    @Override
    protected Object init(Object object) throws ParseException {
        return null;
    }

    @Override
    public IConfigElement getCopy() throws JsonException {
        return null;
    }

    @Override
    public String getTooltip() {
        return null;
    }
}

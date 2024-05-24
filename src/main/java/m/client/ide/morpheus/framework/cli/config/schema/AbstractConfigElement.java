package m.client.ide.morpheus.framework.cli.config.schema;


import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public abstract class AbstractConfigElement  implements IConfigElement {

    private static final String SNIPPET_NAME_MNET_HTTP_DOWNLOAD = "M.net.http.download";

    ConfigElement configElement;
    AbstractConfigElement  parentObject;
    Object                  jsonObject;

    public AbstractConfigElement(AbstractConfigElement parent, Object object) throws ParseException {
        parentObject = parent;

        if(this instanceof ConfigElement) {
            configElement = (ConfigElement) this;
        } else if(parent instanceof ConfigElement) {
            configElement = (ConfigElement) parent;
        } else {
            while(parent instanceof ConfigElement == false) {
                if(parent == null || parent.getParent() == null) {
                    break;
                }
                parent = parent.getParent();
            }
            if(parent instanceof ConfigElement) {
                configElement = (ConfigElement) parent;
            }
        }

        jsonObject = object;
    }

    public Object getJSONObject() {
        return jsonObject;
    }


    public ConfigElement getConfigElement(){
        return configElement;
    }

    /**
     * MethodName	: init
     * ClassName	: IJsonElement
     * Commnet		: initialize object
     * Author		: johyeongjin
     * Datetime		: May 16, 2022 8:06:22 AM
     *
     * @return Object
     * @param object		: json object
     * @return
     * @throws ParseException
     */
    protected abstract Object		init(Object object) throws ParseException;

    protected boolean parseJSONString() throws ParseException {
        if(jsonObject instanceof String) {
            return parseJSONString((String) jsonObject);
        }
        return parseJSONString(jsonObject);
    }

    private boolean parseJSONString(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        return parseJSONString(parser.parse(jsonString));
    }

    private boolean parseJSONString(Object object) throws ParseException {
        jsonObject = init(object);

        return jsonObject != null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
//		sb.append("[").append(getKey() == null ? getClass().getSimpleName() : getKey().toString()).append("] ");
        if(jsonObject instanceof JSONObject) {
            sb.append(((JSONObject) jsonObject).toString()).append("\n");
        } else if(jsonObject instanceof JSONArray) {
            sb.append(((JSONArray) jsonObject).toString()).append("\n");
        } else if(jsonObject instanceof String){
            sb.append("    " + jsonObject).append("\n");
        }
        return sb.toString();
    }

    public String getTooltip() {
        return toString();
    }

    public AbstractConfigElement getParent() {
        return parentObject;
    }
}

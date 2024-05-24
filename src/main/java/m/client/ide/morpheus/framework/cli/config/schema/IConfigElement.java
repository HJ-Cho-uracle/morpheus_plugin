package m.client.ide.morpheus.framework.cli.config.schema;

import net.minidev.json.parser.ParseException;

public interface IConfigElement {
    public static enum jsonKey {
        none(0), properties(1), description(2), type(3), id(4), required(5), defaultValue(6),
        title(7);

        private final int value;

        jsonKey(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return super.toString();//.toLowerCase();
        }

        public String getName() {
            switch (this) {
                case properties:
                    return "properties";
                case description:
                    return "description";
                case type:
                    return "type";
                case id:
                    return "id";
                case required:
                    return "required";
                case defaultValue:
                    return "default";
                case title:
                    return "title";
                default:
                    return "none";
            }
        }

        public static jsonKey fromString(String type) {
            String typeLower = type.toLowerCase();
            if (typeLower.equals(properties.toString().toLowerCase()))
                return jsonKey.properties;
            if (typeLower.equals(description.toString().toLowerCase()))
                return jsonKey.description;
            if (typeLower.equals(jsonKey.type.toString().toLowerCase()))
                return jsonKey.type;
            if (typeLower.equals(jsonKey.id.toString().toLowerCase()))
                return jsonKey.id;
            if (typeLower.equals(required.toString().toLowerCase()))
                return jsonKey.required;
            if (typeLower.equals(jsonKey.defaultValue.toString().toLowerCase()))
                return jsonKey.defaultValue;
            if (typeLower.equals(jsonKey.title.toString().toLowerCase()))
                return jsonKey.title;

            throw new AssertionError("Unknown Snippet Type : " + type);
        }

        public static jsonKey valueOf(int value) {
            switch (value) {
                case 1:
                    return properties;
                case 2:
                    return description;
                case 3:
                    return type;
                case 4:
                    return id;
                case 5:
                    return required;
                case 6:
                    return defaultValue;
                case 7:
                    return title;
                default:
                    return none;
            }
        }
    }

    /**
     * MethodName	: getKey
     * ClassName	: IJsonElement
     * Commnet		: resutrn json object key of json
     * Author		: johyeongjin
     *
     * @return key	: json object key of json
     * @return
     */
    String				getKey();

    /**
     * MethodName	: getJSONObject
     * ClassName	: IJsonElement
     * Commnet		: get json object
     * Author		: johyeongjin
     *
     * @return JSONObject
     * @return
     */
    public Object	   getJSONObject();

    /**
     * MethodName	: cloneJSONObject
     * ClassName	: IJsonElement
     * Commnet		: clone JSONObject
     * Author		: johyeongjin
     *
     * @return AbstractJSONElement
     * @return
     * @throws ParseException
     */
    public IConfigElement getCopy() throws ParseException;

    public String       getTooltip();

    /**
     * MethodName	: toString
     * ClassName	: IJsonElement
     * Author		: johyeongjin
     *
     * @return String		: string of object info
     * @return
     */
    public String       toString();
}

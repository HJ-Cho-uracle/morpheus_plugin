package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

public class Permissions extends AbstractJsonElement {

    public static final String key = "owner";

    private static final String    key_admin = "admin";
    private static final String    key_push = "push";
    private static final String    key_pull = "pull";

    private Boolean  admin;
    private Boolean  push;
    private Boolean  pull;

    public Permissions(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Permissions(String jsonString) throws ParseException {
        super(jsonString);
    }

    public void update(@NotNull JSONObject json) {
        Object object = json.get(key_admin);
        admin = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_push);
        push = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_pull);
        pull = object instanceof Boolean ? (Boolean) object : false;
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_admin, admin);
        jsonObject.put(key_push, push);
        jsonObject.put(key_pull, pull);

        return jsonObject;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getPush() {
        return push;
    }

    public void setPush(Boolean push) {
        this.push = push;
    }

    public Boolean getPull() {
        return pull;
    }

    public void setPull(Boolean pull) {
        this.pull = pull;
    }
}

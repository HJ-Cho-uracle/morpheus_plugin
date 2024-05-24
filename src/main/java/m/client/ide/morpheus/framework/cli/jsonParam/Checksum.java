package m.client.ide.morpheus.framework.cli.jsonParam;

import org.jetbrains.annotations.NotNull;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public class Checksum extends AbstractJsonElement {
    public static final String key = "checksum";
    private static final String key_sha1 = "sha1";
    private String sha1;

    public Checksum(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Checksum(String jsonString) throws ParseException {
        super(jsonString);
    }

    protected void update(@NotNull JSONObject jsonObject) {
        sha1 = jsonObject.get(key_sha1).toString();
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_sha1, sha1);

        return jsonObject;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
}

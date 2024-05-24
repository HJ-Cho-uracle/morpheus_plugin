package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class LicenseParam extends AbstractJsonElement {

    private static final String key_appId = "appId";
    private static final String key_bundleId = "bundleId";
    private static final String key_packageName = "packageName";
    private static final String key_expirationDate = "expirationDate";

    private String appId;
    private String bundleId;
    private String packageName;
    private String expirationDate;

    public LicenseParam(String jsonString) throws ParseException {
        super(jsonString);
    }
    public LicenseParam(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static void parseLicense(@NotNull String jsonString, @NotNull Map<String, LicenseParam> licenses) throws ParseException {
        JSONParser parser = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object object = parser.parse((String) jsonString);
        if(object instanceof JSONObject) {
            LicenseParam license = new LicenseParam((JSONObject) object);
            licenses.put(license.getAppId(), license);
        } else if(object instanceof JSONArray) {
            JSONArray objects = (JSONArray) object;
            Iterator iterator = objects.iterator();
            while(iterator.hasNext()) {
                Object element = iterator.next();
                if(element instanceof JSONObject) {
                    JSONObject json = (JSONObject) element;
                    LicenseParam license = new LicenseParam((JSONObject) json);
                    licenses.put(license.getAppId(), license);
                }
            }
        }
    }

    public void update(@NotNull JSONObject json) {
        this.appId = json.get(key_appId).toString();
        this.bundleId = json.get(key_bundleId).toString();
        this.packageName = json.get(key_packageName).toString();
        this.expirationDate = json.get(key_expirationDate).toString();
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_appId, appId);
        jsonObject.put(key_bundleId, bundleId);
        jsonObject.put(key_packageName, packageName);
        jsonObject.put(key_expirationDate, expirationDate);

        return jsonObject;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}

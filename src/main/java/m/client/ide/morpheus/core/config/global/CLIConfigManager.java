package m.client.ide.morpheus.core.config.global;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.config.AbstractJasonFileManager;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.File;

public class CLIConfigManager extends AbstractJasonFileManager {

    private CLIInfo info = new CLIInfo();

    private static CLIConfigManager instance = new CLIConfigManager();

    public static CLIConfigManager getInstance() {
        return instance;
    }

    private CLIConfigManager() {
        try {
            filePath = Const.USER_HOME + File.separator + ".morpheus/config.json";
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage());
        }
    }

    public void loadJsonString(String jsonString) throws ParseException {
        info.clear();

        JSONParser sp = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object jsonObject = sp.parse(jsonString);
        JSONObject json = (JSONObject) jsonObject;

        info.setNexusBaseUrl((String)json.get(CLIInfo.key_nexusBaseUrl));
        info.setGiteaBaseUrl((String)json.get(CLIInfo.key_giteaBaseUrl));
        info.setGiteaTemplateOrg((String)json.get(CLIInfo.key_giteaTemplateOrg));
        info.setNpmClient((String)json.get(CLIInfo.key_npmClient));
    }

    public void clear() {
        info.clear();
    }

    @SuppressWarnings("unchecked")
    public String getJSONString() {
        StringBuffer sb = new StringBuffer();

        JSONObject object = new JSONObject();
        object.put(CLIInfo.key_nexusBaseUrl, info.getNexusBaseUrl());
        object.put(CLIInfo.key_giteaBaseUrl, info.getGiteaBaseUrl());
        object.put(CLIInfo.key_giteaTemplateOrg, info.getGiteaTemplateOrg());
        object.put(CLIInfo.key_npmClient, info.getNpmClient());
        sb.append("   " + object.toJSONString(JSONStyle.LT_COMPRESS)).append("\n");

        return sb.toString();
    }

    public CLIInfo getCLIInfo() {
        return info;
    }

    public void update(CLIInfo info) {
        this.info.update(info);
    }

    public String getNexusBaseUrl() {
        return info.getNexusBaseUrl();
    }

    public void setNexusBaseUrl(String nexusBaseUrl) {
        info.setNexusBaseUrl(nexusBaseUrl);
    }

    public String getGiteaBaseUrl() {
        return info.getGiteaBaseUrl();
    }

    public void setGiteaBaseUrl(String giteaBaseUrl) {
        info.setGiteaBaseUrl(giteaBaseUrl);
    }

    public String getGiteaTemplateOrg() {
        return info.getGiteaTemplateOrg();
    }

    public void setGiteaTemplateOrg(String giteaTemplateOrg) {
        info.setGiteaTemplateOrg(giteaTemplateOrg);
    }

    public String getNpmClient() {
        return info.getNpmClient();
    }

    public void setNpmClient(String npmClient) {
        info.setNpmClient(npmClient);
    }

    @Override
    public String toString() {
        return info.toString();
    }
}

package m.client.ide.morpheus.core.config.global;

import m.client.ide.morpheus.core.config.webserver.MappingInfo;

public class CLIInfo {
    static final String key_nexusBaseUrl = "nexusBaseUrl";
    static final String key_giteaBaseUrl = "giteaBaseUrl";
    static final String key_giteaTemplateOrg = "giteaTemplateOrg";
    static final String key_npmClient = "npmClient";

    private static final String nexusBaseUrl_default = "https://nexus.dev.morpheus.kr";
    private static final String giteaBaseUrl_default = "https://gitea.dev.morpheus.kr/api/v1";
    private static final String giteaTemplateOrg_default = "morpheus-template";
    private static final String npmClient_default = "npm";

    private String nexusBaseUrl = nexusBaseUrl_default;
    private String giteaBaseUrl = giteaBaseUrl_default;
    private String giteaTemplateOrg = giteaTemplateOrg_default;
    private String npmClient = npmClient_default;

    public CLIInfo() {
        this(nexusBaseUrl_default, giteaBaseUrl_default, giteaTemplateOrg_default, npmClient_default);
    }

    public CLIInfo(String nexusBaseUrl, String giteaBaseUrl, String giteaTemplateOrg, String npmClient) {
        this.nexusBaseUrl = nexusBaseUrl;
        this.giteaBaseUrl = giteaBaseUrl;
        this.giteaTemplateOrg = giteaTemplateOrg;
        this.npmClient = npmClient;
    }

    public String getNexusBaseUrl() {
        return nexusBaseUrl;
    }

    public void setNexusBaseUrl(String nexusBaseUrl) {
        this.nexusBaseUrl = nexusBaseUrl;
    }

    public String getGiteaBaseUrl() {
        return giteaBaseUrl;
    }

    public void setGiteaBaseUrl(String giteaBaseUrl) {
        this.giteaBaseUrl = giteaBaseUrl;
    }

    public String getGiteaTemplateOrg() {
        return giteaTemplateOrg;
    }

    public void setGiteaTemplateOrg(String giteaTemplateOrg) {
        this.giteaTemplateOrg = giteaTemplateOrg;
    }

    public String getNpmClient() {
        return npmClient;
    }

    public void setNpmClient(String npmClient) {
        this.npmClient = npmClient;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(key_nexusBaseUrl).append(" = ").append(nexusBaseUrl).append("\n");
        sb.append(key_giteaBaseUrl).append(" = ").append(giteaBaseUrl).append("\n");
        sb.append(key_giteaTemplateOrg).append(" = ").append(giteaTemplateOrg).append("\n");
        sb.append(key_npmClient).append(" = ").append(npmClient).append("\n");

        return sb.toString();
    }

    public void clear() {
        this.nexusBaseUrl = "";
        this.giteaBaseUrl = "";
        this.giteaTemplateOrg = "";
        this.npmClient = "";
    }

    public void setDefault() {
        this.nexusBaseUrl = nexusBaseUrl_default;
        this.giteaBaseUrl = giteaBaseUrl_default;
        this.giteaTemplateOrg = giteaTemplateOrg_default;
        this.npmClient = npmClient_default;
    }

    public void update(CLIInfo info) {
        this.nexusBaseUrl = info.nexusBaseUrl;
        this.giteaBaseUrl = info.giteaBaseUrl;
        this.giteaTemplateOrg = info.getGiteaTemplateOrg();
        this.npmClient = info.npmClient;
    }
}

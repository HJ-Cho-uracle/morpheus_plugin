package m.client.ide.morpheus.framework.cli.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MorpheusProjectInfo {
    static final @NotNull String key_projectName = "projectName";
    static final @NotNull String key_applicationId = "applicationId";
    static final @NotNull String key_androidAppName = "androidAppName";
    static final @NotNull String key_androidPackageName = "androidPackageName";
    static final @NotNull String key_iosAppName = "iosAppName";
    static final @NotNull String key_iosBundleId = "iosBundleId";
    static final @Nullable String key_webRootDir = "webRootDir";
    static final @Nullable String key_androidRootDir = "androidRootDir";
    static final @Nullable String key_iosRootDir = "iosRootDir";
    static final @Nullable String key_legacyJsOutPut = "legacyJsOutPut";

    private @NotNull String projectName;
    private @NotNull String applicationId;
    private @NotNull String androidAppName;
    private @NotNull String androidPackageName;
    private @NotNull String iosAppName;
    private @NotNull String iosBundleId;
    private @Nullable String webRootDir;
    private @Nullable String androidRootDir;
    private @Nullable String iosRootDir;
    private @Nullable String legacyJsOutPut;

    public MorpheusProjectInfo() {
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");

        sb.append(key_projectName).append(" = ").append(projectName).append("\n");
        sb.append(key_applicationId).append(" = ").append(applicationId).append("\n");
        sb.append(key_androidAppName).append(" = ").append(androidAppName).append("\n");
        sb.append(key_androidPackageName).append(" = ").append(androidPackageName).append("\n");
        sb.append(key_iosAppName).append(" = ").append(iosAppName).append("\n");
        sb.append(key_iosBundleId).append(" = ").append(iosBundleId).append("\n");
        sb.append(key_webRootDir).append(" = ").append(webRootDir).append("\n");
        sb.append(key_androidRootDir).append(" = ").append(androidRootDir).append("\n");
        sb.append(key_iosRootDir).append(" = ").append(iosRootDir).append("\n");
        sb.append(key_legacyJsOutPut).append(" = ").append(legacyJsOutPut).append("\n");

        return sb.toString();
    }

    public void clear() {
        projectName = "";
        applicationId = "";
        androidAppName = "";
        androidPackageName = "";
        iosAppName = "";
        iosBundleId = "";
        webRootDir = "";
        androidRootDir = "";
        iosRootDir = "";
        legacyJsOutPut = "";
    }

    public void update(MorpheusProjectInfo info) {
        projectName = info.projectName;
        applicationId = info.applicationId;
        androidAppName = info.androidAppName;
        androidPackageName = info.androidPackageName;
        iosAppName = info.iosAppName;
        iosBundleId = info.iosBundleId;
        webRootDir = info.webRootDir;
        androidRootDir = info.androidRootDir;
        iosRootDir = info.iosRootDir;
        legacyJsOutPut = info.legacyJsOutPut;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAndroidAppName() {
        return androidAppName;
    }

    public void setAndroidAppName(String androidAppName) {
        this.androidAppName = androidAppName;
    }

    public String getAndroidPackageName() {
        return androidPackageName;
    }

    public void setAndroidPackageName(String androidPackageName) {
        this.androidPackageName = androidPackageName;
    }

    public String getIosAppName() {
        return iosAppName;
    }

    public void setIosAppName(String iosAppName) {
        this.iosAppName = iosAppName;
    }

    public String getIosBundleId() {
        return iosBundleId;
    }

    public void setIosBundleId(String iosBundleId) {
        this.iosBundleId = iosBundleId;
    }

    public String getWebRootDir() {
        return webRootDir;
    }

    public void setWebRootDir(String webRootDir) {
        this.webRootDir = webRootDir;
    }

    public String getAndroidRootDir() {
        return androidRootDir;
    }

    public void setAndroidRootDir(String androidRootDir) {
        this.androidRootDir = androidRootDir;
    }

    public String getIosRootDir() {
        return iosRootDir;
    }

    public void setIosRootDir(String iosRootDir) {
        this.iosRootDir = iosRootDir;
    }

    public String getLegacyJsOutPut() {
        return legacyJsOutPut;
    }

    public void setLegacyJsOutPut(String legacyJsOutPut) {
        this.legacyJsOutPut = legacyJsOutPut;
    }
}

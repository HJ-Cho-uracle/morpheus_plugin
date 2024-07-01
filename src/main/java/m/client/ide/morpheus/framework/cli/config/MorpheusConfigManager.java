package m.client.ide.morpheus.framework.cli.config;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import m.client.ide.morpheus.core.config.AbstractJasonFileManager;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import m.client.ide.morpheus.framework.eclipse.AppXMLManager;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class MorpheusConfigManager extends AbstractJasonFileManager {

    private MorpheusProjectInfo info;
    private Project project;

    public static boolean isMorpheusProject(@NotNull Project project) {
        File configFile = FileUtil.getChildFile(project, NpmConstants.CONFIG_JSON_FILE);
        if (!configFile.exists()) {
            return false;
        }

        MorpheusConfigManager configManager = new MorpheusConfigManager(project);
        return configManager.filePath != null && !configManager.filePath.isEmpty();
    }

    public static @NotNull MorpheusConfigManager makeMorpheusConfigFile(@NotNull Project project, @NotNull AppXMLManager appXMLmanager) {
        File configFile = new File(project.getBasePath(), NpmConstants.CONFIG_JSON_FILE);

        return makeMorpheusConfigFile(configFile, appXMLmanager);
    }

    public static @NotNull MorpheusConfigManager makeMorpheusConfigFile(File configFile, @NotNull AppXMLManager appXMLmanager) {
        MorpheusConfigManager configManager = new MorpheusConfigManager(configFile);
        try {
            configManager.init(configFile);

            configManager.info = new MorpheusProjectInfo();
            configManager.info.setProjectName(appXMLmanager.getProjectName());
            configManager.info.setApplicationId(appXMLmanager.getApplicationId());
            configManager.info.setAndroidAppName(appXMLmanager.getAndroidAppName());
            configManager.info.setAndroidPackageName(appXMLmanager.getAndroidPackageName());
            configManager.info.setIosAppName(appXMLmanager.getIosAppName());
            configManager.info.setIosBundleId(appXMLmanager.getIosBundleId());
            configManager.saveToFile();
        } catch (IOException | ParseException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        }

        return configManager;
    }

    private void setProject(Project project) {
        this.project = project;
    }

    private Project getProject() {
        return project;
    }

    public MorpheusConfigManager() {
        this(ProjectManager.getInstance().getDefaultProject());
    }

    public MorpheusConfigManager(Project project) {
        this(FileUtil.getChildFile(project, NpmConstants.CONFIG_JSON_FILE));
    }

    public MorpheusConfigManager(File configFile) {
        try {
            filePath = configFile != null && configFile.exists() && configFile.isFile() ? configFile.getAbsolutePath() : "";
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage());
        }
    }

    @Override
    protected String makeJsonString() {
        StringBuffer sb = new StringBuffer();

        JSONObject object = new JSONObject();
        object.put(MorpheusProjectInfo.key_projectName, info.getProjectName());
        object.put(MorpheusProjectInfo.key_applicationId, info.getApplicationId());
        object.put(MorpheusProjectInfo.key_androidAppName, info.getAndroidAppName());
        object.put(MorpheusProjectInfo.key_androidPackageName, info.getAndroidPackageName());
        object.put(MorpheusProjectInfo.key_iosAppName, info.getIosAppName());
        object.put(MorpheusProjectInfo.key_iosBundleId, info.getIosBundleId());

        String value = info.getWebRootDir();
        if (value != null && !value.isEmpty()) {
            object.put(MorpheusProjectInfo.key_webRootDir, info.getWebRootDir());
        }
        value = info.getAndroidRootDir();
        if (value != null && !value.isEmpty()) {
            object.put(MorpheusProjectInfo.key_androidRootDir, info.getAndroidRootDir());
        }
        value = info.getIosRootDir();
        if (value != null && !value.isEmpty()) {
            object.put(MorpheusProjectInfo.key_iosRootDir, info.getIosRootDir());
        }
        value = info.getLegacyJsOutPut();
        if (value != null && !value.isEmpty()) {
            object.put(MorpheusProjectInfo.key_legacyJsOutPut, info.getLegacyJsOutPut());
        }

        sb.append("   " + object.toJSONString(JSONStyle.LT_COMPRESS)).append("\n");

        return sb.toString();
    }

    @Override
    public void loadJsonString(String jsonString) throws ParseException {
        JSONParser sp = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object jsonObject = sp.parse(jsonString);
        JSONObject json = (JSONObject) jsonObject;

        info = new MorpheusProjectInfo();
        info.setProjectName((String) json.get(MorpheusProjectInfo.key_projectName));
        info.setApplicationId((String) json.get(MorpheusProjectInfo.key_applicationId));
        info.setAndroidAppName((String) json.get(MorpheusProjectInfo.key_androidAppName));
        info.setAndroidPackageName((String) json.get(MorpheusProjectInfo.key_androidPackageName));
        info.setIosAppName((String) json.get(MorpheusProjectInfo.key_iosAppName));
        info.setIosBundleId((String) json.get(MorpheusProjectInfo.key_iosBundleId));
        info.setWebRootDir((String) json.get(MorpheusProjectInfo.key_webRootDir));
        info.setAndroidRootDir((String) json.get(MorpheusProjectInfo.key_androidRootDir));
        info.setIosRootDir((String) json.get(MorpheusProjectInfo.key_iosRootDir));
        info.setLegacyJsOutPut((String) json.get(MorpheusProjectInfo.key_legacyJsOutPut));
    }

    public MorpheusProjectInfo getInfo() {
        return info;
    }

    public String getAndroidAppId() {
        return info == null ? "" : info.getApplicationId();
    }

    public String getIOSAppName() {
        return info == null ? "" : info.getIosAppName();
    }

    public String getBundleId() {
        return info == null ? "" : info.getIosBundleId();
    }
}

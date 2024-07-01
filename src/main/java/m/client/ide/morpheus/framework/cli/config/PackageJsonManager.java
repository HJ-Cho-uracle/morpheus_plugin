package m.client.ide.morpheus.framework.cli.config;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.config.AbstractJasonFileManager;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackageJsonManager extends AbstractJasonFileManager {

    private PackageJsonInfo info;

    public PackageJsonManager(@Nullable @SystemIndependent @NonNls String baseDir) {
        try {
            @Nullable File configFile = CommonUtil.getPathFile(baseDir, NpmConstants.PACKAGE_FILE);
            filePath = configFile != null && configFile.exists() && configFile.isFile() ? configFile.getAbsolutePath() : "";
            init();
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage());
        }
    }

    @Override
    protected String makeJsonString() {
        StringBuffer sb = new StringBuffer();

        JSONObject object = new JSONObject();
        object.put(PackageJsonInfo.key_name, info.getProjectName());
        object.put(PackageJsonInfo.key_private, info.isPrivate());
        object.put(PackageJsonInfo.key_version, info.getVersion());
        object.put(PackageJsonInfo.key_description, info.getDescription());
        object.put(PackageJsonInfo.key_scripts, makeScripts(info.getScripts()));
        object.put(PackageJsonInfo.key_keywords, makeJsonArray(info.getKeywords()));
        object.put(PackageJsonInfo.key_dependencies, info.getDependencies());
        object.put(PackageJsonInfo.key_devDependencies, info.getDevDependencies());
        object.put(PackageJsonInfo.key_author, info.getAuthor());
        object.put(PackageJsonInfo.key_license, info.getLicense());

        sb.append("   " + object.toJSONString(JSONStyle.NO_COMPRESS)).append("\n");

        return sb.toString();
    }

    private @NotNull JSONArray makeJsonArray(List<String> keywords) {
        JSONArray array = new JSONArray();
        if (keywords != null) {
            for (int i = 0; i < keywords.size(); i++) {
                array.add(keywords.get(i));
            }
        }
        return array;
    }

    private @NotNull JSONObject makeScripts(PackageJsonInfo.ScriptsInfo scripts) {
        JSONObject object = new JSONObject();
        if (scripts != null) {
            object.put(PackageJsonInfo.key_sync, scripts.sync);
            object.put(PackageJsonInfo.key_postinstall, scripts.postInstall);
        }
        return object;
    }

    @Override
    public void loadJsonString(String jsonString) throws ParseException {
        JSONParser sp = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object jsonObject = sp.parse(jsonString);
        JSONObject json = (JSONObject) jsonObject;

        info = new PackageJsonInfo();
        info.setProjectName((String) json.get(PackageJsonInfo.key_name));
        info.setPrivate((Boolean) json.get(PackageJsonInfo.key_private));
        info.setVersion((String) json.get(PackageJsonInfo.key_version));
        info.setDescription((String) json.get(PackageJsonInfo.key_description));
        info.setScripts(loadScripts((JSONObject) json.get(PackageJsonInfo.key_scripts)));
        info.setKeywords(loadArray((JSONArray) json.get(PackageJsonInfo.key_keywords)));
        info.setDependencies(loadDependencies(json.get(PackageJsonInfo.key_dependencies)));
        info.setDevDependencies(json.get(PackageJsonInfo.key_devDependencies));
        info.setAuthor((String) json.get(PackageJsonInfo.key_author));
        info.setLicense((String) json.get(PackageJsonInfo.key_license));
    }

    private PackageJsonInfo.@NotNull ScriptsInfo loadScripts(@NotNull JSONObject jsonObject) {
        String sync = (String) jsonObject.get(PackageJsonInfo.key_sync);
        String postinstall = (String) jsonObject.get(PackageJsonInfo.key_postinstall);

        return new PackageJsonInfo.ScriptsInfo(sync, postinstall);
    }

    private @NotNull List<String> loadArray(@NotNull JSONArray array) {
        List<String> keywords = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            keywords.add((String) array.get(i));
        }

        return keywords;
    }

    @Contract(pure = true)
    private @Nullable Object loadDependencies(Object o) {
        return new JSONObject();
    }

    public PackageJsonInfo getInfo() {
        return info;
    }

    public static PackageJsonInfo makePackageJsonInfo(@Nullable @SystemIndependent @NonNls String baseDir) {
        PackageJsonManager packageManager = new PackageJsonManager(baseDir);
        File configFile = new File(baseDir, NpmConstants.PACKAGE_FILE);
        try {
            packageManager.init(configFile);
            if (packageManager.info == null) {
                packageManager.info = new PackageJsonInfo(baseDir);
            }
            packageManager.saveToFile();
        } catch (IOException | ParseException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        }

        return packageManager.getInfo();
    }

    private void setPackageInfo(PackageJsonInfo packageJsonInfo) {
        this.info = packageJsonInfo;
    }
}

package m.client.ide.morpheus.core.config.global;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NpmRCFileManager {
    private static final String file_name = OSUtil.isMac() ? Const.USER_HOME + File.separator + NpmConstants.NPMRC_FILE : "";

    public static final String KEY_REGISTRY = "@morpheus:registry";
    private static final String URL_NEXUS_BASE = "https://nexus.dev.morpheus.kr";

    private static final String URL_REPOSITORY_NPM = "/repository/npm";


    public String getNexusUrl() {
        return nexusUrl;
    }

    public void setNexusUrl(String nexusUrl) {
        this.nexusUrl = nexusUrl;
    }

    private String nexusUrl;

    public NpmRCFileManager() {
        this(URL_NEXUS_BASE);
    }

    public NpmRCFileManager(@NotNull String nexusUrl) {
        this.nexusUrl = nexusUrl.isEmpty() ? URL_NEXUS_BASE : nexusUrl;
    }

    public void createNpmRCFile() {
        createNpmRCFile(file_name, this.nexusUrl);
    }

    public static void createNpmRCFile(String rcFilePath) {
        createNpmRCFile(rcFilePath, URL_NEXUS_BASE);
    }

    public static void createNpmRCFile(String rcFilePath, String nexusUrl) {
        if(rcFilePath == null || rcFilePath.isEmpty()) {
            CommonUtil.log(Log.LEVEL_DEBUG, "Invalid file path for '.npmrc' : " + rcFilePath); // $NON-NLS-1#
            return;
        }

        File rcFile = new File(rcFilePath);
        if (rcFile.exists()) {
            rcFile.delete();
        }

        FileOutputStream output = null;
        try {
            CommonUtil.log(Log.LEVEL_DEBUG, "Create NpmRCFile : " + rcFile.getAbsolutePath()); // $NON-NLS-1#
            output = FileUtil.openOutputStream(rcFile, false);
            String morpheusRegistry = KEY_REGISTRY + "=" + nexusUrl + URL_REPOSITORY_NPM;
            output.write(morpheusRegistry.getBytes());
            output.flush();
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "Create NpmRCFile! : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}

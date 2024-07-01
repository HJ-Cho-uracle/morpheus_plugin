package m.client.ide.morpheus.framework.eclipse.library;

import m.client.ide.morpheus.core.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

public class AndroidLibraryConfig {
    private static final String TAG_APPLICATION = "application";
    private static final String TAG_FILE = "file";
    private static final String TAG_PATH = "path";

    private static final String ATTR_ID = "id";
    private static final String ATTR_VERSIOIN = "version";
    private static final String ATTR_NAME = "name";

    private static final Object ID_FRAEMWORK = "Frameworks";
    private static final Object ID_PROJECT = "Project";
    private static final Object ID_BUNDLES = "Bundles";
    private static final Object ID_3RD_BUNDLE = "3rd_Bundles";

    private static final String FD_SRC = "src";
    private static final String FD_FRAMEWORK_END = ".framework";
    private static final String FD_XCFRAMEWORK_END = ".xcframework";

    protected static final Object EXT_FRAMEWORK = "framework";
    protected static final Object EXT_XCFRAMEWORK = "xcframework";
    protected static final Object EXT_BUNDLE = "bundle";

    private String version;
    private ArrayList<String> frameworks;
    private Map<String, List<File>> srcMap;

    private File[] files;
    private File[] fThirdPartBundles;

    public AndroidLibraryConfig() {
        this.frameworks = new ArrayList<String>();
        this.srcMap = new HashMap<String, List<File>>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(ArrayList<String> frameworks) {
        this.frameworks = frameworks;
    }

    public Map<String, List<File>> getSrcMap() {
        return srcMap;
    }

    public void setSrcMap(Map<String, List<File>> srcMap) {
        this.srcMap = srcMap;
    }


    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public File[] getThirdPartBundle() {
        return fThirdPartBundles;
    }

    public void setThirdPartBundle(File[] fThirdPartBundles) {
        this.fThirdPartBundles = fThirdPartBundles;
    }

    public void parse(@NotNull File libConfigFile) {
        File parent = libConfigFile.getParentFile();
        Document doc = XMLUtil.getDocument(libConfigFile);
        if (doc == null) {
            return;
        }
        Element root = doc.getDocumentElement();
        parse(root);
    }

    private void parse(Element configRoot) {
        Element configApplicationElement = XMLUtil.getElementByName(configRoot, "application");
        if(configApplicationElement != null) {

        }
        Element configManifestElement = XMLUtil.getElementByName(configRoot, "manifest");
    }

    private void getBundlesFile(@NotNull File file, ArrayList<File> bundles) {
        if (file.isDirectory()) {
            if (file.getName().endsWith(FD_FRAMEWORK_END) ||
                    file.getName().endsWith(FD_XCFRAMEWORK_END)) {
                bundles.add(file);
            } else {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File f : listFiles) {
                        getBundlesFile(f, bundles);
                    }
                }
            }
        } else {
            bundles.add(file);
        }
    }
}

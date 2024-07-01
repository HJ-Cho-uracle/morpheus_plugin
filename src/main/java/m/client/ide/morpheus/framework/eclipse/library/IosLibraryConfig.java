package m.client.ide.morpheus.framework.eclipse.library;

import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.XMLUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;

public class IosLibraryConfig {
    private static final String TAG_ROOT = "config";
    private static final String TAG_IOS = "ios";
    private static final String TAG_GROUP = "group";
    private static final String TAG_FILE = "file";
    private static final String TAG_PATH = "path";

    private static final String ATTR_ID = "id";
    private static final String ATTR_VERSIOIN = "version";
    private static final String ATTR_NAME = "name";

    private static final String ID_FRAEMWORK = "Frameworks";
    private static final String ID_PROJECT = "Project";
    private static final String ID_BUNDLES = "Bundles";
    private static final String ID_3RD_BUNDLE = "3rd_Bundles";

    private static final String FD_SRC = "src";
    private static final String FD_FRAMEWORK_END = ".framework";
    private static final String FD_XCFRAMEWORK_END = ".xcframework";

    protected static final String EXT_FRAMEWORK = "framework";
    protected static final String EXT_XCFRAMEWORK = "xcframework";
    protected static final String EXT_BUNDLE = "bundle";

    private String version;
    private ArrayList<String> frameworks;
    private Map<String, List<String>> srcMap;

    private List<String> files;
    private List<String> fThirdPartBundles;

    public IosLibraryConfig() {
        this.frameworks = new ArrayList<>();
        this.srcMap = new HashMap<>();
    }

    public IosLibraryConfig(Element element) {
        parse(element);
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

    public Map<String, List<String>> getSrcMap() {
        return srcMap;
    }

    public void setSrcMap(Map<String, List<String>> srcMap) {
        this.srcMap = srcMap;
    }


    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getThirdPartBundle() {
        return fThirdPartBundles;
    }

    public void setThirdPartBundle(List<String> fThirdPartBundles) {
        this.fThirdPartBundles = fThirdPartBundles;
    }

    public void parseFromDir(@NotNull File iosDir) {
        File libConfigFile = new File(iosDir, FrameworkConstants.LIBRARY_CONFIG_FILE);
        if (!libConfigFile.exists()) {
            return;
        }
        Document doc = XMLUtil.getDocument(libConfigFile);
        if (doc == null) {
            return;
        }
        Element root = doc.getDocumentElement();
        String version = root.getAttribute(ATTR_VERSIOIN);

        ArrayList<String> bundles = new ArrayList<>();
        ArrayList<String> frameworks = new ArrayList<>();
        Map<String, List<String>> srcMap = new HashMap<>();

        NodeList groupNodeList = root.getElementsByTagName(TAG_GROUP);

        for (int i = 0; i < groupNodeList.getLength(); i++) {
            Element groupElement = (Element) groupNodeList.item(i);

            String id = groupElement.getAttribute(ATTR_ID);
            if (id.equals(ID_FRAEMWORK)) {
                NodeList fileNodeList = groupElement.getElementsByTagName(TAG_FILE);
                for (int j = 0; j < fileNodeList.getLength(); j++) {
                    Element fileElement = (Element) fileNodeList.item(j);
                    frameworks.add(fileElement.getTextContent());
                }

            } else if (id.equals(ID_PROJECT)) {
                NodeList pathNodeList = groupElement.getElementsByTagName(TAG_PATH);
                for (int j = 0; j < pathNodeList.getLength(); j++) {
                    Element pathElement = (Element) pathNodeList.item(j);
                    String pathName = pathElement.getAttribute(ATTR_NAME);

                    List<String> srcList = new ArrayList<>();
                    NodeList srcFileList = pathElement.getElementsByTagName(TAG_FILE);
                    for (int k = 0; k < srcFileList.getLength(); k++) {
                        Element srcFile = (Element) srcFileList.item(k);

                        File srcF = new File(iosDir, FD_SRC + pathName + File.separator + srcFile.getTextContent());
                        if (srcF.exists()) {
                            srcList.add(srcFile.getTextContent());
                        }

                    }
                    srcMap.put(pathName, srcList);
                }
            } else if (id.equals(ID_BUNDLES)) {
                NodeList fileNodeList = groupElement.getElementsByTagName(TAG_FILE);
                for (int j = 0; j < fileNodeList.getLength(); j++) {
                    Element fileElement = (Element) fileNodeList.item(j);
                    bundles.add(fileElement.getTextContent());
                }
            } else if (id.equals(ID_3RD_BUNDLE)) {
                ArrayList<File> fThirdPartBundles = new ArrayList<File>();
                NodeList fileNodeList = groupElement.getElementsByTagName(TAG_FILE);
                for (int j = 0; j < fileNodeList.getLength(); j++) {
                    Element fileElement = (Element) fileNodeList.item(j);
                    String path = fileElement.getTextContent();
                    File ef = new File(iosDir, path);
                    if (ef.exists()) {
                        getBundlesFile(ef, fThirdPartBundles);
                    }
                }

                Collections.sort(fThirdPartBundles, (o1, o2) -> {
                    String path1 = o1.getAbsolutePath();
                    String path2 = o2.getAbsolutePath();
                    return path1.compareTo(path2);
                });
                setThirdPartBundle(getRelativePaths(iosDir, fThirdPartBundles));
            }
        }

        setVersion(version);
        setFrameworks(frameworks);
        setSrcMap(srcMap);

        List<File> fList = new ArrayList<File>();
        File[] fF = iosDir.listFiles(pathname -> {
            String ext = FileUtil.getFileExtension(pathname);
            return ext.equalsIgnoreCase(EXT_FRAMEWORK) || ext.equalsIgnoreCase(EXT_XCFRAMEWORK);
        });

        for (File f1 : fF) {
            fList.add(f1);
        }

        if (bundles.size() < 1) {
            File[] fL = iosDir.listFiles(pathname -> {
                String ext = FileUtil.getFileExtension(pathname);
                return ext.equalsIgnoreCase(EXT_BUNDLE);
            });

            for (File f1 : fL) {
                fList.add(f1);
            }
        } else {
            for (String f : bundles) {
                File ef = new File(iosDir, f);
                if (ef.exists()) {
                    fList.add(ef);
                }
            }
        }

        setFiles(getRelativePaths(iosDir, fList));
    }

    private @NotNull List<String> getRelativePaths(File parent, @NotNull List<File> files) {
        List<String> relativePaths = new ArrayList<>();
        for (File file : files) {
            relativePaths.add(parent.toPath().relativize(file.toPath()).toString());
        }
        return relativePaths;
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

    public void parse(@NotNull Element root) {
        version = root.getAttribute(ATTR_VERSIOIN);

        @NotNull List<Element> groupElements = XMLUtil.getChildElementsByName(root, TAG_GROUP);
        for (Element groupElement : groupElements) {
            String id = groupElement.getAttribute(ATTR_ID);
            if (ID_PROJECT.equals(id)) {
                parseProjectGroup(groupElement);
            } else if (ID_FRAEMWORK.equals(id)) {
                parseFrameworkGroup(groupElement);
            } else if (ID_BUNDLES.equals(id)) {
                parseBundlesGroup(groupElement);
            } else if (ID_3RD_BUNDLE.equals(id)) {
                parse3RDBundlesGroup(groupElement);
            }
        }
    }

    private void parseFrameworkGroup(Element groupElement) {
        if (frameworks == null) {
            frameworks = new ArrayList<>();
        } else {
            frameworks.clear();
        }

        List<Element> fileElements = XMLUtil.getChildElementsByName(groupElement, TAG_FILE);
        for (Element fileElement : fileElements) {
            frameworks.add(fileElement.getTextContent());
        }
    }

    private void parseProjectGroup(Element groupElement) {
        if (srcMap == null) {
            srcMap = new HashMap<>();
        } else {
            srcMap.clear();
        }

        List<Element> pathElements = XMLUtil.getChildElementsByName(groupElement, TAG_PATH);
        for (Element pathElement : pathElements) {
            List<String> srcList = new ArrayList<>();
            String key = pathElement.getAttribute(ATTR_NAME);

            List<Element> fileElements = XMLUtil.getChildElementsByName(pathElement, TAG_FILE);
            for (Element fileElement : fileElements) {
                srcList.add(fileElement.getTextContent());
            }
            srcMap.put(key, srcList);
        }
    }

    private void parseBundlesGroup(Element groupElement) {
        if (files == null) {
            files = new ArrayList<>();
        } else {
            files.clear();
        }

        List<Element> fileElements = XMLUtil.getChildElementsByName(groupElement, TAG_FILE);
        for (Element fileElement : fileElements) {
            files.add(fileElement.getTextContent());
        }
    }

    private void parse3RDBundlesGroup(Element groupElement) {
        if (fThirdPartBundles == null) {
            fThirdPartBundles = new ArrayList<>();
        } else {
            fThirdPartBundles.clear();
        }

        List<Element> fileElements = XMLUtil.getChildElementsByName(groupElement, TAG_FILE);
        for (Element fileElement : fileElements) {
            fThirdPartBundles.add(fileElement.getTextContent());
        }
    }

    public Element toElement(Document doc, String tag) {
        if (tag == null || tag.isEmpty()) {
            tag = TAG_ROOT;
        }
        Element root = doc.createElement(tag);
        root.setAttribute(ATTR_VERSIOIN, version);

        appendFrameworkGroup(doc, root);
        appendProjectGroup(doc, root);
        appendBundlesGroup(doc, root);
        append3RDBundlesGroup(doc, root);

        return root;
    }

    private void appendFrameworkGroup(Document doc, Element root) {
        if (frameworks != null && frameworks.size() > 0) {
            Element groupElement = doc.createElement(TAG_GROUP);
            groupElement.setAttribute(ATTR_ID, ID_FRAEMWORK);
            root.appendChild(groupElement);

            for (String framework : frameworks) {
                Element fileElement = doc.createElement(TAG_FILE);
                fileElement.setTextContent(framework);
                groupElement.appendChild(fileElement);
            }
        }
    }

    private void appendProjectGroup(Document doc, Element root) {
        if (srcMap == null || srcMap.size() == 0) {
            return;
        }

        Element groupElement = doc.createElement(TAG_GROUP);
        groupElement.setAttribute(ATTR_ID, ID_PROJECT);
        root.appendChild(groupElement);

        Set<String> keySet = srcMap.keySet();
        for (String key : keySet) {
            List<String> srcList = srcMap.get(key);
            if (srcList == null || srcList.size() == 0) {
                continue;
            }

            Element pathElement = doc.createElement(TAG_PATH);
            pathElement.setAttribute(ATTR_NAME, key);
            groupElement.appendChild(pathElement);
            for (String src : srcList) {
                Element srcFile = doc.createElement(TAG_FILE);
                srcFile.setTextContent(src);
                pathElement.appendChild(srcFile);
            }
        }
    }

    private void appendBundlesGroup(Document doc, Element root) {
        if (files == null || files.size() == 0) {
            return;
        }

        Element groupElement = doc.createElement(TAG_GROUP);
        groupElement.setAttribute(ATTR_ID, ID_BUNDLES);
        root.appendChild(groupElement);

        for (String file : files) {
            Element fileElement = doc.createElement(TAG_FILE);
            fileElement.setTextContent(file);
            groupElement.appendChild(fileElement);
        }
    }

    private void append3RDBundlesGroup(Document doc, Element root) {
        if (fThirdPartBundles == null || fThirdPartBundles.size() == 0) {
            return;
        }

        Element groupElement = doc.createElement(TAG_GROUP);
        groupElement.setAttribute(ATTR_ID, ID_3RD_BUNDLE);
        root.appendChild(groupElement);

        for (String file : fThirdPartBundles) {
            Element fileElement = doc.createElement(TAG_FILE);
            fileElement.setTextContent(file);
            groupElement.appendChild(fileElement);
        }
    }
}

package m.client.ide.morpheus.framework.eclipse.library;

import com.intellij.openapi.progress.ProgressIndicator;
import com.twelvemonkeys.io.FileUtil;
import m.client.ide.morpheus.core.resource.LibraryType;
import m.client.ide.morpheus.core.resource.ResourcesConstants;
import m.client.ide.morpheus.core.resource.TagType;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.XMLUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.framework.eclipse.SVNUtil;
import m.client.ide.morpheus.framework.eclipse.Version;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import com.esotericsoftware.minlog.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmatesoft.svn.core.SVNURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class UnapplyLibraryManager {
    private static UnapplyLibraryManager instance;
    private Hashtable<String, Hashtable<String, Library>> libraries = new Hashtable<>();

    public static UnapplyLibraryManager getInstance() {
        if (instance == null) {
            instance = new UnapplyLibraryManager();
        }
        return instance;
    }

    private UnapplyLibraryManager() {
        loadUnapplyLibraryInfo();
    }

    public UnapplyLibraryManager(ProgressIndicator indicator) {
        loadLibrariesFromResource(indicator);
    }

    private void loadUnapplyLibraryInfo() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("import/" +
                ResourcesConstants.LIBRARY_UNAPPLY_FILE_NAME);

        if (inputStream != null) {
            Document doc = XMLUtil.getDocument(inputStream);
            Element root = doc.getDocumentElement();

            NodeList libraryNodeList = root.getChildNodes();
            for (int i = 0; i < libraryNodeList.getLength(); i++) {
                Node childNode = libraryNodeList.item(i);
                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element element = (Element) childNode;
                String libraryType = element.getTagName();
                Hashtable<String, Library> libraryTable = libraries.get(libraryType);
                if (libraryTable == null) {
                    libraryTable = new Hashtable<>();
                    libraries.put(libraryType, libraryTable);
                }

                @NotNull List<Element> libraryList = XMLUtil.getChildElementsByName(element, "library");
                for (Element libraryElement : libraryList) {
                    Library library = new Library(libraryElement);
                    libraryTable.put(library.getKey(), library);
                }
            }
        }
    }

    public void writeUnapplyLibraryInfo() {
        File tempFolder = new File(CommonUtil.getAppDataLocation(), "temp");
        writeUnapplyLibraryInfo(tempFolder.getAbsolutePath());
    }

    public void writeUnapplyLibraryInfo(String location) {
        @Nullable Document doc = XMLUtil.getNewDocument();
        Element root = doc.createElement("unapplyinfo");
        doc.appendChild(root);

        Enumeration<String> types = libraries.keys();
        while (types.hasMoreElements()) {
            String type = types.nextElement();
            Hashtable<String, Library> libraryTable = libraries.get(type);
            if (libraryTable == null || libraryTable.size() == 0) {
                continue;
            }

            Element typeElement = doc.createElement(type);
            Enumeration<String> ids = libraryTable.keys();
            while (ids.hasMoreElements()) {
                Library library = libraryTable.get(ids.nextElement());
                typeElement.appendChild(library.toDocument(doc));
            }
            root.appendChild(typeElement);
        }

        if (location == null || location.isEmpty()) {
            location = CommonUtil.getPathString(CommonUtil.getAppDataLocation(), "temp");
        }
        File file = new File(location, ResourcesConstants.LIBRARY_UNAPPLY_FILE_NAME);

        String contents = XMLUtil.writeXMLString(doc);
        try {
            FileUtil.write(file, contents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    public void loadLibrariesFromResource(@NotNull ProgressIndicator indicator) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("import/" +
                ResourcesConstants.LIBRARY_LIST_CONFIG_FILE_NAME);

        if (indicator != null) {
            indicator.setText(FrameworkMessages.get(FrameworkMessages.loadUnapplyInfo));
        }
        if (inputStream != null) {
            Document doc = XMLUtil.getDocument(inputStream);
            Element root = doc.getDocumentElement();

            NodeList libraryNodeList = root.getElementsByTagName("library");
            for (int i = 0; i < libraryNodeList.getLength(); i++) {
                Element libraryElement = (Element) libraryNodeList.item(i);
                LibraryType libraryType = LibraryType.fromString(libraryElement.getAttribute("type"));
                Hashtable<String, Library> libraryTable = libraries.get(libraryType.toString());
                if (libraryTable == null) {
                    libraryTable = new Hashtable<>();
                    libraries.put(libraryType.toString(), libraryTable);
                }

                loadLibraries(indicator, libraryElement, libraryTable);
            }
            CommonUtil.log(Log.LEVEL_DEBUG, "loadLibrariesConfigFromResource complete!");
        }
    }

    private void loadLibraries(ProgressIndicator indicator, @NotNull Element libraryElement, Hashtable<String, Library> libraryTable) {
        String id = libraryElement.getAttribute("id");
        LibraryType libraryType = LibraryType.fromString(libraryElement.getAttribute("type"));
        Element apiElement = XMLUtil.getElementByName(libraryElement, "api");
        String api = apiElement.getTextContent().trim();

        if (indicator != null) {
            String loadLibrary = libraryType.toString() + " - " + id + "." + api;
            indicator.setText2(FrameworkMessages.get(FrameworkMessages.loadUnapplyLibrary, loadLibrary));
        }

        Element historyElement = XMLUtil.getElementByName(libraryElement, "history");
        if (historyElement != null) {
            String history = historyElement.getTextContent().trim();
            ByteArrayInputStream stream = new ByteArrayInputStream(history.getBytes(StandardCharsets.UTF_8));
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            try {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf('#') == 0) {
                        String[] tokens = line.split(" ");
                        if (tokens.length < 2) {
                            continue;
                        }

                        Version version = Version.valueOf(tokens[1]);
                        @Nullable Library library = getLibraryFromDownload(indicator, libraryType, id, api, version.getMicro() + "." + version.getQualifier());
                        if (library != null) {
                            libraryTable.put(library.getKey(), library);
                        }
                    }
                }
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    private @Nullable Library getLibraryFromDownload(ProgressIndicator indicator, LibraryType libraryType, String id, String api, String revision) throws UnknownHostException {
        String url = getResourceTag(libraryType, id, api, revision);
        SVNURL svnURL = SVNUtil.getSVNURL(TagType.LIBRARY, url);
        if (svnURL == null) {
            return null;
        }

        File tempFolder = new File(CommonUtil.getAppDataLocation(), "temp");
        File destDir = new File(tempFolder, CommonUtil.getPathString(id + "." + api));
        try {
            if (destDir.exists())
                FileUtils.deleteDirectory(destDir);

            if (indicator != null) {
                String loadLibrary = libraryType + " - " + id + "." + api + " export...";
                indicator.setText2(FrameworkMessages.get(FrameworkMessages.loadUnapplyLibrary, loadLibrary));
            }

            SVNUtil.export(svnURL, destDir, false);
            if (destDir.exists()) {
                return getLibraryFromDir(indicator, destDir);
            }
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        } finally {
            if (destDir.exists() && destDir.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(destDir);
                } catch (IOException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private @NotNull Library getLibraryFromDir(ProgressIndicator indicator, File libraryDir) {
        File libraryFile = new File(libraryDir, ResourcesConstants.LIBRARY_CONFIG_FILE_NAME);
        Library library = getLibraryFromConfig(indicator, libraryFile);

        File androidDir = new File(libraryDir, "android"); //$NON-NLS-1$ //$NON-NLS-2$
        if (androidDir.exists()) {
            File configFile = new File(androidDir, FrameworkConstants.LIBRARY_CONFIG_FILE);
            if (configFile.exists()) {
                try {
                    String androidConfig = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                    library.setAndroidConfig(androidConfig);
                } catch (IOException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
                }
            }

            File jinLibs = new File(androidDir, "jniLibs");
            if (jinLibs.exists()) {
                Collection<File> listfiles = FileUtils.listFiles(jinLibs, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                List<String> files = new ArrayList<>();
                for (File file : listfiles) {
                    files.add(jinLibs.toPath().relativize(file.toPath()).toString());
                }
                library.setJniLibFiles(files);
            }
            File resFolder = new File(androidDir, "resFolder");
            if (resFolder.exists()) {
                Collection<File> listfiles = FileUtils.listFiles(resFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                List<String> files = new ArrayList<>();
                for (File file : listfiles) {
                    files.add(resFolder.toPath().relativize(file.toPath()).toString());
                }
                library.setResFiles(files);
            }

            File file = new File(androidDir, "library.info");
            if (file.exists()) {
                try {
                    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                    library.setLibraryInfoLines(lines);
                } catch (IOException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
                }
            }

            File gradleDir = new File(androidDir, "gradle");
            if (gradleDir.exists() && gradleDir.isDirectory()) {
                Collection<File> listFiles = FileUtils.listFiles(gradleDir, new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return "gradle".equals(m.client.ide.morpheus.core.utils.FileUtil.getFileExtension(file));
                    }

                    @Override
                    public boolean accept(File file, String name) {
                        return "gradle".equals(m.client.ide.morpheus.core.utils.FileUtil.getFileExtension(file));
                    }
                }, TrueFileFilter.INSTANCE);
                List<String> gradleFiles = new ArrayList<>();
                for (File gradleFile : listFiles) {
                    gradleFiles.add(gradleDir.toPath().relativize(gradleFile.toPath()).toString());
                }
                library.setGradleFiles(gradleFiles);
            }
        }

        File iosDir = new File(libraryDir, "ios");
        if (iosDir.exists()) {
            IosLibraryConfig iosConfig = new IosLibraryConfig();
            iosConfig.parseFromDir(iosDir);
            library.setIOSConfig(iosConfig);
        }

        return library;
    }

    public String getResourceTag(@NotNull Library resource) {
        return getResourceTag(resource.getLibraryType(), resource.getId(), resource.getApi(), resource.getRevision());
    }

    public String getResourceTag(@NotNull LibraryType libraryType, String id, String api, String revision) {
        StringBuilder sb = new StringBuilder();
        sb.append("/");

        return sb.append(libraryType.toString()).append("@").append(id).append(".")
                .append(api).append(".").append(revision).toString();
    }

    private @NotNull Library getLibraryFromConfig(ProgressIndicator indicator, File configFile) {
        Document doc = XMLUtil.getDocument(configFile);
        return getLibraryFromConfig(indicator, doc.getDocumentElement());
    }

    private @NotNull Library getLibraryFromConfig(ProgressIndicator indicator, @NotNull Element libraryElement) {

        String id = libraryElement.getAttribute("id");
        LibraryType libraryType = LibraryType.fromString(libraryElement.getAttribute("type"));
        boolean isFree = true;
        String pay = libraryElement.getAttribute("free").trim();
        if (pay != null && !pay.equals("")) {
            isFree = Boolean.parseBoolean(pay);
        }

        Element apiElement = XMLUtil.getElementByName(libraryElement, "api");
        Element nameElement = XMLUtil.getElementByName(libraryElement, "name");
        Element descElement = XMLUtil.getElementByName(libraryElement, "description");
        Element revisionElement = XMLUtil.getElementByName(libraryElement, "revision");

        if (indicator != null) {
            String loadLibrary = libraryType + " - " + id + "." + apiElement.getTextContent().trim() + "." + revisionElement.getTextContent().trim();
            indicator.setText2(FrameworkMessages.get(FrameworkMessages.loadUnapplyLibrary, loadLibrary));
        }

        Element historyElement = XMLUtil.getElementByName(libraryElement, "history");

        Element optionElement = XMLUtil.getElementByName(libraryElement, "option");

        String option = null;
        if (optionElement != null) {
            option = optionElement.getTextContent();
        }

        Library library = new Library(id, nameElement.getTextContent().trim(), apiElement.getTextContent().trim(), libraryType, descElement.getTextContent()
                .trim(), revisionElement.getTextContent().trim(), historyElement.getTextContent().trim(), option);

        Element interfacesElement = XMLUtil.getElementByName(libraryElement, "interfaces");
        if (interfacesElement != null) {
            ArrayList<String> interfaceList = new ArrayList<String>();
            NodeList interfaceNodeList = interfacesElement.getElementsByTagName("interface");
            for (int i = 0; i < interfaceNodeList.getLength(); i++) {
                Element interfaceElement = (Element) interfaceNodeList.item(i);
                interfaceList.add(interfaceElement.getTextContent().trim());
            }
            library.setInterfaceList(interfaceList);
        }
        return library;
    }

    private @NotNull ArrayList<Library> loadInstalledLibraryList() {
        ArrayList<Library> libraryList = new ArrayList<>();
        File libraryHomeDir = new File(ResourcesConstants.LIBRARY_LOCATION);
        File[] typeDirs = libraryHomeDir.listFiles(new ResourceLibraryTypeDirectoryFilter());

        if (typeDirs != null) {
            for (File typeDir : typeDirs) {
                File[] libraryDirs = typeDir.listFiles(new ResourceDirectoryFilter(ResourcesConstants.LIBRARY_CONFIG_FILE_NAME));

                for (File libraryDir : libraryDirs) {
                    File configFile = new File(libraryDir, ResourcesConstants.LIBRARY_CONFIG_FILE_NAME);

                    libraryList.add(getLibraryFromConfig(null, configFile));
                }
            }
        }

        return libraryList;
    }

    public Library getLibrary(@NotNull LibraryType libraryType, String id, String api, String revision) {
        Hashtable<String, Library> typeLibraries = libraries.get(libraryType.toString());
        if (typeLibraries != null) {
            Library library = typeLibraries.get(id + "." + api + "." + revision);
            return library != null ? library : findLibrary(typeLibraries, id);
        }
        return null;
    }

    private @Nullable Library findLibrary(@NotNull Hashtable<String, Library> typeLibraries, String id) {
        List<String> keys = typeLibraries.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        for (String key : keys) {
            if (key.startsWith(id)) {
                return typeLibraries.get(key);
            }
        }

        return null;
    }

    public Library getUnapplyInfo(String libraryType, String key) {
        Hashtable<String, Library> table = libraries.get(libraryType);
        if (table != null) {
            return table.get(key);
        }
        return null;
    }
}

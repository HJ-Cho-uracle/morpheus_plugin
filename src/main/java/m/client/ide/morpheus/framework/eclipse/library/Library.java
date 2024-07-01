package m.client.ide.morpheus.framework.eclipse.library;

import m.client.ide.morpheus.core.resource.LibraryType;
import m.client.ide.morpheus.core.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private static final String TAG_LIBRARY = "library";
    private static final String ATTR_ID = "id";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_FREE = "free";
    private static final String TAG_API = "api";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_REVISION = "revision";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_INTERFACES = "interfaces";
    private static final String TAG_INTERFACE = "interface";
    private static final String TAG_OPTION = "option";
    private static final String TAG_JNILIBFILES = "jniLibFiles";
    private static final String TAG_JNILIBFILE = "jniLibFile";
    private static final String TAG_RESFILES = "resFiles";
    private static final String TAG_RESFILE = "resFile";
    private static final String TAG_LIBRARYINFOLINES = "libraryInfoLines";
    private static final String TAG_GRADLE = "gradle";
    private static final String TAG_FILE = "file";
    private static final String TAG_LIBRARYINFOLINE = "libraryInfoLine";
    private static final String TAG_IOS = "iosConfig";
    private static final String TAG_ANDROID = "androidConfig";
    private static final String TAG_APPLICATION = "application";
    private static final String TAG_MANIFEST = "manifest";

    private String id;
    private String name;
    private String api;
    private LibraryType libraryType;
    private String description;
    private String revision;
    private String history;
    private String option;
    private List<String> interfaceList;
    private List<String> jniLibFiles;
    private List<String> resFiles;
    private List<String> libraryInfoLines;
    private List<String> gradleFiles;
    private IosLibraryConfig iosConfig;
    private String androidConfig;
    private boolean isFree;

    public Library(Element libraryElement) {
        parse(libraryElement);
    }

    public Library(String id, String api, LibraryType libraryType, String description, String revision, String history) {
        this(id, id + "." + api + "." + revision, api, libraryType, description, revision, history, "");
    }

    public Library(String id, String name, String api, LibraryType libraryType, String description, String revision, String history, String option) {
        this.id = id;
        this.name = name;
        this.api = api;
        this.libraryType = libraryType;
        this.description = description;
        this.revision = revision;
        this.history = history;
        this.option = option;
    }

    public String getGroupName() {
        StringBuffer sb = new StringBuffer();
        sb.append("m_").append(getLibraryType().toString());
        if (getLibraryType() != LibraryType.CORE) {
            sb.append("_").append(getLibName());
        }
        return sb.toString();
    }

    private String getLibName() {
        String[] ids = getId().split("\\.");
        int index = 0;
        for (int i = 0; i < ids.length; i++) {
            String s = ids[i];
            if (s.equals(getLibraryType().toString())) {
                index = i;
                break;
            }
        }

        String name = "";
        for (int i = index + 1; i < ids.length; i++) {
            if (i > index + 1) {
                name += ".";
            }
            name += ids[i];
        }

        if (name.equals("")) {
            name = ids[ids.length - 1];
        }
        return name;
    }

    public String getCliId() {
        StringBuilder cliId = new StringBuilder("@morpheus/");
        if (libraryType == LibraryType.CORE) {
            cliId.append(libraryType);
        } else {
            cliId.append(libraryType.toString()).append('-').append(id.substring(id.lastIndexOf('.') + 1).toLowerCase());
        }

        return cliId.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public LibraryType getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(LibraryType libraryType) {
        this.libraryType = libraryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public void setInterfaceList(ArrayList<String> interfaceList) {
        this.interfaceList = interfaceList;
    }

    public String getKey() {
        return id + "." + api + "." + revision;
    }

    public List<String> getInterfaceList() {
        return interfaceList;
    }

    public List<String> getJniLibFiles() {
        return jniLibFiles;
    }

    public List<String> getResFiles() {
        return resFiles;
    }

    public List<String> getLibraryInfoLines() {
        return libraryInfoLines;
    }

    public List<String> getGradleFiles() {
        return gradleFiles;
    }

    public IosLibraryConfig getIosConfig() {
        return iosConfig;
    }

    public Element getAndroidConfig() {
        Document doc = XMLUtil.getDocument(androidConfig.trim());
        return doc.getDocumentElement();
    }

    public void setJniLibFiles(List<String> listFiles) {
        jniLibFiles = listFiles;
    }

    public void setResFiles(List<String> listFiles) {
        resFiles = listFiles;
    }

    public void setLibraryInfoLines(List<String> libraryInfoLines) {
        this.libraryInfoLines = libraryInfoLines;
    }

    public void setGradleFiles(List<String> gradleFiles) {
        this.gradleFiles = gradleFiles;
    }

    public void setIOSConfig(IosLibraryConfig iosConfig) {
        this.iosConfig = iosConfig;
    }

    public void setAndroidConfig(String androidConfig) {
        this.androidConfig = androidConfig;
    }

    private void parse(@NotNull Element libraryElement) {
        id = libraryElement.getAttribute(ATTR_ID);
        libraryType = LibraryType.fromString(libraryElement.getAttribute(ATTR_TYPE));
        String pay = libraryElement.getAttribute(ATTR_FREE).trim();
        if (pay != null && !pay.equals("")) {
            isFree = Boolean.parseBoolean(pay);
        }

        Element element = XMLUtil.getElementByName(libraryElement, TAG_API);
        if (element != null) {
            api = element.getTextContent();
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_NAME);
        if (element != null) {
            name = element.getTextContent();
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_DESCRIPTION);
        if (element != null) {
            description = element.getTextContent();
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_REVISION);
        if (element != null) {
            revision = element.getTextContent();
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_HISTORY);
        if (element != null) {
            history = element.getTextContent();
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_OPTION);
        if (element != null) {
            option = element.getTextContent();
        }

        Element interfacesElement = XMLUtil.getElementByName(libraryElement, TAG_INTERFACES);
        if (interfacesElement != null) {
            ArrayList<String> interfaceList = new ArrayList<String>();
            NodeList interfaceNodeList = interfacesElement.getElementsByTagName(TAG_INTERFACE);
            for (int i = 0; i < interfaceNodeList.getLength(); i++) {
                Element interfaceElement = (Element) interfaceNodeList.item(i);
                interfaceList.add(interfaceElement.getTextContent().trim());
            }
            setInterfaceList(interfaceList);
        }

        element = XMLUtil.getElementByName(libraryElement, TAG_JNILIBFILES);
        if (element != null) {
            jniLibFiles = parseChildrenList(element, TAG_JNILIBFILE);
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_RESFILES);
        if (element != null) {
            resFiles = parseChildrenList(element, TAG_RESFILE);
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_LIBRARYINFOLINES);
        if (element != null) {
            libraryInfoLines = parseChildrenList(element, TAG_LIBRARYINFOLINE);
        }
        element = XMLUtil.getElementByName(libraryElement, TAG_GRADLE);
        if (element != null) {
            gradleFiles = parseChildrenList(element, TAG_FILE);
        }
//        private IosLibraryConfig iosConfig;
        element = XMLUtil.getElementByName(libraryElement, TAG_IOS);
        if (element != null) {
            iosConfig = new IosLibraryConfig(element);
        }
//        private Document androidConfig;
        element = XMLUtil.getElementByName(libraryElement, TAG_ANDROID);
        if(element != null) {
            androidConfig = element.getTextContent();
        }
    }

    private @NotNull List<String> parseChildrenList(@NotNull Element parent, String childTag) {
        ArrayList<String> children = new ArrayList<>();

        NodeList nodeList = parent.getElementsByTagName(childTag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String child = element.getTextContent().trim();
            if (child != null && !child.isEmpty())
                children.add(child);
        }
        return children;
    }


    public Element toDocument(@NotNull Document doc) {
        Element libraryElement = doc.createElement(TAG_LIBRARY);

        libraryElement.setAttribute(ATTR_ID, id);
        libraryElement.setAttribute(ATTR_TYPE, libraryType.toString());

        appendChild(doc, libraryElement, TAG_API, api);
        appendChild(doc, libraryElement, TAG_NAME, name);
//        appendChild(doc, libraryElement, TAG_DESCRIPTION, description);
        appendChild(doc, libraryElement, TAG_REVISION, revision);
//        appendChild(doc, libraryElement, TAG_HISTORY, history);
        appendChildrenList(doc, libraryElement, interfaceList, TAG_INTERFACES, TAG_INTERFACE);
        if (option != null && !option.isEmpty()) {
            appendChild(doc, libraryElement, TAG_OPTION, option);
        }

//        private List<String> jinLibFiles;
        appendChildrenList(doc, libraryElement, jniLibFiles, TAG_JNILIBFILES, TAG_JNILIBFILE);
//        private List<String> resFiles;
        appendChildrenList(doc, libraryElement, resFiles, TAG_RESFILES, TAG_RESFILE);
//        private List<String> libraryInfoList;
        appendChildrenList(doc, libraryElement, libraryInfoLines, TAG_LIBRARYINFOLINES, TAG_LIBRARYINFOLINE);
        appendChildrenList(doc, libraryElement, gradleFiles, TAG_GRADLE, TAG_FILE);
//        private IosLibraryConfig iosConfig;
        if (iosConfig != null) {
            libraryElement.appendChild(iosConfig.toElement(doc, TAG_IOS));
        }
//        private Document androidConfig;
        appendAndroidConfig(doc, libraryElement);

        return libraryElement;
    }

    private void appendAndroidConfig(Document doc, Element libraryElement) {
        if(androidConfig == null || androidConfig.isEmpty()) {
            return;
        }

        Element android = doc.createElement(TAG_ANDROID);
        CDATASection data = doc.createCDATASection(androidConfig);
        android.appendChild(data);
        libraryElement.appendChild(android);
    }

    private Element appendChildrenList(@NotNull Document doc, Element root, List<String> list, String parentTag, String childTag) {
        if (list == null) {
            return null;
        }

        Element interfaces = doc.createElement(parentTag);
        for (String child : list) {
            Element childElement = doc.createElement(childTag);
            childElement.setTextContent(child);
            interfaces.appendChild(childElement);
        }
        root.appendChild(interfaces);
        return interfaces;
    }

    private @NotNull Element appendChild(@NotNull Document doc, @NotNull Element parent, String tag, String content) {
        Element element = doc.createElement(tag);
        element.setTextContent(content);
        parent.appendChild(element);

        return element;
    }
}

package m.client.ide.morpheus.framework.eclipse;

import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.resource.LibraryType;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.core.utils.StringUtil;
import m.client.ide.morpheus.core.utils.XMLUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.framework.eclipse.library.Library;
import m.client.ide.morpheus.ui.dialog.licensemanager.LicenseManagerView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AppXMLManager {

    private File appXMLFile;
    private ApplicationAndroidProject androidProject;
    private ApplicationXcodeProject xcodeProject;
    private String projectName = null;
    private String applicationName;

    private ArrayList<Library> libraryList = null;

    public AppXMLManager() {
    }

    public AppXMLManager(File appXMLFile, String projectName, String applicationName,
                         ApplicationAndroidProject androidProject, ApplicationXcodeProject xcodeProject,
                         ArrayList<Library> libraryList) {
        super();
        this.appXMLFile = appXMLFile;
        this.projectName = projectName;
        this.applicationName = applicationName;
        this.androidProject = androidProject;
        this.xcodeProject = xcodeProject;
        this.libraryList = libraryList;
    }

    public static @NotNull AppXMLManager getAppXMLManager(Project targetProject) {

        File configFile = FileUtil.getChildFile(targetProject, FrameworkConstants.PROJECT_CONFIGURATION_FILE);

        return getAppXMLManager(configFile);
    }

    public static @NotNull AppXMLManager getAppXMLManager(File appXMLFile) {

        if (appXMLFile == null || !appXMLFile.exists())
            return new AppXMLManager();

        @NotNull AppXMLManager appXMLManager = loadAppXMLManager(appXMLFile);
        return appXMLManager;
    }

    @Contract("_ -> new")
    public static @NotNull AppXMLManager loadAppXMLManager(File appXMLFile) {
        Document projectConfigurationDoc = XMLUtil.getDocument(appXMLFile);

        Element applicationElement = projectConfigurationDoc.getDocumentElement();
        Element projectNameElement = XMLUtil.getFirstChildElementByName(applicationElement, "projectName");
        Element applicationNameElement = XMLUtil.getFirstChildElementByName(applicationElement, "applicationName");

        Element androidElement = XMLUtil.getFirstChildElementByName(applicationElement, "android");
        Element androidProjectNameElement = XMLUtil.getFirstChildElementByName(androidElement, "projectName");
        Element androidApplicationNameElement = XMLUtil.getFirstChildElementByName(androidElement, "applicationName");
        Element androidPackageNameElement = XMLUtil.getFirstChildElementByName(androidElement, "packageName");
        Element androidCompileSdkElement = XMLUtil.getFirstChildElementByName(androidElement, "compileSdk");
        Element iosElement = XMLUtil.getFirstChildElementByName(applicationElement, "ios");

        String projectName = projectNameElement.getTextContent().trim();
        ApplicationXcodeProject xcodeProject = null;
        if (iosElement != null) {
            Element iosProjectNameElement = XMLUtil.getFirstChildElementByName(iosElement, "projectName");
            Element iosApplicationNameElement = XMLUtil.getFirstChildElementByName(iosElement, "applicationName");
            Element iosBundleIdentifierElement = XMLUtil.getFirstChildElementByName(iosElement, "bundleIdentifier");

            xcodeProject = new ApplicationXcodeProject(iosProjectNameElement.getTextContent().trim(),
                    iosApplicationNameElement.getTextContent().trim(), iosBundleIdentifierElement.getTextContent().trim());
        } else {
            xcodeProject = new ApplicationXcodeProject();
        }

        Element librariesElement = XMLUtil.getFirstChildElementByName(applicationElement, "libraries");

        String applicationName = applicationNameElement.getTextContent().trim();
        ApplicationAndroidProject androidProject = new ApplicationAndroidProject(androidProjectNameElement.getTextContent().trim(),
                androidApplicationNameElement.getTextContent().trim(), androidPackageNameElement.getTextContent().trim(),
                androidCompileSdkElement.getTextContent().trim());

//        UnapplyLibraryManager unapplyLibraryManager = UnapplyLibraryManager.getInstance();
        NodeList libraryNodes = librariesElement.getElementsByTagName("library");
        ArrayList<Library> libraryList = new ArrayList<Library>();
        for (int i = 0; i < libraryNodes.getLength(); i++) {
            Element libraryElement = (Element) libraryNodes.item(i);

            String id = libraryElement.getAttribute("id");
            String api = libraryElement.getAttribute("api");
            String revision = libraryElement.getAttribute("revision");
            LibraryType libraryType = LibraryType.fromString(libraryElement.getAttribute("type"));
//	public Resource(String id, String name, ResourceType resourceType, String description, String revision, String history) {
            Library library = new Library(id, api, libraryType, "", revision, "");
            if (library != null) {
                library.setRevision(revision);
                libraryList.add(library);
            }
//            Library library = unapplyLibraryManager.getLibrary(libraryType, id, api, revision);
//            if (library != null) {
//                libraryList.add(library);
//            }
        }
        return new AppXMLManager(appXMLFile, projectName, applicationName, androidProject, xcodeProject, libraryList);
    }

    public void writeFile() {
        Document doc = XMLUtil.getDocument(appXMLFile);
        setProjectConfiguration(doc);

        XMLUtil.writeXML(appXMLFile, doc);
    }

    private void setProjectConfiguration(@NotNull Document doc) {
        Element applicationElement = doc.getDocumentElement();
        Element projectNameElement = XMLUtil.getFirstChildElementByName(applicationElement, "projectName");
        Element applicationNameElement = XMLUtil.getFirstChildElementByName(applicationElement, "applicationName");

        Element androidElement = XMLUtil.getFirstChildElementByName(applicationElement, "android");
        Element androidProjectNameElement = XMLUtil.getFirstChildElementByName(androidElement, "projectName");
        Element androidApplicationNameElement = XMLUtil.getFirstChildElementByName(androidElement, "applicationName");
        Element androidPackageNameElement = XMLUtil.getFirstChildElementByName(androidElement, "packageName");
        Element androidCompileSdkElement = XMLUtil.getFirstChildElementByName(androidElement, "compileSdk");

        if (OSUtil.isMac()) {
            Element iosElement = XMLUtil.getFirstChildElementByName(applicationElement, "ios");
            if (iosElement == null) {
                iosElement = doc.createElement("ios");
                applicationElement.appendChild(iosElement);
            } else {
                XMLUtil.removeAll(iosElement);
            }

            Element iosProjectNameElement = doc.createElement("projectName");
            iosElement.appendChild(iosProjectNameElement);
            Element iosApplicationNameElement = doc.createElement("applicationName");
            iosElement.appendChild(iosApplicationNameElement);
            Element iosBundleIdentifierElement = doc.createElement("bundleIdentifier");
            iosElement.appendChild(iosBundleIdentifierElement);

            if (xcodeProject != null) {
                iosProjectNameElement.setTextContent(StringUtil.checkNull(xcodeProject.getProjectName()));
                iosApplicationNameElement.setTextContent(StringUtil.checkNull(xcodeProject.getApplicationName()));
                iosBundleIdentifierElement.setTextContent(StringUtil.checkNull(xcodeProject.getBundleId()));
            }
        }

        Element componentsElement = XMLUtil.getFirstChildElementByName(applicationElement, "components");
        Element librariesElement = XMLUtil.getFirstChildElementByName(applicationElement, "libraries");

        projectNameElement.setTextContent(getProjectName());
        applicationNameElement.setTextContent(getApplicationName());
        if (androidProject != null) {
            androidProjectNameElement.setTextContent(androidProject.getProjectName());
            androidApplicationNameElement.setTextContent(androidProject.getApplicationName());
            androidPackageNameElement.setTextContent(androidProject.getPackageName());
            androidCompileSdkElement.setTextContent(androidProject.getCompileSdk());
        }

        XMLUtil.removeAll(componentsElement);

        XMLUtil.removeAll(librariesElement);
        for (Library library : getLibraryList()) {
            Element libraryElement = doc.createElement("library");
            libraryElement.setAttribute("id", library.getId());
            libraryElement.setAttribute("type", library.getLibraryType().toString());
            libraryElement.setAttribute("api", library.getApi());
            libraryElement.setAttribute("revision", library.getRevision());

            librariesElement.appendChild(libraryElement);
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    @NotNull
    public ArrayList<Library> getLibraryList() {
        if (libraryList == null) {
            return new ArrayList<>();
        }

        libraryList.sort(Comparator.comparingInt(o -> o.getLibraryType().getValue()));

        return libraryList;
    }

    public List<Library> getFilteredLibraryList() {
        if (libraryList == null) {
            return new ArrayList<>();
        }

        Iterator<Library> it = libraryList.stream().filter(library ->
                library.getLibraryType().equals(LibraryType.CORE) ||
                        library.getLibraryType().equals(LibraryType.ADDON) ||
                        library.getLibraryType().equals(LibraryType.PLUGIN)).iterator();

        List<Library> libraries = new ArrayList<>();
        while (it.hasNext()) {
            Library library = it.next();
//            Unload library 에서 삭제되지 m.client.ide.morpheus.core.npm.NpmUtils.unapplyDependencies() 에서 처리
//            String cliId = library.getCliId();
//            if (library.getLibraryType().equals(LibraryType.ADDON) && cliId.indexOf("locale") >= 0) {
//                continue;
//            }
            libraries.add(library);
        }
        libraries.sort(Comparator.comparingInt(o -> o.getLibraryType().getValue()));

        return libraries;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setLibraryList(ArrayList<Library> libraryList) {
        this.libraryList = libraryList;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("ProjectConfiguration : \n\n");
        sb.append("Project Name : " + projectName + "\n");
        sb.append("Application Name : " + applicationName + "\n");
        sb.append("Library list : \n");
        for (Library library : libraryList) {
            sb.append("\tID : " + library.getId() + "\n");
            sb.append("\tType : " + library.getLibraryType().toString() + "\n");
            sb.append("\tApi : " + library.getApi() + "\n");
            sb.append("\tDescription : " + library.getDescription() + "\n");
            sb.append("\tRevision : " + library.getRevision() + "\n");
            sb.append("\tHistory : " + library.getHistory() + "\n");
            sb.append("\tInterface list : \n");
            sb.append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    public String getApplicationId() {
        return androidProject != null ? androidProject.getPackageName() : LicenseManagerView.EDUCATION_PREFIX + "." + projectName.toLowerCase();
    }

    public String getAndroidAppName() {
        return androidProject != null ? androidProject.getApplicationName() : projectName;
    }

    public String getAndroidPackageName() {
        return androidProject != null ? androidProject.getPackageName() : LicenseManagerView.EDUCATION_PREFIX + "." + projectName.toLowerCase();
    }

    public String getIosAppName() {
        return xcodeProject != null ? xcodeProject.getApplicationName() : projectName;
    }

    public String getIosBundleId() {
        return xcodeProject != null ? xcodeProject.getBundleId() : LicenseManagerView.EDUCATION_PREFIX + "." + projectName.toLowerCase();
    }
}

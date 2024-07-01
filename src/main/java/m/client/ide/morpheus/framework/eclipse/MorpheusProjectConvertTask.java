package m.client.ide.morpheus.framework.eclipse;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.core.config.global.NpmRCFileManager;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.framework.cli.config.PackageJsonInfo;
import m.client.ide.morpheus.framework.cli.config.PackageJsonManager;
import m.client.ide.morpheus.framework.eclipse.library.IosLibraryConfig;
import m.client.ide.morpheus.framework.eclipse.library.Library;
import m.client.ide.morpheus.framework.eclipse.library.UnapplyLibraryManager;
import m.client.ide.morpheus.framework.eclipse.manifest.Manifest;
import m.client.ide.morpheus.framework.eclipse.manifest.ManifestUtil;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class MorpheusProjectConvertTask extends Task.Modal {
    private final File projectFolder;
    private final AppXMLManager appXMLmanager;

    public MorpheusProjectConvertTask(@NotNull VirtualFile projectDir) {
        this(projectDir.toNioPath().toFile());
    }

    public MorpheusProjectConvertTask(@NotNull File projectDir) {
        super(null, FrameworkMessages.get(FrameworkMessages.convertProject), false);

        projectFolder = projectDir;
        appXMLmanager = AppXMLManager.getAppXMLManager(new File(projectFolder, FrameworkConstants.PROJECT_CONFIGURATION_FILE));
    }

    /**
     * @param indicator
     */
    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (!projectFolder.exists()) {
            CommonUtil.log(Log.LEVEL_ERROR, "Project not exist.");
            return;
        }
        indicator.setText(FrameworkMessages.get(FrameworkMessages.UnApplyLibraries));

        String task = "Make 'package.json' File.";
        indicator.setText2(task);
        PackageJsonInfo projectInfo = PackageJsonManager.makePackageJsonInfo(projectFolder.getAbsolutePath());
        CommonUtil.log(Log.LEVEL_DEBUG, task + System.lineSeparator() + projectInfo);

        task = "Make '.npmrc' File.";
        indicator.setText2(task);
        NpmRCFileManager.createNpmRCFile(CommonUtil.getPathFile(projectFolder.getAbsolutePath(), NpmConstants.NPMRC_FILE).getAbsolutePath());
        CommonUtil.log(Log.LEVEL_DEBUG, task);

        task = "Make 'morpheus.config.json' File.";
        indicator.setText2(task);
        File configFile = new File(projectFolder, NpmConstants.CONFIG_JSON_FILE);
        MorpheusConfigManager configManager = MorpheusConfigManager.makeMorpheusConfigFile(configFile, appXMLmanager);
        CommonUtil.log(Log.LEVEL_DEBUG, task + System.lineSeparator() + configManager.getJSONString());

        List<Library> libraryList = appXMLmanager.getLibraryList();
        try {
            unApplyLibraries(libraryList, indicator);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }

        convertBuildGradle(projectFolder, appXMLmanager, indicator);

        appXMLmanager.setLibraryList(new ArrayList<>());
        appXMLmanager.writeFile();

        if (OSUtil.isMac()) {
            updateIosProjectFile(projectFolder);
        }

        updateManifestInterface(projectFolder);
        unApplyJSLibrary(projectFolder);

        restructureProject(projectFolder);
    }

    public List<Library> getLibraryDependencies() {
        return appXMLmanager.getFilteredLibraryList();
    }

    private void restructureProject(@NotNull File projectFolder) {
        File androidProjectFolder = FileUtil.getChildFile(projectFolder, UIMessages.get(UIMessages.AndroidProjectPath));
        if (!androidProjectFolder.exists()) {
            androidProjectFolder.mkdirs();
        }

        try {
            // src folder move to android project folder
            File srcFolder = FileUtil.getChildFile(projectFolder, "src");
            if (srcFolder.exists()) {
                FileUtils.moveDirectory(srcFolder, FileUtil.getChildFile(androidProjectFolder, srcFolder.getName(), "main", "java"));
            }

            // jar files in mcoreLibs folder move to libs folder
            srcFolder = FileUtil.getChildFile(projectFolder, "mcoreLibs");
            if (srcFolder.exists()) {
                FileUtils.copyDirectory(srcFolder, FileUtil.getChildFile(projectFolder, "libs"));
                FileUtils.deleteDirectory(srcFolder);
            }

            // libs folder move to android project folder
            srcFolder = FileUtil.getChildFile(projectFolder, "libs");
            if (srcFolder.exists()) {
                FileUtils.moveDirectory(srcFolder, FileUtil.getChildFile(androidProjectFolder, srcFolder.getName()));
            }

            // build folder move to android project folder
            srcFolder = FileUtil.getChildFile(projectFolder, "build");
            if (srcFolder.exists()) {
                FileUtils.moveDirectory(srcFolder, FileUtil.getChildFile(androidProjectFolder, srcFolder.getName()));
            }

            // res folder move to android project folder
            srcFolder = FileUtil.getChildFile(projectFolder, "res");
            if (srcFolder.exists()) {
                FileUtils.moveDirectory(srcFolder, FileUtil.getChildFile(androidProjectFolder, "src", "main", "res"));
            }

            // delete bin, gen folder
            srcFolder = FileUtil.getChildFile(projectFolder, "bin");
            if (srcFolder.exists()) {
                FileUtils.deleteDirectory(srcFolder);
            }
            srcFolder = FileUtil.getChildFile(projectFolder, "gen");
            if (srcFolder.exists()) {
                FileUtils.deleteDirectory(srcFolder);
            }

            File srcFile = FileUtil.getChildFile(projectFolder, "build.gradle");
            if (srcFile.exists()) {
                FileUtils.moveToDirectory(srcFile, androidProjectFolder, true);
            }
            srcFile = FileUtil.getChildFile(projectFolder, "morpheus_proguard-project.txt");
            if (srcFile.exists()) {
                FileUtils.moveToDirectory(srcFile, androidProjectFolder, true);
            }

            srcFile = FileUtil.getChildFile(androidProjectFolder, "morpheus.gradle");
            createMorpheusGradleFile(srcFile);

            srcFile = FileUtil.getChildFile(projectFolder, "build.gradle");
            createBuildGradleFile(srcFile);
            srcFile = FileUtil.getChildFile(projectFolder, "settings.gradle");
            createSettingsGradleFile(srcFile);
            srcFile = FileUtil.getChildFile(projectFolder, "Jenkinsfile");
            createJenkinsFile(srcFile);

            srcFile = FileUtil.getChildFile(projectFolder, "AndroidManifest.xml");
            if (srcFile.exists()) {
                FileUtils.moveToDirectory(srcFile, FileUtil.getChildFile(androidProjectFolder, "src", "main"), true);
            }

            srcFile = FileUtil.getChildFile(projectFolder, "application.xml");
            if (srcFile.exists()) {
                srcFile.delete();
            }

            File projectSettingFile = new File(projectFolder, EclipseProjectNatureUtil.FILENAME_DOT_PROJECT);
            if (projectSettingFile.exists()) {
                projectSettingFile.delete();
            }

            srcFolder = FileUtil.getChildFile(projectFolder, "native", "ios");
            if (srcFolder.exists()) {
                FileUtils.moveToDirectory(srcFolder, projectFolder, true);
                FileUtils.deleteDirectory(srcFolder.getParentFile());
            }

            srcFile = FileUtil.getChildFile(projectFolder, UIMessages.get(UIMessages.IOSProjectPath));
            srcFile = FileUtil.getChildFile(srcFile, "Podfile");
            createPodfileFile(projectFolder.getName(), srcFile);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    public static void linkResourcesToXcodeProject(@NotNull Project project) throws Exception {
        File projectFolder = new File(project.getBasePath());
        linkResourcesToXcodeProject(projectFolder);
    }

    public static void linkResourcesToXcodeProject(File projectDir) throws Exception {
        File iosProjectFolder = FileUtil.getChildFile(projectDir, IOSProject_Path);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            return;
        }

        CommonUtil.@NotNull PythonCmd pythonCmd = CommonUtil.getPythonCmd();
        if (pythonCmd == null || pythonCmd.getCmd().isEmpty()) {
            return;
        }

        File modConfigFile = CommonUtil.getPathFile(pythonCmd.getModpbxproj(), FrameworkConstants.MOD_PBXPROJ_CONFIG_FILE);
        File runModConfigFile = CommonUtil.getPathFile(pythonCmd.getModpbxproj(), FrameworkConstants.MOD_PBXPROJ_LINK_RESOURCE);
        @NotNull AppXMLManager appXMLmanager = AppXMLManager.getAppXMLManager(new File(projectDir, FrameworkConstants.PROJECT_CONFIGURATION_FILE));
        String iosPbxProjFileLocation = CommonUtil.getPathString(iosProjectFolder.getAbsolutePath(), false,
                appXMLmanager.getProjectName() + FrameworkConstants.PROJECT_XCODE_SUFFIX, FrameworkConstants.PROJECT_XCODE_PBXPROJ_FILE);

        StringBuilder sb = new StringBuilder();

        File resFolder = FileUtil.getChildFile(projectDir, "assets/res");
        String contentToAdd = String.format("project.add_file_if_doesnt_exist('%s', tree='SOURCE_ROOT')", resFolder.getAbsolutePath());
        String content = null;

        InputStream is = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        BufferedReader br = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        fis = new FileInputStream(modConfigFile);
        br = new BufferedReader(new InputStreamReader(fis));

        String line = null;

        sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        content = sb.toString();
        content = content.replace("%IOS_PROJECT_PATH%", iosPbxProjFileLocation).replace("#ADD_NEW_FILE_HERE", contentToAdd);

        is = new ByteArrayInputStream(content.toString().getBytes("UTF-8"));
        fos = new FileOutputStream(runModConfigFile);
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(fos);

        int len = 0;
        byte[] buf = new byte[1024];
        while ((len = bis.read(buf, 0, 1024)) != -1) {
            bos.write(buf, 0, len);
        }
        bos.flush();

        String[] commands = {pythonCmd.getCmd(), runModConfigFile.getAbsolutePath()};
        ;
        ExecCommandUtil.executeCommandWithLog("[Project Manager] Link Resources To XcodeProject. [" + runModConfigFile.getName() + "]", commands);

        if (bos != null)
            bos.close();
        if (br != null)
            br.close();
    }

    private void createJenkinsFile(File file) throws IOException {
        String contents = FileUtil.readResFile("/common/Jenkinsfile");
        FileUtil.writeUTF8ToFile(contents, file);
    }

    private void createPodfileFile(String projectName, File file) throws IOException {
        String contents = "# Uncomment the next line to define a global platform for your project\n" +
                "# platform :ios, '9.0'\n" +
                "\n" +
                "def morpheus_pods\n" +
                "end\n" +
                "\n" +
                "target '%s' do\n" +
                "  # Comment the next line if you don't want to use dynamic frameworks\n" +
                "  use_frameworks!\n" +
                "\n" +
                "  # Pods for Morpheus Library\n" +
                "  morpheus_pods\n" +
                "\n" +
                "  # Add Your Pods Here\n" +
                "\n" +
                "end\n";
        contents = String.format(contents, projectName);

        FileUtil.writeUTF8ToFile(contents, file);
    }

    private void createBuildGradleFile(File file) throws IOException {
        String contents = "plugins {\n" +
                "    id 'com.android.application' version '7.2.2' apply false\n" +
                "    id 'com.android.library' version '7.2.2' apply false\n" +
                "}\n" +
                "\n" +
                "task clean(type: Delete) {\n" +
                "    delete rootProject.buildDir\n" +
                "}";

        FileUtil.writeUTF8ToFile(contents, file);
    }

    private void createSettingsGradleFile(File file) throws IOException {
        String contents = "pluginManagement {\n" +
                "    repositories {\n" +
                "        gradlePluginPortal()\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "    }\n" +
                "}\n" +
                "dependencyResolutionManagement {\n" +
                "    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "        flatDir {\n" +
                "            dirs 'libs'\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "//rootProject.name = \"Morpheus\"\n" +
                "include ':app'\n" +
                "project(':app').projectDir = file('./android/app')\n" +
                "\n";

        FileUtil.writeUTF8ToFile(contents, file);
    }

    private void createMorpheusGradleFile(File file) throws IOException {
        String contents = "// !! DO NOT TOUCH THIS FILE \n" +
                "apply from: '../../node_modules/@morpheus/core/android/library.gradle'\n" +
                "apply from: '../../node_modules/@morpheus/addon-db/android/library.gradle'";

        FileUtil.writeUTF8ToFile(contents, file);
    }

    private void convertBuildGradle(File projectFolder, @NotNull AppXMLManager appXMLmanager, ProgressIndicator indicator) {
        BuildGradleUtils buildGradleUtils = new BuildGradleUtils(projectFolder);

        buildGradleUtils.insertApplyFrom("morpheus.gradle");
        buildGradleUtils.insertAppId(appXMLmanager.getApplicationId());
        buildGradleUtils.removeBuildScript();
        buildGradleUtils.removeRepositories();
        buildGradleUtils.refactorSourceSets();
        buildGradleUtils.removeMCoreLibsFileTree();

        File gradlePropertyFile = new File(projectFolder, FrameworkConstants.GRADLE_PROPERTIES_FILE);
        @NotNull Properties gredleProperties = CommonUtil.getProperties(gradlePropertyFile);
        gredleProperties.setProperty("MORPHEUS_LIB_PATH", "../../node_modules/@morpheus");
        CommonUtil.setProperties(gradlePropertyFile, gredleProperties, "");
    }

    public void unApplyLibraries(@NotNull List<Library> unApplyList, @NotNull ProgressIndicator indicator) throws IOException {
        String task = FrameworkMessages.get(FrameworkMessages.UnApplyLibraries);
        CommonUtil.log(Log.LEVEL_DEBUG, task + System.lineSeparator() + unApplyList);
        indicator.setText2(task);

        for (Library library : unApplyList) {
            unApplyAndroidLibrary(projectFolder, library);
            if (OSUtil.isMac()) {
                unApplyIOSLibrary(projectFolder, library);
            }
        }
        if (OSUtil.isMac()) {
            try {
                linkResourcesToXcodeProject(projectFolder);
            } catch (Exception e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    private void unApplyAndroidLibrary(File projectDir, @NotNull Library library) throws IOException {
        CommonUtil.log(Log.LEVEL_DEBUG, "Unapply library] " + library.getId() + "." + library.getApi() + "." + library.getRevision());
        File androidLibsDir = new File(projectDir, "libs");
        File androidResDir = new File(projectDir, "res");
        File mcoreLibs = new File(projectDir, "mcoreLibs");

        StringBuilder prefixBuilder = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String[] ids = library.getId().split("\\.");
        sb.append("m_").append(library.getLibraryType().toString());
        prefixBuilder.append(library.getLibraryType().toString());

        String api = library.getApi();
        String revision = library.getRevision();
        switch (library.getLibraryType()) {
            case CORE:
                sb.append("_").append(api);
                if (revision != null && !revision.isEmpty()) {
                    sb.append(".").append(revision);
                }
                break;
            default:
                sb.append("_").append(ids[ids.length - 1]).append("_").append(api);  //$NON-NLS-2$
                if (revision != null && !revision.isEmpty()) {
                    sb.append(".").append(revision);
                }
                prefixBuilder.append("_").append(ids[ids.length - 1]);
                break;
        }
        sb.append(".jar");
        CommonUtil.log(Log.LEVEL_DEBUG, "\tDelete Jar : " + sb);

        File fileToDelete = new File(mcoreLibs.exists() ? mcoreLibs : androidLibsDir, sb.toString());
        if (fileToDelete.exists()) {
            fileToDelete.delete();
            CommonUtil.log(Log.LEVEL_DEBUG, "\tDelete : " + fileToDelete.getAbsolutePath());
        }

        final String prefix = prefixBuilder.toString();

        File[] parentDirs = androidResDir.listFiles(arg0 -> arg0.isDirectory());

        if (parentDirs != null) {
            ArrayList<File> toRemoveFileList = new ArrayList<File>();
            for (File file : parentDirs) {
                File[] files = file.listFiles((dir, name) -> name.startsWith(prefix));

                for (File file2 : files) {
                    toRemoveFileList.add(file2);
                }
            }

            for (File file : toRemoveFileList) {
                if (file.exists()) {
                    file.delete();
                    CommonUtil.log(Log.LEVEL_DEBUG, "\tDelete : " + file.getAbsolutePath());
                }
            }
        }

//        구현방안 검토
        deleteLibraryResourceFiles(projectDir, library);

        /**
         * Commnet		: 다른 라이브러리에서 사용될 수 있기 때문에 AndroidManifest.xml 파일에서는 제거하지 않음
         * 				  컴파일 에러에 대해서는 필요 시 안내하는 것으로 대체 ==> converting 에서는 제거 함
         * Author		: johyeongjin
         * Datetime		: Mar 4, 2022 11:07:21 AM
         *
         */
        removeAndroidManifestConfig(projectDir, library);

//        구현방안 검토
        unApplyGradle(projectDir, library);
    }

    /**
     * MethodName	: unApplyGradle
     * ClassName	: MorpheusProjectConvertTask
     * Commnet		: gradle 파일을 포함한 library 적용헤제 시 gradle 파일을 삭제 후
     * build.gradle 파일에 해당되는 'apply from' 삭제
     * Author		: johyeongjin
     * Datetime		: Sep 28, 2022 1:40:10 PM
     *
     * @param projectFolder
     * @param library
     * @return void
     */
    private void unApplyGradle(File projectFolder, @NotNull Library library) {
        Library unapplyInfo = UnapplyLibraryManager.getInstance().getLibrary(library.getLibraryType(), library.getId(), library.getApi(), library.getRevision());
        if (unapplyInfo == null || unapplyInfo.getGradleFiles() == null) {
            return;
        }

        CommonUtil.log(Log.LEVEL_DEBUG, "Unapply gradle] " + unapplyInfo.getId() + "." + unapplyInfo.getApi() + "." + unapplyInfo.getRevision());
        BuildGradleUtils gradleUtil = new BuildGradleUtils(projectFolder);
        List<String> listFiles = unapplyInfo.getGradleFiles();
        for (String file : listFiles) {
            File deleteFile = FileUtil.getChildFile(projectFolder, file);
            if (deleteFile != null && deleteFile.exists()) {
                deleteFile.delete();
                CommonUtil.log(Log.LEVEL_DEBUG, "\tdelete file] " + deleteFile.getAbsolutePath());
            }
            gradleUtil.deleteApplyFrom(deleteFile);
        }
    }

    private void deleteLibraryResourceFiles(File projectDir, @NotNull Library library) {
        Library unapplyInfo = UnapplyLibraryManager.getInstance().getLibrary(library.getLibraryType(), library.getId(), library.getApi(), library.getRevision());
        if (unapplyInfo == null) {
            return;
        }

        File androidLibsDir = new File(projectDir, "libs");
        File androidResDir = new File(projectDir, "res");
        File mcoreLibs = new File(projectDir, "mcoreLibs");

        deleteLibraryFiles(unapplyInfo.getJniLibFiles(), androidLibsDir); //$NON-NLS-1$
        deleteLibraryFiles(unapplyInfo.getResFiles(), androidResDir); //$NON-NLS-1$

        List<String> lines = unapplyInfo.getLibraryInfoLines();
        if (lines == null || lines.size() == 0) {
            return;
        }
        deleteJarFiles(androidLibsDir, lines);
        deleteJarFiles(mcoreLibs, lines);
    }

    private void deleteJarFiles(@NotNull File folder, List<String> startNamesToDelete) {
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        Collection<File> files = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (String startName : startNamesToDelete) {
            for (File jarFile : files) {
                String jarBaseName = FilenameUtils.getBaseName(jarFile.getName());
                if (jarBaseName.startsWith(startName)) {
                    jarFile.delete();
                    CommonUtil.log(Log.LEVEL_DEBUG, "\tDelete jar files : " + jarFile.getAbsolutePath());
                    break;
                }
            }
        }
    }

    private void deleteLibraryFiles(List<String> listFiles, File libFolder) {
        if (listFiles == null || listFiles.size() == 0) {
            return;
        }
        for (String path : listFiles) {
            File file = new File(libFolder, path);

            if (file.exists()) {
                file.delete();
                CommonUtil.log(Log.LEVEL_DEBUG, "\tDelete library files : " + file.getAbsolutePath());
            }
        }
    }

    private void removeAndroidManifestConfig(File projectFolder, @NotNull Library library) {
        Library unapplyInfo = UnapplyLibraryManager.getInstance().getLibrary(library.getLibraryType(), library.getId(), library.getApi(), library.getRevision());
        if (unapplyInfo == null) {
            return;
        }

        removeAndroidManifestConfig(projectFolder, unapplyInfo.getAndroidConfig());
    }

    private void removeAndroidManifestConfig(File projectFolder, Element configRoot) {
        if (projectFolder == null || configRoot == null) {
            return;
        }

        Element configApplicationElement = XMLUtil.getFirstChildElementByName(configRoot, "application");
        Element configManifestElement = XMLUtil.getFirstChildElementByName(configRoot, "manifest");

        File androidManifestXMLFile = FileUtil.getChildFile(projectFolder, FrameworkConstants.PROJECT_ANDROID_MANIFEST);
        if (!androidManifestXMLFile.exists()) {
            return;
        }

        CommonUtil.log(Log.LEVEL_DEBUG, "Remove android manifest config] " + androidManifestXMLFile.getAbsolutePath());
        Document manifestDoc = XMLUtil.getDocument(androidManifestXMLFile);
        if (manifestDoc == null) {
            return;
        }

        Element manifestRoot = manifestDoc.getDocumentElement();

        if (configApplicationElement != null) {
            Element applicationElement = XMLUtil.getFirstChildElementByName(manifestRoot, "application");
            NodeList configApplicationNodeList = configApplicationElement.getChildNodes();

            for (int i = 0; i < configApplicationNodeList.getLength(); i++) {
                if (configApplicationNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    Element srcEle = (Element) configApplicationNodeList.item(i);
                    String elementName = srcEle.getNodeName();
                    String attrName = srcEle.getAttribute("android:name");

                    NodeList nodeList = applicationElement.getElementsByTagName(elementName);

                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (nodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element ele = (Element) nodeList.item(j);
                            if (attrName.equals(ele.getAttribute("android:name"))) {
                                CommonUtil.log(Log.LEVEL_DEBUG, "\tRemove android manifest config : " + elementName + " : android:name=" + attrName);
                                applicationElement.removeChild(ele);
                                CommonUtil.log(Log.LEVEL_DEBUG, "\tRemove application element : " + ele);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (configManifestElement != null) {
            removeXMLNamespace(manifestRoot, configManifestElement);

            NodeList configManifestNodeList = configManifestElement.getChildNodes();
            for (int i = 0; i < configManifestNodeList.getLength(); i++) {
                if (configManifestNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element srcEle = (Element) configManifestNodeList.item(i);
                    String elementName = srcEle.getNodeName();
                    String attrName = srcEle.getAttribute("android:name");

                    NodeList nodeList = manifestRoot.getElementsByTagName(elementName);

                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (nodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element ele = (Element) nodeList.item(j);
                            if (attrName.equals(ele.getAttribute("android:name"))) {
                                CommonUtil.log(Log.LEVEL_DEBUG, "\tRemove android manifest config : " + elementName + " : android:name=" + attrName);
                                manifestRoot.removeChild(ele);
                                CommonUtil.log(Log.LEVEL_DEBUG, "\t\tRemove manifest element : " + ele);
                                break;
                            }
                        }
                    }
                }
            }
        }

        XMLUtil.writeXML(androidManifestXMLFile, manifestDoc);
    }

    private void removeXMLNamespace(Element manifestRoot, Element configManifestElement) {
        if (manifestRoot == null || configManifestElement == null) {
            return;
        }

        NamedNodeMap configManifestAttributes = configManifestElement.getAttributes();
        if (configManifestAttributes == null) {
            return;
        }

        String EMPTYSTRING = "";
        String ENTITY_COLON = ":";
        String XMLNS_PREFIX = "xmlns";
        for (int i = 0; i < configManifestAttributes.getLength(); i++) {
            Node attribute = configManifestAttributes.item(i);

            String name = attribute.getNodeName();
            int col = name.lastIndexOf(ENTITY_COLON);
            final String prefix = (col > 0) ? name.substring(0, col) : EMPTYSTRING;

            if (!EMPTYSTRING.equals(prefix)) {
                if (prefix.equals(XMLNS_PREFIX)) {
                    manifestRoot.removeAttribute(name);
                }
            }
        }
    }

    private void updateManifestInterface(@NotNull File projectFolder) {
        File manifestFile = ManifestUtil.getManifestFile(projectFolder);
        Manifest manifest = ManifestUtil.getManifest(manifestFile);
        manifest.setLibraryAddonList(new ArrayList<>());
        manifest.setLibraryPluginList(new ArrayList<>());

        Document doc = manifest.toDocument();
        XMLUtil.writeXML(manifestFile, doc);
    }

    private void unApplyJSLibrary(File projectFolder) {
        File mcoreMinJs = getMcoreJSFile(projectFolder);
        try {
            if (mcoreMinJs.exists()) {
                FileUtil.writeUTF8ToFile("", mcoreMinJs);
            } else {
                mcoreMinJs.createNewFile();
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    public static final String MCORE_MIN_JS_PATH = "assets/res/www/js/";
    public static final String PROPERTY_MCORE_MIN_JS = "mcore.min.js";

    private File getMcoreJSFile(File projectFolder) {
        if (EclipseProjectNatureUtil.isSPAProject(projectFolder)) {
            File projectPropertyFile = new File(projectFolder, CommonUtil.PROPERTIES_PROJECT);
            @NotNull Properties gredleProperties = CommonUtil.getProperties(projectPropertyFile);
            String filePath = gredleProperties.getProperty(PROPERTY_MCORE_MIN_JS);
            if (filePath != null && !filePath.isEmpty()) {
                return FileUtil.getChildFile(projectFolder, filePath);
            }
        }

        return FileUtil.getChildFile(projectFolder, MCORE_MIN_JS_PATH + PROPERTY_MCORE_MIN_JS);
    }

    private void unApplyIOSLibrary(File projectFolder, @NotNull Library library) {
        String name = library.getGroupName().replace(".", "_");
        unApplyIOSLibrary(projectFolder, library, name);

        if (library.getId().startsWith(MorpheusCLIUtil.PUSH_ID)) {
            StringBuilder sb = new StringBuilder();
            String[] ids = library.getId().split("\\.");
            sb.append("m_").append(library.getLibraryType().toString());
            switch (library.getLibraryType()) {
                case CORE:
                    break;
                default:
                    sb.append("_").append(ids[ids.length - 1]);  //$NON-NLS-2$
                    break;
            }
            unApplyIOSLibrary(projectFolder, library, sb.toString());
        }
    }

    private static final String IOSProject_Path = "native" + File.separator + "ios" + File.separator + "project";

    private @NotNull String getLibraryDirectoryName(@NotNull Library library) {
        return library.getId() + "." + library.getApi();
    }


    private void unApplyIOSLibrary(File projectFolder, Library library, String name) {
        @NotNull AppXMLManager appXMLmanager = AppXMLManager.getAppXMLManager(new File(projectFolder, FrameworkConstants.PROJECT_CONFIGURATION_FILE));
        XcodeGroup libraryGroup = new XcodeGroup(name, name, new XcodeGroup("MLibrary", "MLibrary", null));
        //XcodeGroup librarySampleGroup = new XcodeGroup(sb.toString(), sb.toString() + "_sample", new XcodeGroup("Sample", "Sample", null));

        File iosProjectFolder = FileUtil.getChildFile(projectFolder, IOSProject_Path);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            return;
        }

        String libraryDirectoryName = libraryGroup.getName() + "_" + library.getApi();
        File libraryDir = new File(CommonUtil.getPathString(iosProjectFolder.getAbsolutePath(), false,
                libraryGroup.getParent().getName(), libraryDirectoryName));

        //버전 정보 없는 라이브러리 선택
        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            libraryDirectoryName = libraryGroup.getName();
            libraryDir = new File(CommonUtil.getPathString(iosProjectFolder.getAbsolutePath(), false,
                    libraryGroup.getParent().getName(), libraryDirectoryName));

            if (!libraryDir.exists() || !libraryDir.isDirectory()) {
                return;
            }
        }

        CommonUtil.PythonCmd pythonCmd = CommonUtil.getPythonCmd();
        if (pythonCmd == null || pythonCmd.getCmd().isEmpty()) {
            return;
        }

        File templateFile = new File(CommonUtil.getPathString(pythonCmd.getModpbxproj(), false, "unload.py"));
        File configFile = new File(CommonUtil.getPathString(pythonCmd.getModpbxproj(), false, "unload_" + library.getId() + ".py"));

        String iosPbxProjFileLocation = CommonUtil.getPathString(iosProjectFolder.getAbsolutePath(), false,
                appXMLmanager.getProjectName() + FrameworkConstants.PROJECT_XCODE_SUFFIX, FrameworkConstants.PROJECT_XCODE_PBXPROJ_FILE);

        StringBuilder removeFileBuilder = new StringBuilder();
        StringBuilder groupBuilder = new StringBuilder();
        StringBuilder arrayBuilder = new StringBuilder();

        groupBuilder.append(
                libraryGroup.getVarName() + " = project.get_or_create_group('" + libraryGroup.getName() + "', parent="
                        + libraryGroup.getParent().getVarName() + ")").append("\n");

        removeFileBuilder.append("project.remove_file(" + libraryGroup.getVarName() + ")").append("\n");

        File[] libraryFiles = libraryDir.listFiles((arg0, arg1) -> arg1.endsWith(".bundle") || arg1.endsWith(".framework") || arg1.endsWith(".xcframework"));

        ArrayList<File> fileList = new ArrayList<>();

        if (libraryFiles != null && libraryFiles.length > 0) {
            for (File file : libraryFiles) {
                fileList.add(file);
            }
        }

        //서드파티 기타 라이브러리파일이 존재하는 경우 Config 파일에서 라이브러리 번들을 구해서 파일에 추가
        IosLibraryConfig iosConfig = library.getIosConfig();
        if (iosConfig != null) {
            if (iosConfig.getThirdPartBundle() != null && iosConfig.getThirdPartBundle().size() > 0) {
                for (String f : iosConfig.getThirdPartBundle()) {
                    fileList.add(new File(iosProjectFolder, f));
                }
            }
        }

        arrayBuilder.append("fileNames = [");
        boolean first = true;
        for (File file : fileList) {
            if (first)
                first = false;
            else
                arrayBuilder.append(", ");
            arrayBuilder.append("'").append(file.getName()).append("'");
        }


        arrayBuilder.append("]").append("\n");

        String getGroup = groupBuilder.toString();
        String removeFile = removeFileBuilder.toString();
        String fileArray = arrayBuilder.toString();
        String content = null;

        InputStream is = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedReader br = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        String line = null;
        StringBuilder contentSb = new StringBuilder();

        try {
            fis = new FileInputStream(templateFile);
            br = new BufferedReader(new InputStreamReader(fis));

            while ((line = br.readLine()) != null) {
                contentSb.append(line);
                contentSb.append("\n");
            }

            content = contentSb.toString()
                    .replace("%IOS_PROJECT_PATH%", iosPbxProjFileLocation)
                    .replace("#ADD_GROUP_HERE", getGroup)
                    .replace("#ADD_FILES_ARRAY_HERE", fileArray)
                    .replace("#REMOVE_GROUP", removeFile);


            is = new ByteArrayInputStream(content.getBytes("UTF-8"));
            fos = new FileOutputStream(configFile);
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(fos);

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf, 0, 1024)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
                if (fis != null)
                    fis.close();
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
            }
        }

        String[] commands = {pythonCmd.getCmd(), configFile.getAbsolutePath()};
        String path = configFile.getPath();
        ExecCommandUtil.executeCommandWithLog("[Project Manager] Unload iOS Library To XcodeProject. [" + path + "]", commands);

        try {
            FileUtils.deleteDirectory(libraryDir);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        }
    }

    /**
     * 라이브러리 변경에 따른 xcode 프로젝트 파일을 변경한다.
     * 주로 프레임워크 패스를 변경한다.
     *
     * @param project
     */
    private void updateIosProjectFile(File project) {
        File iosProjectFolder = FileUtil.getChildFile(project, IOSProject_Path);
        String iosPbxProjFileLocation = CommonUtil.getPathString(iosProjectFolder.getAbsolutePath(), false,
                project.getName() + FrameworkConstants.PROJECT_XCODE_SUFFIX, FrameworkConstants.PROJECT_XCODE_PBXPROJ_FILE);
        File xcodeFile = new File(iosPbxProjFileLocation);
        if (!xcodeFile.exists()) {
            return;
        }

        StringBuffer sBuffer = new StringBuffer();
        InputStreamReader isr = null;
        BufferedReader br = null;
        InputStream stream = null;
        try {
            String line = null;
            isr = new InputStreamReader(new FileInputStream(xcodeFile));
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sBuffer.append(line);
                sBuffer.append("\n");
            }

            int startIndex = 0;
            int endIndex = 0;
            boolean isChange = false;
            while (startIndex != -1) {
                startIndex = findIndexContents(sBuffer, "FRAMEWORK_SEARCH_PATHS", startIndex);
                if (startIndex != -1) {
                    endIndex = findIndexContents(sBuffer, ";", startIndex);
                    if (endIndex != -1) {
                        String contents = sBuffer.substring(startIndex, endIndex + 1);
                        String changeStr = changeFrameworkPath(contents);

                        //변경된 내용이 있을 경우
                        if (changeStr != null) {
                            sBuffer.replace(startIndex, endIndex + 1, changeStr);
                            isChange = true;
                        }
                    }
                }
            }

            String contents = sBuffer.toString();
            if (contents.indexOf("../../../assets/res") > 0) {
                contents = contents.replace("../../../assets/res", "../../assets/res");
                isChange = true;
            }
            if (isChange) {
                FileUtil.writeUTF8ToFile(contents, xcodeFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static int findIndexContents(@NotNull StringBuffer fileContents, String str, int nIndex) {
        return fileContents.indexOf(str, nIndex + 1);
    }

    /**
     * 구버전의 프레임웍 경로를 통합된 하나의 경로로 바꾼다.
     * 만약 바뀐 내용이 없는 경우엔 null을 리턴한다.
     *
     * @param contents
     * @return
     */
    private static @Nullable String changeFrameworkPath(@NotNull String contents) {
        int startIndex = 0;
        int endIndex = 0;
        String pathName = "";
        if (contents.contains("(") && contents.contains(");")) {
            startIndex = contents.indexOf("(") + 1;
            endIndex = contents.lastIndexOf(")");
            pathName = contents.substring(0, startIndex - 1);
        } else if (contents.contains("]\"")) {
            startIndex = contents.indexOf("]\"");
            String tmp = contents.substring(startIndex + 2);
            if (tmp.contains("(") && tmp.contains(");")) {
                startIndex = contents.indexOf("(") + 3;
                endIndex = contents.lastIndexOf(")");
                pathName = contents.substring(0, startIndex - 1);
            } else if (tmp.contains("\"")) {
                startIndex += tmp.indexOf("\"") + 2;
                endIndex = contents.lastIndexOf("\"") + 1;
                pathName = contents.substring(0, startIndex - 1);
            } else {
                return null;
            }
        } else {
            startIndex = contents.indexOf("\"");
            endIndex = contents.lastIndexOf("\"") + 1;
            pathName = contents.substring(0, startIndex);
        }

        if (startIndex <= endIndex) {
            String cnt = contents.substring(startIndex, endIndex);
            String[] strArr = cnt.split(",");

            final String mlPath = "\"$(SRCROOT)/MLibrary/**\"";
            final String tab = "\t\t\t\t\t";

            boolean isChange = false;
            List<String> list = new ArrayList<String>();
            for (String s : strArr) {
                boolean c = false;
                String tmp = s.replace("\t", "").replace("\n", "");
                if (!tmp.isEmpty() && !tmp.replace(" ", "").equals("") && !tmp.replace(" ", "").replace("\"", "").equals("")) {
                    if (tmp.contains("m_core") || tmp.contains("m_addon") || tmp.contains("m_plugin")) {
                        isChange = true;
                        c = true;
                    } else if (tmp.contains("MLibrary")) {
                        if (!tmp.contains("$(SRCROOT)/MLibrary/**")) {
                            isChange = true;
                            c = true;
                        }
                    }

                    if (!c) {
                        list.add(tmp);
                    }
                }
            }

            boolean isAddMlPath = true;
            for (String s : list) {
                if (s.contains("$(SRCROOT)/MLibrary/**")) {
                    isAddMlPath = false;
                    break;
                }
            }
            if (isAddMlPath) {
                list.add(mlPath);
            }

            if (isChange || isAddMlPath) {
                StringBuffer sb = new StringBuffer();
                int listSize = list.size();
                for (int i = 0; i < listSize; i++) {
                    if (list.size() > 1) {
                        sb.append(tab);
                    }
                    sb.append(list.get(i));
                    if (i < listSize - 1) {
                        sb.append(",");
                    }
                    if (list.size() > 1) {
                        sb.append("\n");
                    }
                }

                if (listSize > 1) {
                    sb.insert(0, "(\n");
                    sb.append("\t\t\t\t);");
                } else {
                    sb.append(";");
                }

//				sb.insert(0, "FRAMEWORK_SEARCH_PATHS = ");
                sb.insert(0, pathName);
                return sb.toString();
            }
        }
        return null;
    }

    public String getResourceTag(@NotNull Library resource) {
        StringBuilder sb = new StringBuilder();
        sb.append("/");

        return sb.append(resource.getLibraryType().toString()).append("@").append(resource.getId()).append(".")
                .append(resource.getApi()).append(".").append(resource.getRevision()).toString();
    }
}

package m.client.ide.morpheus.core.utils;

import com.android.annotations.Nullable;
import com.android.utils.GrabProcessOutput;
import com.android.utils.GrabProcessOutput.IProcessOutput;
import com.android.utils.GrabProcessOutput.Wait;
import com.esotericsoftware.minlog.Log;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PlatformUtils;
import m.client.ide.morpheus.core.constants.CoreConstants;
import m.client.ide.morpheus.core.messages.CoreMessages;
import nonapi.io.github.classgraph.utils.JarUtils;
import org.jetbrains.android.util.AndroidUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("restriction")
public class CommonUtil {
    private static final Logger LOG = Logger.getInstance(CommonUtil.class);

    public static final String ApplicationConfigEditor_ID = "m.client.ide.ui.editor.ApplicationConfigEditor";
    public static final String ExportAndroidWizard_ID = "m.client.ide.ui.ExportAndroidWizard";
    public static final String ExportiOSWizard_ID = "m.client.ide.ui.ExportiOSWizard";
    public static final String ExportProjectWizard_ID = "m.client.ide.framework.project.ExportProjectWizard";
    public static final String SYSTEM_USR_BIN = "/usr/bin";
    public static final String COMMOND_PYTHON = "python";
    public static final String MODPBXPROJ = "mod-pbxproj";
    public static final String MODPBXPROJ3 = "mod-pbxproj3";
    public static final String MODPBXPROJ14 = "mod-pbxproj14";
    public static final String PROPERTIES_LOCAL = "local.properties";
    public static final String PROPERTIES_PROJECT = "project.properties";

    private static final String TOOLS_RESOURCE_PATH = "src/main/resources/tools";
    private static final String ENVIRONMENT_PATH = "PATH";

    public static class PythonCmd {
        private String cmd = "";
        private String version = "";
        private String modpbxproj = "";

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getModpbxproj() {
            return modpbxproj;
        }

        public void setModpbxproj(String modpbxproj) {
            this.modpbxproj = modpbxproj;
        }

        @Override
        public String toString() {
            return "Python CMD ----> " + cmd + ", Version ----> " + version + ", Modpbxproj ----> " + modpbxproj;
        }
    }

    private static PythonCmd pythonCmd;

    public static void log(int levelError, String log) {
        log(levelError, CommonUtil.class.getClass(),
                ProjectManager.getInstance().getDefaultProject(), log);
    }

    public static void log(int levelError, Project project, String log) {
        log(levelError, CommonUtil.class.getClass(), project, log);
    }

    public static void log(int levelError, @NotNull Class clss, String log) {
        log(levelError, clss, null, log);
    }

    public static void log(int levelError, @NotNull Class clss, Project project, String log) {
        log(levelError, clss, project, log, null);
    }

    public static void log(int levelError, String log, Throwable e) {
        log(levelError, CommonUtil.class.getClass(),
                ProjectManager.getInstance().getDefaultProject(), log, e);
    }

    public static void log(int levelError, @NotNull Class clss, Project project, String log, Throwable e) {
        if (PreferenceUtil.getShowDebugMessage()) {
            System.out.println(log);
            Log.debug(clss.getName(), log);
        }

        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }

        switch (levelError) {
            case Log.LEVEL_DEBUG:
                LOG.debug(log);
                break;
            case Log.LEVEL_ERROR:
                LOG.error(log);
                break;
            default:
        }

        if (e != null) {
            e.printStackTrace(System.err);
        }
    }

    public static @NotNull String getDateString(long milliseconds) {
        return getDateFormatString(milliseconds, "yyyy-MM-dd");
    }

    public static @NotNull String getDateTimeString(long milliseconds) {
        return getDateFormatString(milliseconds, "yyyy-MM-dd HH:mm:ss");
    }

    public static @NotNull String getDateFormatString(long milliseconds, String timeFormat) {
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat(timeFormat, Locale.KOREA);
        return formatter.format(date);
    }

    public static void openDialog(int type, String message) {
        openDialog(type, "", message);
    }

    public static void openDialog(String title, String message) {
        openDialog(null, JOptionPane.INFORMATION_MESSAGE, title, message);
    }

    public static void openDialog(int type, String title, String message) {
        openDialog(null, type, title, message);
    }

    public static void openDialog(Component baseComponent, int type, String title, String message) {
        if (baseComponent == null) {
            baseComponent = getDefaultWindows();
        }
        JOptionPane.showMessageDialog(baseComponent,
                message, title, type);
    }

    private static @org.jetbrains.annotations.Nullable Component getDefaultWindows() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isActive() && window.isFocused()) {
                return window;
            }
        }
        return null;
    }

    public static void openErrorDialog(final String message) {
        openErrorDialog(CoreMessages.get(CoreMessages.CommonUtil_1), message);
    }

    public static void openErrorDialog(final String title, final String message) {
        openDialog(JOptionPane.ERROR_MESSAGE, title, message);
    }

    public static void openInfoDialog(final String message) {
        openInfoDialog(CoreMessages.get(CoreMessages.CommonUtil_0), message);
    }

    public static void openInfoDialog(final String title, final String message) {
        openDialog(JOptionPane.INFORMATION_MESSAGE, title, message);
    }

    public static void openWarningDialog(String title, String message) {
        openDialog(JOptionPane.WARNING_MESSAGE, title, message);
    }

    public static int openQuestion(String title, String message) {
        return openQuestion(null, title, message);
    }

    public static int openQuestion(Component parentComponent, String title, String message) {
        if (parentComponent == null) {
            parentComponent = getDefaultWindows();
        }
        return openConfirmDialog(parentComponent, JOptionPane.YES_NO_OPTION, title, message);
    }

    //	optionType – an int designating the options available on the dialog: YES_NO_OPTION, YES_NO_CANCEL_OPTION, or OK_CANCEL_OPTION
//	optionType – an int designating the options available on the dialog: YES_NO_OPTION, YES_NO_CANCEL_OPTION, or OK_CANCEL_OPTION
    public static int openConfirmDialog(Component parentComponent, int type, String title, String message) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title, type);
    }

    /**
     * MethodName	: getDeploymentTargets
     * ClassName	: CommonUtil
     * Commnet		: iOS deployment target 내림차순 리스트 리턴 ( 9.0 ~ 15.5 )
     * Author		: johyeongjin
     * Datetime		: Jun 23, 2022 9:16:39 AM
     *
     * @return ArrayList<String>
     * @return
     */
    public static ArrayList<String> getDeploymentTargets() {
        ArrayList<String> targets = new ArrayList<String>();
        targets.addAll(Arrays.asList(CoreConstants.DEPLOYMENT_TARGET_SUGGESTED_VALUES));
        targets.sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // TODO Auto-generated method stub
                return (int) (Float.valueOf(o2) * 10 - Float.valueOf(o1) * 10);
            }
        });
        return targets;
    }

    public static ResourceBundle getApplicationBundle() {
        ResourceBundle applicationFile = ResourceBundle.getBundle("application");
        return applicationFile;
    }

    public static URL getDocURL() {
        URL url = null;
        try {
            url = new URL(getDocURLString());
        } catch (MalformedURLException e) {
            LOG.error(e);
        }
        return url;
    }

    public static String getDocURLString() {
        return getApplicationBundle().getString("docURL");
    }

    public static String[] getFeatureGroups() {
        return getApplicationBundle().getString("featureGroups").split(",");
    }

    /**
     * 공지사항 리스트 URL 리턴
     *
     * @return
     */
    public static String getNoticeListURL() {
        return getApplicationBundle().getString("noticeListURL");
    }


    /**
     * MethodName	: getAppDataLocation
     * ClassName	: CommonUtil
     * Commnet		: Return default MSDK Location
     * :   MacOS : (user.home)/Library/Application Support/Morpheus
     * :   Windows : System.getenv("AppData") + Morpheus
     * Author		: johyeongjin
     * Datetime		: Feb 24, 2022 9:26:19 AM
     *
     * @return String
     * @return
     */
    public static String getAppDataLocation() {
        String workingDirectory;
        if (OSUtil.isWindows()) {
            //it is simply the location of the "AppData" folder
            workingDirectory = System.getenv("AppData");
            workingDirectory = getPathString(workingDirectory, "Morpheus");
        }
        //Otherwise, we assume Linux or Mac
        else {
            //in either case, we would start in the user's home directory
            workingDirectory = System.getProperty("user.home");
            //if we are on a Mac, we are not done, we look for "Application Support"
            workingDirectory += "/Library/Application Support/Morpheus";
        }

        File dir = new File(workingDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return workingDirectory;
    }

    public static Path getPath(Path parent, String... children) {
        Path path = parent;
        if (children != null)
//			for (String child : children) {
            path = Path.of(path.toString(), children);
//			}
        return path;
    }

    public static File getPathFile(Path parent, String... children) {
        Path path = getPath(parent, children);
        return path.toFile();
    }

    public static Path getPath(String parent, String... children) {
        return Path.of(parent, children);
    }

    public static File getPathFile(String parent, String... children) {
        return getPath(parent, children).toFile();
    }

    public static String getPathString(Path parent, String... children) {
        return getPathString(parent, false, children);
    }

    public static String getPathString(Path parent, boolean hasDoubleQuotes, String... children) {
        Path path = getPath(parent, children);
        if (OSUtil.isWindows() && hasDoubleQuotes)
            return "\"" + path.toString() + "\"";
        else
            return path.toString();
    }

    public static String getPathString(String parent, String... children) {
        return getPathString(parent, false, children);
    }

    public static String getPathString(String parent, boolean hasDoubleQuotes, String... children) {
        Path path = getPath(parent, children);
        if (OSUtil.isWindows() && hasDoubleQuotes)
            return "\"" + path.toString() + "\"";
        else
            return path.toString();
    }

    public static Path getRelativePath(Path source, Path target) {
        return Path.of(FileUtil.getRelativePath(source.toFile(), target.toFile()));
    }

    public static boolean isNetworkAvailable(String host) {
        boolean result = false;
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(host, 80);
        try {
            sock.connect(addr, 3000);
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return result;
    }

    public static boolean isNetworkAvailable(String host, int port) {
        boolean result = false;
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(host, port);
        try {
            sock.connect(addr, 3000);
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }

        return result;
    }

    public static boolean isNetworkAvailable(InetSocketAddress addr) {
        boolean result = false;
        Socket sock = new Socket();
        try {
            sock.connect(addr, 3000);
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }

        return result;
    }

    public static boolean isNetworkAvailable(URI uri) {
        String url = uri.toString();
        int port = 80;
        String host = uri.getHost();
        String t = url.replace("http://", "").replace(host, "");
        if (t.contains(":")) {
            int startIndex = t.indexOf(":") + 1;
            int endIndex = t.indexOf("/");
            if (startIndex < endIndex) {
                String p = t.substring(startIndex, endIndex);
                try {
                    port = Integer.parseInt(p);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return isNetworkAvailable(new InetSocketAddress(host, port));
    }

    public static @NotNull Properties getProperties(File propertiesFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (FileNotFoundException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }

        return properties;
    }

    public static void setProperties(File propertiesFile, Properties properties, String comments) {
        OutputStream os = null;
        try {
            if (!propertiesFile.canWrite()) {
                String[] commands = {"chmod", "-R", "+w", propertiesFile.getAbsolutePath()};
                try {
                    Process process = ExecCommandUtil.excuteCommand(commands);
                    process.waitFor();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
            if (propertiesFile.exists())
                propertiesFile.delete();

            Set<Object> keySet = properties.keySet();
            for (Object key : keySet) {
                String value = properties.getProperty((String) key);
                if (value == null)
                    value = ""; //$NON-NLS-1$
                properties.setProperty((String) key, value);
            }

            os = new FileOutputStream(propertiesFile);
            properties.store(os, comments);

        } catch (FileNotFoundException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    public static void updateProperties(@NotNull File propertiesFile, Properties properties, String comments) {

        if (!propertiesFile.exists()) {
            try {
                propertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Properties originProperties = new Properties();
        try {
            originProperties.load(new FileInputStream(propertiesFile));
            Set<Object> keySet = properties.keySet();
            for (Object key : keySet) {
                String value = properties.getProperty((String) key);
                if (value == null)
                    value = ""; //$NON-NLS-1$
                originProperties.setProperty((String) key, value);
            }

            FileOutputStream os = new FileOutputStream(propertiesFile);
            originProperties.store(os, comments);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @NotNull
    public static PythonCmd getPythonCmd() {
        // TODO Auto-generated method stub
        if (pythonCmd == null || pythonCmd.getCmd().isEmpty()) {
            pythonCmd = new PythonCmd();

            File defaultPython = new File(SYSTEM_USR_BIN, COMMOND_PYTHON);
            if (defaultPython.exists() && defaultPython.canExecute()) {
                pythonCmd.cmd = defaultPython.getAbsolutePath();
            } else {
                File folder = new File(SYSTEM_USR_BIN);
                File[] pythons = folder.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        // TODO Auto-generated method stub
                        String name = pathname.getName();
                        if (name.startsWith(COMMOND_PYTHON) &&
                                Character.isDigit(name.charAt(6)))
                            return true;

                        return false;
                    }
                });
                if (pythons != null && pythons.length > 0) {
                    for (File python : pythons) {
                        log(Log.LEVEL_DEBUG, SYSTEM_USR_BIN + " ] Finded Python ----> " + python.getAbsolutePath());
                        if (python.canExecute()) {
                            pythonCmd.cmd = python.getAbsolutePath();
                            break;
                        }
                    }
                }
            }
            if (pythonCmd.cmd == null || pythonCmd.cmd.isEmpty()) {
                pythonCmd = null;
                return null;
            }

            Path modPbxprojPath = getPath(getToolsPath(), MODPBXPROJ);
            pythonCmd.version = getPythonVersion(pythonCmd.cmd);
            log(Log.LEVEL_DEBUG, "Pyphon command result ] " + pythonCmd.version);
            String xcodeVersion = getXcodeVersion();
            if (!xcodeVersion.isEmpty() && xcodeVersion.compareToIgnoreCase("14") >= 0) {
                modPbxprojPath = getPath("tools", MODPBXPROJ14);
            } else if (!pythonCmd.version.isEmpty() && pythonCmd.version.compareToIgnoreCase("3") >= 0) {
                modPbxprojPath = getPath("tools", MODPBXPROJ3);
            }
            System.out.println("Pyphon version : " + pythonCmd.version + "] modPbxprojPath : " + modPbxprojPath);

            pythonCmd.modpbxproj = getPathString(modPbxprojPath, false);
        }

        log(Log.LEVEL_DEBUG, "CommonUtil.getPythonCmd() ] " + pythonCmd);
        return pythonCmd;
    }

    @Nullable
    private static Path getToolsPath() {
        final Path[] toolPath = new Path[1];
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                @org.jetbrains.annotations.Nullable String jarPath = PathManager.getJarPathForClass(getClass());
                System.out.println("JarPath : " + jarPath);
                String classPath = JarUtils.classNameToClassfilePath(getClass().getName());
                System.out.println("ClassPath : " + classPath);
                toolPath[0] = CommonUtil.getPath(jarPath, TOOLS_RESOURCE_PATH);
            }
        });
        return toolPath[0];
    }

    public static void readFile(String filePath) {
        File file = new File(filePath);

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPythonVersion(String pythonCmd) {

        final AtomicReference<String> result = new AtomicReference<String>();
        try {
            String[] commands = {pythonCmd, "-V"};
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();

            GrabProcessOutput.grabProcessOutput(process, Wait.WAIT_FOR_READERS, new IProcessOutput() {
                @Override
                public void out(@Nullable String line) {
                    // Ignore stdout
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        String[] token = line.split(" ");
                        if (token.length > 1)
                            result.set(token[1]);
                        log(Log.LEVEL_DEBUG, "Pyphon command out : " + commands[0] + " " + commands[1] + "] " + result.get());
                    }
                }

                @Override
                public void err(@Nullable String line) {
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        String[] token = line.split(" ");
                        if (token.length > 1)
                            result.set(token[1]);
                        log(Log.LEVEL_DEBUG, "Pyphon command err : " + commands[0] + " " + commands[1] + "] " + result.get());
                    }
                }
            });
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log(Log.LEVEL_ERROR, "Pyphon command exception : " + e.getLocalizedMessage());
        }

        return result.get().trim();
    }

    /**
     * MethodName	: getXcodeVersion
     * ClassName	: CommonUtil
     * Commnet		:
     * Author		: johyeongjin
     * Datetime		: Sep 14, 2022 1:35:58 PM
     *
     * @return String Xcode version
     * @return
     */
    public static @NotNull String getXcodeVersion() {
        final String commands[] = {"xcodebuild", "-version"};

        String xcode = "Xcode ";
        StringBuilder version = new StringBuilder();

        try {
            Process process = ExecCommandUtil.excuteCommand(commands);

            GrabProcessOutput.grabProcessOutput(process, Wait.WAIT_FOR_PROCESS, new IProcessOutput() {
                @Override
                public void out(@Nullable String line) {
                    // Ignore stdout
                    if (line != null) {
                        if (!line.isEmpty() && line.contains(xcode)) {
                            log(Log.LEVEL_DEBUG, "[getXcodeVersion]" + line);
                            version.append(line.substring(line.indexOf(xcode) + xcode.length()));
                        }
                    } else {
                        log(Log.LEVEL_DEBUG, "[getXcodeVersion] Complete '" + commands[0] + " " + commands[1]);
                    }
                }

                @Override
                public void err(@Nullable String line) {
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        if (!line.isEmpty() && line.contains(xcode)) {
                            log(Log.LEVEL_DEBUG, "[getXcodeVersion]" + line);
                            version.append(line.substring(line.indexOf(xcode) + xcode.length()));
                        }
                        LOG.error("[getXcodeVersion error] " + line);
                    }
                }
            });
            Thread.sleep(500);
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            LOG.error("[getXcodeVersion error : " + e.toString() + "] " + e.getMessage());
        }
        return version.toString();
    }

    public static boolean isAndroidStudio() {
        return StringUtil.equals(PlatformUtils.getPlatformPrefix(), "AndroidStudio");
    }

    /**
     * MethodName	: updateAndroidExported
     * ClassName	: CommonUtil
     * Commnet		: Android compile sdk 31 이상에서 intent-filter 가 있는 activity 는
     * ‘android:exported=true’ 가 명시 되도록 수정
     * Author		: johyeongjin
     * Datetime		: Sep 14, 2022 4:52:47 PM
     *
     * @param androidProject : "AndroidManifest.xml" 파일이 포함된 프로젝트
     * @param compileSdk     : ex. "android-31" or "31"
     * @return void
     */
    public static void updateAndroidExported(Project androidProject, String compileSdk) {
        if (!AndroidUtils.getInstance().isAndroidProject(androidProject)) {
            return;
        }

        @Nullable VirtualFile projectFile = androidProject.getWorkspaceFile().findChild(androidProject.getName());
        @Nullable VirtualFile manifest = projectFile.findChild("AndroidManifest.xml");
        File androidManifestXML = new File(manifest.getPath());

        Document manifestDoc = XMLUtil.getDocument(androidManifestXML);
        if (manifestDoc != null) {
            Element manifestElement = manifestDoc.getDocumentElement();

            Element appElement = XMLUtil.getFirstChildElementByName(manifestElement, "application");
            if (appElement != null) {
                if (updateAndroidExported(appElement, compileSdk)) {
                    XMLUtil.writeXML(androidManifestXML, manifestDoc);
                    manifest.refresh(true, false);
                }
            }
        }
    }

    /**
     * MethodName	: updateAndroidExported
     * ClassName	: CommonUtil
     * Commnet		: Android compile sdk 31 이상에서 intent-filter 가 있는 activity 는
     * ‘android:exported=true’ 가 명시 되도록 수정
     * Author		: johyeongjin
     * Datetime		: Sep 14, 2022 4:03:09 PM
     *
     * @param appElement : "AndroidManifest.xml" 파일의 <application> element
     * @param compileSdk : ex.> "android-31" or "31"
     * @return boolean
     */
    public static boolean updateAndroidExported(Element appElement, String compileSdk) {
        if (compileSdk == null || appElement == null) {
            return false;
        }

        boolean modified = false;
        String sVersion = compileSdk.split("-").length > 1 ? compileSdk.split("-")[1] : compileSdk;
        try {
            Integer.parseInt(sVersion);
        } catch (NumberFormatException e) {
            LOG.error("[error : " + e.toString() + "] " + e.getMessage());
            return false;
        }
        if (sVersion.compareTo("30") > 0) {
            List<Element> activities = XMLUtil.getChildElementsByName(appElement, "activity");
            for (Element activity : activities) {
                if (XMLUtil.getFirstChildElementByName(activity, "intent-filter") != null &&
                        activity.getAttribute("android:exported").isEmpty()) {
                    activity.setAttribute("android:exported", "true");
                    modified = true;
                }
            }
        }

        return modified;
    }

    /**
     * @param project
     * @return
     */
    public static boolean refreshProject(@NotNull Project project) {
        @Nullable VirtualFile projectFile = project.getWorkspaceFile().findChild(project.getName());
        if (projectFile != null) {
            projectFile.refresh(true, true);
            return true;
        }

        return false;
    }

    public static boolean refreshProject(@NotNull String projectRootPath) {
        @Nullable VirtualFile root = VirtualFileManager.getInstance().findFileByNioPath(Paths.get(projectRootPath));
        if (root != null) {
            root.refresh(true, true);
            return true;
        }

        return false;
    }
}

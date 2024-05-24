package m.client.ide.morpheus.framework.cli;

import com.android.annotations.Nullable;
import com.android.tools.idea.wizard.template.ModuleTemplateData;
import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.config.global.NpmRCFileManager;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.*;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import m.client.ide.morpheus.framework.template.MorpheusAppTemplateData;
import m.client.ide.morpheus.framework.template.MorpheusTemplateHelper;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.Status;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MorpheusCLIUtil {

    public static final String PUSH_ID = "m.client.library.plugin.push"; //$NON-NLS-1$
    //	public static final String PUSH_ID_R4 = "m.client.library.plugin.push.r4"; //$NON-NLS-1$
    public static final String PREVENTION_ID = "m.client.library.plugin.prevention"; //$NON-NLS-1$
    public static final String VIEWER_ID = "m.client.library.plugin.viewer"; //$NON-NLS-1$
    public static final String QR_ID = "m.client.library.plugin.qr"; //$NON-NLS-1$
    public static final String ANDROID_QZ_ID = "m.client.library.third_android.viewer.oz"; //$NON-NLS-1$
    public static final String IOS_QZ_ID = "m.client.library.third_ios.viewer.oz"; //$NON-NLS-1$

    private static @NotNull String getMorpheusCommand() {
        @NotNull CoreSettingsState state = CoreSettingsState.getInstance();
        String morpheusCommand = state.getMorpheusCommand();
        if (morpheusCommand != null && !morpheusCommand.isEmpty()) {
            return morpheusCommand;
        }

        StringBuilder command = new StringBuilder(ExecCommandUtil.findPathWithWhich("morpheus"));

        state.setMorpheusCommand(command.toString());
        return command.toString();
    }

    public static @NotNull String installMorpheusCLI(Project project) {
        String npm = NpmUtils.findNpm();
        if (npm.isEmpty()) {
            String log = "[installMorpheusCLI error : Node is not exist or too row version.";
            log(Log.LEVEL_ERROR, log);
            return "";
        }

        NpmRCFileManager rcFileManager = new NpmRCFileManager();
        rcFileManager.createNpmRCFile();

        String[] commands = {npm, "install", "-g", "@morpheus/cli"};

        ExecCommandUtil.execProcessHandler(FrameworkMessages.get(FrameworkMessages.installMorpheusCLI), null, commands);

        CoreSettingsState.getInstance().setMorpheusCommand("");
        String command = getMorpheusCommand();
        CoreSettingsState.getInstance().setMorpheusCommand(command);
        return command;
    }

    private static @NotNull String getMorpheusCLIVersion() {
        String[] commands = {getMorpheusCommand(), "--version"};

        return ExecCommandUtil.execProcessHandler(FrameworkMessages.get(FrameworkMessages.getMorpheusCLIVersion), null, commands);
    }

    public static boolean checkMorpheusCLI(Project project) {
        String npm = NpmUtils.getNpmPathWithCheck(project);
        if (npm.isEmpty()) {
            return false;
        }

        String version = getMorpheusCLIVersion();
        if (version == null || version.isEmpty()) {
            int ret = CommonUtil.openQuestion(FrameworkMessages.get(FrameworkMessages.morpheusCliCheck),
                    FrameworkMessages.get(FrameworkMessages.morpheusCliInstallQuestion));
            if (ret == JOptionPane.YES_OPTION) {
                installMorpheusCLI(project);
                SwingUtilities.invokeLater(() -> {
                    String installedVersion = getMorpheusCLIVersion();
                    CoreSettingsState settings = CoreSettingsState.getInstance();
                    settings.setCliVersion(installedVersion);
                });
            } else {
                return false;
            }
        } else {
            // Save latest installed version!
            CoreSettingsState settings = CoreSettingsState.getInstance();
            if (!version.equals(settings.getCliVersion())) {
                settings.setCliVersion(version);
            }
        }

        return true;
    }

    public static void createProject(@NotNull ModuleTemplateData moduleData, MorpheusAppTemplateData templateData) {
        File appFolder = moduleData.getRootDir();
        File projectFolder = new File(appFolder.getParent());

        createProject(projectFolder, templateData);
    }

    public static void createProject(File projectFolder, MorpheusAppTemplateData templateData) {
        FileUtil.delete(projectFolder);

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "getMorpheusVersion() => [ " + version + " ]");
        }

        String projectName = projectFolder.getName();
        String[] commands = MorpheusTemplateHelper.getCommands(getMorpheusCommand(), projectName, templateData);

        String task = FrameworkMessages.get(FrameworkMessages.createProject, projectName);
        ExecCommandUtil.printCommands(task, commands);

        ExecCommandUtil.execProcessHandler(task, projectFolder.getParentFile(), commands);
    }

    public static void addLicense(@NotNull String zipPath) {
        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "addLicense] getMorpheusVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "license", "add", zipPath};

        String task = FrameworkMessages.get(FrameworkMessages.addLicense);
        ExecCommandUtil.printCommands(task, commands);

        ExecCommandUtil.execProcessHandler(task, null, commands);
    }

    public static @NotNull Map<String, LicenseParam> getLicenseList() {
        Map<String, LicenseParam> licenses = new HashMap<>();
        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "getLicenseList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "select", "license"};

        String task = FrameworkMessages.get(FrameworkMessages.getLicenseList);
        ExecCommandUtil.printCommands(task, commands);

        try {
            String out = ExecCommandUtil.execProcessHandler(task, null, commands);
            if (out != null && !out.isEmpty()) {
                LicenseParam.parseLicense(out, licenses);
            }
        } catch (ParseException e) {
            String log = "[morpheus select license error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, log);
        }

        return licenses;
    }

    public static @NotNull Map<String, Map<String, LibraryParam>> getLibraryList(Project project) {
        Map<String, Map<String, LibraryParam>> libraries = new HashMap<>();
        if (!checkMorpheusCLI(project)) {
            return libraries;
        }

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, project, "getLibraryList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "select", "library"};

        String task = FrameworkMessages.get(FrameworkMessages.getLibraryList);
        ExecCommandUtil.printCommands(task, commands);

        try {
            String out = ExecCommandUtil.execProcessHandler(project, task, commands);
            if (out != null && !out.isEmpty()) {
                LibraryParam.parseLibrary(out, libraries);
            }
        } catch (ParseException e) {
            String log = "[morpheus select library error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, project, log);
        }

        return libraries;
    }

    public static @NotNull Map<String, Map<String, LibraryManagedParam>> getManageLibrary(@NotNull Project project) {
        Map<String, Map<String, LibraryManagedParam>> libraries = new HashMap<>();
        if (!checkMorpheusCLI(project)) {
            return libraries;
        }

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, project, "getLibraryList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "manage", "library", "-l"};

        String task = FrameworkMessages.get(FrameworkMessages.getLibraryList);
        ExecCommandUtil.printCommands(task, commands);

        try {
            String out = ExecCommandUtil.execProcessHandler(project, task, commands);
            if (out != null && !out.isEmpty()) {
                LibraryManagedParam.parseLibraryManaged(out, libraries);
            }
        } catch (ParseException e) {
            String log = "[morpheus get manage library error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, project, log);
        }

        return libraries;
    }

    public static @NotNull Map<String, LibraryManagedParam> getInstalledLibrary(@NotNull Project project) {
        Map<String, LibraryManagedParam> installedLibraries = new HashMap<>();
        if (!checkMorpheusCLI(project)) {
            return installedLibraries;
        }

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, project, "getLibraryList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "manage", "library", "-l"};

        String task = FrameworkMessages.get(FrameworkMessages.getLibraryList);
        ExecCommandUtil.printCommands(task, commands);

        try {
            String out = ExecCommandUtil.execProcessHandler(project, task, commands);
            if (out != null && !out.isEmpty()) {
                Map<String, Map<String, LibraryManagedParam>> libraries = new HashMap<>();
                LibraryManagedParam.parseLibraryManaged(out, libraries);
                for (String category : libraries.keySet()) {
                    Map<String, LibraryManagedParam> categoryLibraries = libraries.get(category);
                    for (String name : categoryLibraries.keySet()) {
                        LibraryManagedParam param = categoryLibraries.get(name);
                        Status canState = Status.fromString(param.getCanState());
                        if (canState == Status.APPLIED || canState == Status.UPDATABLE) {
                            installedLibraries.put(name, param);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            String log = "[morpheus get manage library error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, project, log);
        }

        return installedLibraries;
    }

    public static @NotNull Map<String, Map<String, LibraryManagedParam>> mananageLibrary(Project project, String jsonString) {
        Map<String, Map<String, LibraryManagedParam>> libraries = new HashMap<>();
        if (!checkMorpheusCLI(project)) {
            return libraries;
        }

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "getLibraryList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

//        if(jsonString.startsWith("[")) {
//            jsonString = jsonString.substring(1);
//        }
//        if(jsonString.endsWith("]")) {
//            jsonString = jsonString.length() > 1 ? jsonString.substring(0, jsonString.length() - 1) : "";
//        }

        String[] commands = {getMorpheusCommand(), "manage", "library", "-j", jsonString};

        String task = FrameworkMessages.get(FrameworkMessages.manLibrary);
        ExecCommandUtil.printCommands(task, commands);

        ExecCommandUtil.execProcessHandler(project, task, commands);
        libraries = getManageLibrary(project);

        return libraries;
    }

    public static boolean applyLicense(Project project, String appID) {
        if (!checkMorpheusCLI(project)) {
            return false;
        }

        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "applyLicense] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "license", appID};

        String task = FrameworkMessages.get(FrameworkMessages.applyLicense);
        ExecCommandUtil.printCommands(task, commands);

        String out = ExecCommandUtil.execProcessHandler(project, task, commands);
        if (out != null && !out.isEmpty()) {
            log(Log.LEVEL_DEBUG, project, out);
        }

        return true;
    }

    public static @NotNull Map<String, TemplateParam> getTemplateList() {
        Map<String, TemplateParam> templates = new HashMap<>();
        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "getTemplateList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "select", "template"};

        String task = FrameworkMessages.get(FrameworkMessages.getTemplateList);
        ExecCommandUtil.printCommands(task, commands);

        try {
            String out = ExecCommandUtil.execProcessHandler(task, null, commands);
            if (out != null && !out.isEmpty()) {
                TemplateParam.parseTemplate(out, templates);
            }
        } catch (ParseException e) {
            String log = "[morpheus select template error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, log);
        }

        return templates;
    }

    public static @Nullable TemplateInfoParam getTemplateInfo(String name) {
        if (PreferenceUtil.getShowDebugMessage()) {
            String version = getMorpheusCLIVersion();
            log(Log.LEVEL_DEBUG, "getTemplateList] getMorpheusCLIVersion() => [ " + version + " ]");
        }

        String[] commands = {getMorpheusCommand(), "select", "template", name};

        String task = FrameworkMessages.get(FrameworkMessages.getTemplateList);
        ExecCommandUtil.printCommands(task, commands);

        TemplateInfoParam templateInfo = null;
        try {
            String out = ExecCommandUtil.execProcessHandler(task, null, commands);
            if (out != null && !out.isEmpty()) {
                templateInfo = new TemplateInfoParam(out);
            }
        } catch (ParseException e) {
            String log = "[morpheus select template " + name + " error] " + e.getLocalizedMessage();
            log(Log.LEVEL_ERROR, log);
        }

        return templateInfo;
    }

    private static void log(int levelError, String log) {
        log(levelError, null, log);
    }

    private static void log(int levelError, Project project, String log) {
        CommonUtil.log(levelError, MorpheusCLIUtil.class, project, log);
    }
}

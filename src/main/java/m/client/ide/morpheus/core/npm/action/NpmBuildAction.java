package m.client.ide.morpheus.core.npm.action;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.messages.NLS;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import m.client.ide.morpheus.ui.message.UIMessages;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NpmBuildAction extends AnAction {
    private final String ENTER_STRING = OSUtil.isWindows() ? "\r\n" : Const.ENTER_STRING;

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        Project project = getEventProject(e);
        if (project == null) {
            return;
        }

        boolean isMorpheus = MorpheusConfigManager.isMorpheusProject(project);
        isMorpheus &= hasPackageFile(project);
        isMorpheus &= NpmUtils.hasNpmFile();

        e.getPresentation().setEnabled(isMorpheus);
    }

    private boolean hasPackageFile(Project project) {
        @Nullable File file = FileUtil.getChildFile(project, NpmConstants.PACKAGE_PATH);

        return file != null && file.exists();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, NpmBuildAction.class, null, "Project is null");
            return;
        }

        StringBuilder message =
                new StringBuilder(anActionEvent.getPresentation().getText() + " Selected!");
        // If an element is selected in the editor, add info about it.
        Navigatable selectedElement = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (selectedElement != null) {
            message.append("\nSelected Element: ").append(selectedElement);
        }
        String title = anActionEvent.getPresentation().getDescription();
        title = UIMessages.get(UIMessages.NPMBuild);

        @Nullable File file = FileUtil.getChildFile(project, NpmConstants.PACKAGE_PATH);
        try {
            runBuildCommand(project, getSelectedCommand(getNodeCommands(file)));
        } catch (ParseException e) {
            CommonUtil.log(Log.LEVEL_ERROR, NpmBuildAction.class, null, e.getLocalizedMessage());
        }
    }

    private Map.Entry<String, String> getSelectedCommand(Map<String, String> nodeCommands) {
        Map.Entry<String, String> result;

        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        NodeCommandSelectedDialog dialog = new NodeCommandSelectedDialog(nodeCommands);
        dialog.setSize(new Dimension(480, 360));
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);

        return dialog.getSelectedNodeCommand();
    }

    private Map<String, String> getNodeCommands(File file) throws ParseException {
        Map<String, String> result = new LinkedHashMap<String, String>();

        JSONParser parser = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        try {
            JSONObject root = (JSONObject) parser.parse(new FileReader(file));
            JSONObject scripts = (JSONObject) root.get(NpmConstants.SCRIPTS);
            if (scripts == null) {
                return result;
            }

            Set<String> keySet = scripts.keySet();
            List<String> keys = Lists.newArrayList(keySet).stream().map(obj -> obj.toString()).sorted(NpmUtils.comparator).collect(Collectors.toList());

            for (String key : keys) {
                String value = key;
                if (NpmUtils.NpmCommand.BUILD.name().equalsIgnoreCase(key)) {
                    value = NpmUtils.NpmCommand.BUILD.getCommandName();
                } else if (NpmUtils.NpmCommand.START.name().equalsIgnoreCase(key)) {
                    value = NpmUtils.NpmCommand.START.getCommandName();
                } else {
                    continue;
                }

                result.put(key, value);
            }

        } catch (IOException | ParseException e) {
            CommonUtil.log(Log.LEVEL_ERROR, NpmBuildAction.class, null, e.getLocalizedMessage());
        }

        return result;
    }

    private void runBuildCommand(Project project, Map.Entry<String, String> nodeCommand) {
        if (nodeCommand == null) {
            return;
        }

        String npm = NpmUtils.getNpmPathWithCheck(project);
        if (npm.isEmpty()) {
            CommonUtil.openErrorDialog("알림", NLS.bind("''{0}'' 실행 전 ''설치(install)'' 실행이 선행되어야 합니다.", nodeCommand.getValue()));
            return;
        }
        String commandLine = StringUtil.wrapDoubleQuatation(npm) + Const.SPACE_STRING +
                NpmConstants.RUN_COMMAND + Const.SPACE_STRING + nodeCommand.getKey() + ENTER_STRING;
        try {
            @NotNull ShellTerminalWidget shellWidget = ExecCommandUtil.getShellWidget(project, project.getName() + "(Npm)", project.getBasePath());
            shellWidget.executeCommand(commandLine);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}

package m.client.ide.morpheus.ui.dialog.librarymanager;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import m.client.ide.morpheus.core.component.checktreetable.*;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.CLILibraryManager;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryManagedParam;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import m.client.ide.morpheus.ui.dialog.LibraryManagerDialog;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.LibCheckboxTreeTable;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.LibTreeTableModel;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.LibTreeTableNode;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;

public class LibraryView implements TreeItemValueChangeListenre, TreeItemSelectionChangeListenre {
    private final boolean isHideButtons;
    private JComboBox<Object> comboLibs;
    private Map<String, Object> libFilters;
    private JScrollPane scrollPaneRes;
    private LibCheckboxTreeTable treeTableLib;
    private JPanel libPanel;
    private JButton applyButton;
    private JButton unApplyButton;
    private JButton refreshButton;
    private JPanel panelButtons;
    private LibraryManagerView libView;
    private ProgressIndicator progressIndicator;

    /**
     * List<LibraryParam> LibraryManagerComposite.java
     * save 시에 적용 시켜야 하는 library list.
     * Commnet		: Manifest editor 의 document 로 처리하여 dirty 와 함께 운영하기위해 분리하였으나
     * StructuredTextEditor 의 document 는 setContents 등 함수가 throw exception 으로 막혀있음
     */
    private List<LibraryParam> librariesToApply = new ArrayList<LibraryParam>();
    /**
     * List<LibraryParam> LibraryManagerComposite.java
     * save 시에 미적용 시켜야 하는 library list.
     * Commnet		: Manifest editor 의 document 로 처리하여 dirty 와 함께 운영하기위해 분리하였으나
     * StructuredTextEditor 의 document 는 setContents 등 함수가 throw exception 으로 막혀있음
     */
    private List<LibraryParam> librariesToUnApply = new ArrayList<LibraryParam>();

    public LibraryView() {
        this(false);
    }

    public LibraryView(boolean isHideButtons) {
        this.isHideButtons = isHideButtons;
        if (isHideButtons) {
            return;
        }

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUtil.log(Log.LEVEL_DEBUG, this.getClass().getName() + "] actionPerformed - " + e.toString());
                apply();
            }
        });
        unApplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUtil.log(Log.LEVEL_DEBUG, this.getClass().getName() + "] actionPerformed - " + e.toString());
                unApply();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUtil.log(Log.LEVEL_DEBUG, this.getClass().getName() + "] actionPerformed - " + e.toString());
                treeTableLib.setInput("Libraries", fillSdkItem(libView.getProject()));
            }
        });
    }

    protected void createUIComponents() {
        if (comboLibs == null) {
            comboLibs = new ComboBox<>();
        }

        treeTableLib = new LibCheckboxTreeTable(new LibTreeTableModel(new LibTreeTableNode("Libraries", "")));
        treeTableLib.addTreeItemValueChangeListener(this);
        treeTableLib.addTreeItemSelectionChangeListener(this);

        comboLibs.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item instanceof String) {
                    Object input = libFilters.get(item);
                    setInput((String) item, input);
                }
            }
        });

        if (this.isHideButtons) {
            panelButtons.setEnabled(false);
            panelButtons.setVisible(false);
        }
    }

    private void setInput(@NotNull String name, @Nullable Object input) {
        treeTableLib.setInput(name, input);
    }

    private Map<String, Map<String, LibraryParam>> fillSdkItem(Project project) {
        comboLibs.removeAll();
        libFilters = new HashMap<>();

        Map<String, Map<String, LibraryParam>> libraries = CLILibraryManager.getInstance(project).getLibraries();

        if (libraries != null && libraries.size() > 0) {
            comboLibs.addItem("All");
            libFilters.put("All", libraries);

            for (String key : libraries.keySet()) {
                Map<String, LibraryParam> list = libraries.get(key);

                comboLibs.addItem(key);
                libFilters.put(key, list);
            }
            comboLibs.setSelectedIndex(0);
        }

        updateButton();
        return libraries;
    }

    public LibCheckboxTreeTable getLibTree() {
        return treeTableLib;
    }

    public void setLibManagerView(LibraryManagerView manager) {
        this.libView = manager;

        treeTableLib.setInput("Libraries", fillSdkItem(libView.getProject()));
    }

    @Override
    public void treeItemValueChanged(@NotNull TreeItemValueChangeEvent e) {
        CommonUtil.log(Log.LEVEL_DEBUG, e.toString());
        if (e.getNode() != null && e.getColumn() == LibTreeTableModel.LibColumn.CHECKED.getValue()) {
            updateButton();
        }
    }

    @Override
    public void treeItemSelectionChanged(TreeItemSelectionChangeEvent e) {
        LibraryManagerDialog dialog = libView == null ? null : libView.getLibManagerDialog();
        if (dialog == null) {
            return;
        }

        AbstractCheckTreeTableNode node = e.getNode();
        Object object = node == null ? null : node.getUserObject();
        dialog.refreshLibraryInfo(object, object != null);
    }

    public void updateButton() {
        CommonUtil.log(Log.LEVEL_DEBUG, this.getClass().getName() + "] updateButton!");

        librariesToApply.clear();
        librariesToUnApply.clear();

        Object[] checkedItems = treeTableLib.getAllWhiteCheckedItems();
        for (int i = 0; i < checkedItems.length; i++) {
            Object item = checkedItems[i];
            if (item instanceof AbstractCheckTreeTableNode) {
                Object userObject = ((AbstractCheckTreeTableNode) item).getUserObject();
                if (userObject instanceof LibraryParam) {
                    LibraryParam libParam = (LibraryParam) userObject;
                    Status status = Status.fromString(libParam.getStatus());
                    if (status == Status.APPLIED) {
                        librariesToUnApply.add(libParam);
                    } else if (status == Status.UPDATABLE) {
                        librariesToApply.add(libParam);
                        librariesToUnApply.add(libParam);
                    } else {
                        librariesToApply.add(libParam);
                    }
                }
            }
        }

        applyButton.setEnabled(librariesToApply.size() > 0);
        unApplyButton.setEnabled(librariesToUnApply.size() > 0);
    }

    /**
     * Runnable With Progress 실행.
     */
    private void runOperation(Runnable runnable, String jobTitle) {
        Task.Modal modalTask = new Task.Modal(ProjectManager.getInstance().getDefaultProject(), jobTitle, false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                LibraryView.this.progressIndicator = progressIndicator;
                runnable.run();
            }
        };

        ProgressManager.getInstance().run(modalTask);

        updateButton();
    }

    private ProgressIndicator getCurrentProgressDialog() {
        return progressIndicator;
    }

    public void apply() {
//        if (containsPlugin(librariesToApply, PUSH_ID) && !helper.hasNode(manifestDocument, MProjectContents.P_PUSH)) {
//            PushSettingDialog dialog = new PushSettingDialog(getShell(), manifestPage, manifestDocument);
//            dialog.open();
//        }
//
//        if (containsPlugin(objs, ResourceDescriptionComposite.PREVENTION_ID) && !helper.hasNode(manifestDocument, MProjectContents.P_PREVENTION)) {
//            PreventionSettingDialog dialog = new PreventionSettingDialog(getShell(), manifestPage, manifestDocument);
//            dialog.open();
//        }
//
//        if (containsPlugin(objs, ResourceDescriptionComposite.ANDROID_QZ_ID)) {
//            AndroidOZViewerSettingDialog dialog = new AndroidOZViewerSettingDialog(getShell(), getProject());
//            dialog.open();
//        }
//
//        if (containsPlugin(objs, ResourceDescriptionComposite.IOS_QZ_ID)) {
//            IOSOZViewerSettingDialog dialog = new IOSOZViewerSettingDialog(getShell(), getProject());
//            dialog.open();
//        }

        final List<LibraryManagedParam> installList = new ArrayList<>();
        for (LibraryParam library : librariesToApply) {
            Status state = Status.fromString(library.getStatus());
            if (state == Status.NOTAPPLIED || state == Status.UPDATABLE) {
                LibraryManagedParam param = library.getLibraryManageParam();
                param.setCanState(false, true);
                param.setCurrentVersion(param.getLatestVersion());
                installList.add(param);
            }
        }

        runInstallProcess(installList, null);
    }

    private void runInstallProcess(List<LibraryManagedParam> installLibs, List<LibraryManagedParam> uninstallLibs) {
        runOperation(new ApplyRunnable(libView.getProject(), installLibs, uninstallLibs), "라이브러리 적용");
    }

    private boolean containsPlugin(List<LibraryParam> librariesToApply, String id) {
        return Arrays.stream(librariesToApply.toArray()).anyMatch(obj -> {
            if (obj instanceof LibraryParam) {
                LibraryParam library = (LibraryParam) obj;
                return library.getId().startsWith(id);
            }

            return false;
        });
    }

    public void unApply() {
        final List<LibraryManagedParam> unInstallList = new ArrayList<>();
        for (LibraryParam library : librariesToUnApply) {
            LibraryManagedParam param = library.getLibraryManageParam();
            param.setCanState(true, false);
            param.setCurrentVersion("");
            unInstallList.add(library.getLibraryManageParam());
        }

        runInstallProcess(null, unInstallList);
    }

    public class ApplyRunnable implements Runnable {
        private final Project project;
        private List<LibraryManagedParam> installList;
        private List<LibraryManagedParam> uninstallList;

        public ApplyRunnable(Project project, List<LibraryManagedParam> installList, List<LibraryManagedParam> uninstallList) {
            this.project = project;
            this.installList = installList;
            this.uninstallList = uninstallList;
        }

        @Override
        public void run() {
            CLILibraryManager libraryManager = CLILibraryManager.getInstance(project);
            if (uninstallList != null && uninstallList.size() > 0) {
                libraryManager.unApplyLibrary(this.project, uninstallList);
            }
            if (installList != null && installList.size() > 0) {
                libraryManager.applyLibrary(this.project, installList);
            }
        }
    }

}

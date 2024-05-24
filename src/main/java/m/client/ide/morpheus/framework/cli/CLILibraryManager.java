package m.client.ide.morpheus.framework.cli;

import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryManagedParam;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CLILibraryManager {

    private static CLILibraryManager instance;
    private final Map<String, Map<String, LibraryParam>> libraries;
    private Project project;

    private CLILibraryManager(Project project) {
        this.project = project;
        libraries = MorpheusCLIUtil.getLibraryList(project);
        updateLibraryStatus();
    }

    public static CLILibraryManager getInstance(Project project) {
        if(instance == null) {
            instance = new CLILibraryManager(project);
        }
        instance.project = project;
        return instance;
    }

    public Map<String, Map<String, LibraryParam>> getLibraries() {
        return getLibraries(true);
    }

    public Map<String, Map<String, LibraryParam>> getLibraries(boolean isUpdateStatus) {
        if(isUpdateStatus == true) {
            updateLibraryStatus();
        }
        return libraries;
    }

    public boolean updateLibraryStatus() {
        Map<String, Map<String, LibraryManagedParam>> libraryManList = MorpheusCLIUtil.getManageLibrary(project);
        if (libraryManList == null || libraryManList.size() == 0) {
            return false;
        }

        return updateLibraryStatus(libraryManList);
    }

    public boolean updateLibraryStatus(@NotNull Map<String, Map<String, LibraryManagedParam>> libraryManList) {
        for(String category : libraryManList.keySet()) {
            Map<String, LibraryManagedParam> manCategory = libraryManList.get(category);
            for(String key : manCategory.keySet()) {
                LibraryManagedParam libraryManageParam = manCategory.get(key);

                Map<String, LibraryParam> libraryCategory = libraries.get(category);
                if(libraryCategory != null) {
                    LibraryParam library = libraryCategory.get(key);
                    if(library != null) {
                        library.setManageParam(libraryManageParam);
                    }
                }
            }
        }

        return true;
    }

    public boolean applyLibrary(Project project, @NotNull List<LibraryManagedParam> libraryParams) {
        @NotNull Map<String, LibraryManagedParam> installedLibrary = MorpheusCLIUtil.getInstalledLibrary(project);
        JSONObject applyList = new JSONObject();

        for(LibraryManagedParam applyLibrary : libraryParams) {
            LibraryManagedParam param = installedLibrary.get(applyLibrary.getName());
            if (param != null) {
                installedLibrary.remove(applyLibrary.getName());
            }

            installedLibrary.put(applyLibrary.getName(), applyLibrary);
        }

        for(String name : installedLibrary.keySet()) {
            LibraryManagedParam library = installedLibrary.get(name);
            applyList.put(library.getFullname(), library.getCurrentVersion());
        }

        @NotNull Map<String, Map<String, LibraryManagedParam>> libraryManList =
                MorpheusCLIUtil.mananageLibrary(project, applyList.toJSONString(JSONStyle.LT_COMPRESS));
        return updateLibraryStatus(libraryManList);
    }

    public boolean unApplyLibrary(Project project, @NotNull List<LibraryManagedParam> libraryParams) {
        @NotNull Map<String, LibraryManagedParam> installedLibrary = MorpheusCLIUtil.getInstalledLibrary(project);
        JSONObject applyList = new JSONObject();
        for(LibraryManagedParam applyLibrary : libraryParams) {
            LibraryManagedParam param = installedLibrary.get(applyLibrary.getName());
            if (param != null) {
                installedLibrary.remove(applyLibrary.getName());
            }
        }

        for(String name : installedLibrary.keySet()) {
            LibraryManagedParam library = installedLibrary.get(name);
            applyList.put(library.getFullname(), library.getCurrentVersion());
        }

        @NotNull Map<String, Map<String, LibraryManagedParam>> libraryManList =
                MorpheusCLIUtil.mananageLibrary(project, applyList.toJSONString(JSONStyle.LT_COMPRESS));
        return updateLibraryStatus(libraryManList);
    }

    public boolean applyLicense(Project project, @NotNull String appID) {
        return MorpheusCLIUtil.applyLicense(project, appID);
    }
}

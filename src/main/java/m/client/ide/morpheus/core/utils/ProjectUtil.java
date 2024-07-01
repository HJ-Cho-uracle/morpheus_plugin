package m.client.ide.morpheus.core.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class ProjectUtil {

	public static void reloadProject(Project project) {
		SwingUtilities.invokeLater(() -> ProjectManager.getInstance().reloadProject(project));
	}

	// A plugin contains an example app, which needs to be opened when the native Android is to be edited.
	// 'Open in Android Studio' is requested.
	public static VirtualFile findProjectFile(@Nullable AnActionEvent e) {
		if (e != null) {
			final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
			if (file != null && file.exists()) {
				// We have a selection. Check if it is within a plugin.
				final Project project = e.getProject();
				assert (project != null);

				// Return null if this is an ios folder.
				if (isWithinIOSDirectory(file, project)) {
					return null;
				}

				if (isProjectFileName(file.getName())) {
					return getVirtualFileForProject(file);
				}
			}

			final Project project = e.getProject();
			if (project != null) {
				return getVirtualFileForProject(findStudioProjectFile(project));
			}
		}
		return null;
	}

	@Nullable
	public static VirtualFile findStudioProjectFile(@NotNull Project project) {
		Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();
		for (Module module : modules) {
			VirtualFile @NotNull [] roots = ModuleRootManager.getInstance(module).getContentRoots();
			for (VirtualFile child : roots) {
				if (isProjectFileName(child.getName())) {
					return child;
				}
				if (isAndroidDirectory(child)) {
					for (VirtualFile androidChild : child.getChildren()) {
						if (isProjectFileName(androidChild.getName())) {
							return androidChild;
						}
					}
				}
			}
		}
		return null;
	}

	protected static boolean isProjectFileName(String name) {
		// Note: If the project content is rearranged to have the android module file within the android directory, this will fail.
		return name.endsWith("_android.iml");
	}

	@Nullable
	private static VirtualFile getVirtualFileForProject(@Nullable VirtualFile file) {
		// Expect true: isProjectFileName(file.getName()), but some flexibility is allowed.
		if (file == null) {
			return null;
		}
		if (file.isDirectory()) {
			return isAndroidWithApp(file) ? file : null;
		}
		final VirtualFile dir = file.getParent();
		if (isAndroidWithApp(dir)) {
			// In case someone moves the .iml file, or the project organization gets rationalized.
			return dir;
		}
		VirtualFile project = dir.findChild("android");
		if (project != null && isAndroidWithApp(project)) {
			return project;
		}
		project = dir.findChild(".android");
		if (project != null && isAndroidWithApp(project)) {
			return project;
		}
		return null;
	}

	// Return true if the directory is named android and contains either an app (for applications) or a src (for plugins) directory.
	private static boolean isAndroidWithApp(@NotNull VirtualFile file) {
		return isAndroidDirectory(file) && (file.findChild("app") != null || file.findChild("src") != null);
	}

	public static boolean isAndroidDirectory(@NotNull VirtualFile file) {
		return file.isDirectory() && (file.getName().equals("android") || file.getName().equals(".android"));
	}

	public static boolean isWithinIOSDirectory(VirtualFile file, Project project) {
		final VirtualFile baseDir = project.getBaseDir();
		if (baseDir == null) {
			return false;
		}
		VirtualFile candidate = file;
		while (candidate != null && !baseDir.equals(candidate)) {
			if (isIOSDirectory(candidate)) {
				return true;
			}
			candidate = candidate.getParent();
		}
		return false;
	}

	public static boolean isIOSDirectory(@NotNull VirtualFile file) {
		return file.isDirectory() && (file.getName().equals("ios") || file.getName().equals(".ios"));
	}

	public static boolean hasIOSProject(Project project) {
		@Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);

		return iosProjectFolder != null && iosProjectFolder.exists() && iosProjectFolder.isDirectory();
	}
}

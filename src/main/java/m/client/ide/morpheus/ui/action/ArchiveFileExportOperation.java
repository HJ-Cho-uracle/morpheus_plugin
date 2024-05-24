package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ZipUtils;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * Operation for exporting a resource and its children to a new .zip or
 * .tar.gz file.
 *
 * @since 3.1
 */
@SuppressWarnings("restriction")
public class ArchiveFileExportOperation {
    private final Project project;
    private final File tempPath;
    private final File res;
    private String destinationFilename;

    private List<File> resourcesToExport;

    private ProgressIndicator progressIndicator;


    /**
     * Create an instance of this class.  Use this constructor if you wish to
     * export specific resources without a common parent resource
     *
     * @param project   com.intellij.openapi.project
     * @param resources java.util.Vector
     * @param filename  java.lang.String
     */
    public ArchiveFileExportOperation(@NotNull Project project, @NotNull File res, @NotNull List<File> resources, @NotNull String filename) throws IOException {

        // Eliminate redundancies in list of resources being exported
        Iterator<File> elementsEnum = resources.iterator();
        while (elementsEnum.hasNext()) {
            File currentResource = elementsEnum.next();
            if (isDescendent(res, resources, currentResource)) {
                elementsEnum.remove(); //Removes currentResource;
            }
        }

        this.project = project;
        this.res = res;
        resourcesToExport = resources;
        destinationFilename = filename;

        String dataPath = CommonUtil.getAppDataLocation();
        tempPath = new File(dataPath, "temp" + File.separator + project.getName());
        if (tempPath.exists()) {
            FileUtils.deleteDirectory(tempPath);
        }
        tempPath.mkdirs();
    }

    /**
     * Creates and returns the string that should be used as the name of the entry in the archive.
     *
     * @param exportResource the resource to export
     */
    private @NotNull String createDestinationName(@NotNull File exportResource) {
        Path fullPath = Path.of(exportResource.getAbsolutePath());
        return res.getParentFile().toPath().relativize(fullPath).toString();
    }

    /**
     * Export the passed resource to the destination .zip
     *
     * @param exportResource org.eclipse.core.resources.IResource
     */
    protected void exportResourceToTemp(@NotNull File exportResource) throws IOException {
        if (exportResource.isFile()) {
            String destinationName = createDestinationName(exportResource);
            progressIndicator.setText2(destinationName);

            FileUtils.copyFile(exportResource, new File(tempPath, destinationName));
        } else {
            File[] children = exportResource.listFiles();

            String destinationName = createDestinationName(exportResource);
            File destination = new File(tempPath, destinationName);
            destination.mkdirs();

            if(children != null) {
                for (int i = 0; i < children.length; i++) {
                    exportResourceToTemp(children[i]);
                }
            }
        }
    }

    /**
     * Export the resources contained in the previously-defined
     * resourcesToExport collection
     */
    protected void exportSpecifiedResourcesToTemp() throws IOException {
        Iterator<File> resources = resourcesToExport.iterator();

        while (resources.hasNext()) {
            File currentResource = resources.next();
            exportResourceToTemp(currentResource);
        }
    }

    /**
     * Answer a boolean indicating whether the passed child is a descendent
     * of one or more members of the passed resources collection
     *
     * @param resources java.util.Vector
     * @param child     org.eclipse.core.resources.IResource
     * @return boolean
     */
    protected boolean isDescendent(File root, List<File> resources, File child) {
        if (child == null || child.getParent() == null || root.getAbsolutePath().equals(child.getAbsolutePath())) {
            return false;
        }

        File parent = child.getParentFile();
        if (resources.contains(parent)) {
            return true;
        }

        return isDescendent(root, resources, parent);
    }

    /**
     * Export the resources that were previously specified for export
     * (or if a single resource was specified then export it recursively)
     */
    public void run(@NotNull ProgressIndicator progressIndicator)
            throws InvocationTargetException, InterruptedException {
        this.progressIndicator = progressIndicator;

        try {
            setText(UIMessages.get(UIMessages.ExportResourceTask));
            exportSpecifiedResourcesToTemp();

            compression();
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    private void compression() throws IOException {
        ZipUtils.compress(tempPath.getAbsolutePath(), destinationFilename, progressIndicator);
    }

    private void setText(String text) {
        if (progressIndicator != null) {
            progressIndicator.setText(text);
        }
    }

    private void setSubText(String text) {
        if (progressIndicator != null) {
            progressIndicator.setText2(text);
        }
    }
}

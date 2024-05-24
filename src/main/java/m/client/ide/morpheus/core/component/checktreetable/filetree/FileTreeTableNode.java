package m.client.ide.morpheus.core.component.checktreetable.filetree;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ui.ColumnInfo;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FileTreeTableNode extends AbstractCheckTreeTableNode {

    public FileTreeTableNode(String name, Object userObject) {
        super(name, userObject);
    }

    @Override
    public boolean getAllowsChildren() {
        if (userObject instanceof File) {
            return ((File) userObject).isDirectory();
        }
        return false;
    }

    protected void createChildren() {
        removeAllChildren();
        if (userObject instanceof File && ((File) userObject).isDirectory()) {
            File[] children = ((File) userObject).listFiles();
            Arrays.sort(children, (o1, o2) -> {
                if ((o1.isDirectory() && o2.isDirectory())
                        || (o1.isFile() && o2.isFile())) {
                    return o1.getName().compareTo(o2.getName());
                }
                if (o1.isDirectory() && o2.isFile()) {
                    return -1;
                }
                if (o1.isFile() && o2.isDirectory()) {
                    return 1;
                }
                return 0;
            });

            for (int i = 0; i < children.length; i++) {
                File child = children[i];
                add(new FileTreeTableNode(child.getName(), child));
            }
        } else if (userObject instanceof PsiDirectory) {
            PsiElement[] children = ((PsiDirectory) userObject).getChildren();
            Arrays.sort(children, (o1, o2) -> {
                if ((o1 instanceof PsiDirectory && o2 instanceof PsiDirectory)
                        || (o1 instanceof PsiFile && o2 instanceof PsiFile)) {
                    return ((PsiFileSystemItem) o1).getName().compareTo(((PsiFileSystemItem) o2).getName());
                }
                if (o1 instanceof PsiDirectory && o2 instanceof PsiFile) {
                    return -1;
                }
                if (o1 instanceof PsiFile && o2 instanceof PsiDirectory) {
                    return 1;
                }
                return 0;
            });

            for (int i = 0; i < children.length; i++) {
                PsiElement child = children[i];
                add(new FileTreeTableNode(((PsiFileSystemItem) child).getName(), child));
            }
        }
    }

    private String getTimeStr(long millisseconds) {
        Date date = new Date(millisseconds);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return formatter.format(date);
    }

    public String getDate() {
        if (userObject instanceof File && ((File) userObject).isFile()) {
            return getTimeStr(((File) userObject).lastModified());
        } else if (userObject instanceof PsiFile) {
            File file = new File(((PsiFile) userObject).getVirtualFile().getPath());
            return getTimeStr(file.lastModified());
        }
        return "";
    }

    public String getSize() {
        if (userObject instanceof File && ((File) userObject).isFile()) {
            return getFileSize(((File) userObject).length());
        } else if (userObject instanceof PsiFile) {
            File file = new File(((PsiFile) userObject).getVirtualFile().getPath());
            return getFileSize(file.length());
        }
        return "";
    }

    private String getFileSize(long length) {
        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;

        if (length < sizeKb)
            return length + "바이트";
        else if (length < sizeMo)
            return df.format(length / sizeKb) + "KB";
        else if (length < sizeGo)
            return df.format(length / sizeMo) + "MB";
        else if (length < sizeTerra)
            return df.format(length / sizeGo) + "GB";

        return "";
    }

    public int getIndexOfChild(Object child) {
        if (child instanceof TreeNode)
            return getIndex((TreeNode) child);

        return 0;
    }

    public PsiFile getPsiFile() {
        if (userObject instanceof PsiFile) {
            return (PsiFile) userObject;
        } else if (userObject instanceof File) {
            File file = (File) userObject;
            @NotNull Project project = ProjectManager.getInstance().getDefaultProject();
            @Nullable VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByNioPath(file.toPath());
            if (virtualFile != null) {
                return PsiUtilCore.getPsiFile(project, virtualFile);
            }
        }

        return null;
    }

    public static class FileColumnInfo extends ColumnInfo {
        private final FileTreeTableModel.FileColumn column;

        public FileColumnInfo(FileTreeTableModel.FileColumn name) {
            super(name.toString());
            this.column = name;
        }

        @Nullable
        @Override
        public Object valueOf(Object object) {
            if (object instanceof FileTreeTableNode) {
                FileTreeTableNode node = (FileTreeTableNode) object;
                switch (column) {
                    case NAME:
                        System.out.println(getName() + "] " + node.getName());
                        return node.getName();
                    case DATE:
                        System.out.println(getName() + "] " + node.getDate());
                        return node.getDate();
                    case SIZE:
                        System.out.println(getName() + "] " + node.getSize());
                        return node.getSize();
                }
            }
            return object == null ? "" : object.toString();
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}

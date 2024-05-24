package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

import com.intellij.util.ui.ColumnInfo;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LibTreeTableNode extends AbstractCheckTreeTableNode {


    public LibTreeTableNode(String name, Object userObject) {
        super(name, userObject);
    }

    @Override
    public boolean getAllowsChildren() {
        return userObject instanceof Map || userObject instanceof List;
    }

    protected void createChildren() {
        removeAllChildren();
        if(userObject instanceof Map) {
            Object[] keys = ((Map) userObject).keySet().toArray();
            Arrays.sort(keys, (o1, o2) -> {
                if(o1 instanceof String && o2 instanceof String) {
                    return ((String)o1).compareTo((String) o2);
                }
                if(o1 instanceof String) {
                    return -1;
                }
                if(!(o1 instanceof String) && (o2 instanceof String)) {
                    return 1;
                }
                return 0;
            });
            for (Object key : keys) {
                if (key instanceof String) {
                    add(new LibTreeTableNode((String) key, ((Map) userObject).get(key)));
                }
            }
        } else if(userObject instanceof List) {
            ((List) userObject).sort((o1, o2) -> {
                if(o1 instanceof String && o2 instanceof String) {
                    return ((String)o1).compareTo(((String) o2));
                }
                if(o1 instanceof String && !(o2 instanceof String)) {
                    return 1;
                }
                if(!(o1 instanceof String) && o2 instanceof String) {
                    return -1;
                }
                return 0;
            });
            for (Object res : (List) userObject) {
                if (res instanceof String) {
                    add(new LibTreeTableNode(((String) res), res));
                }
            }
        }
    }

    public String getRevision() {
        if(userObject instanceof String) {
            return (String) userObject;
        } else if(userObject instanceof LibraryParam) {
            return ((LibraryParam)userObject).getRevision();
        }
        return "";
    }

    public java.io.Serializable getStatus() {
        if(userObject instanceof String) {
            return (String) userObject;
        } else if(userObject instanceof LibraryParam) {
            return ((LibraryParam)userObject).getStatus();
        }
        return "";
    }

    public int getIndexOfChild(Object child) {
        if(child instanceof TreeNode)
            return getIndex((TreeNode) child);

        return 0;
    }

    public static class LibColumnInfo extends ColumnInfo {
        private final LibTreeTableModel.LibColumn column;

        public LibColumnInfo(LibTreeTableModel.LibColumn libColumn) {
            super(libColumn.toString());
            this.column = libColumn;
        }

        @Nullable
        @Override
        public Object valueOf(Object object) {
            if(object instanceof LibTreeTableNode) {
                LibTreeTableNode node = (LibTreeTableNode) object;
                if(node.userObject instanceof List) {
                    if(column == LibTreeTableModel.LibColumn.NAME) {
                        return node.getName();
                    } else {
                        return "";
                    }
                } else if(node.userObject instanceof LibraryParam) {
                    LibraryParam libraryParam = (LibraryParam) node.userObject;
                    switch (column) {
                        case NAME:
                            System.out.println(getName() + "] " + libraryParam.getName());
                            return libraryParam.getName();
                        case REVISION:
                            System.out.println(getName() + "] " + libraryParam.getRevision());
                            return libraryParam.getRevision();
                        case STATUS:
                            System.out.println(getName() + "] " + libraryParam.getStatus());
                            return libraryParam.getStatus();
                    }
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

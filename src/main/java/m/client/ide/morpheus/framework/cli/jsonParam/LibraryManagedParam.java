package m.client.ide.morpheus.framework.cli.jsonParam;

import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.Status;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LibraryManagedParam extends AbstractJsonElement {
    private static final String key_fullname = "fullname";
    private static final String key_name = "name";
    private static final String key_version = "version";
    private static final String key_can = "can";

    private String          fullname;
    private String          name;
    private Version         version;
    private Can             can;
    private String          category;

    public LibraryManagedParam(JSONObject jsonObject) {
        super(jsonObject);
    }

    public LibraryManagedParam(String jsonString) throws ParseException {
        super(jsonString);
    }

    public static void parseLibraryManaged(String jsonString, Map<String, Map<String, LibraryManagedParam>> libraries) throws ParseException {
        JSONParser parser = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object object = parser.parse((String) jsonString);
        if (object instanceof JSONObject) {
            LibraryManagedParam library = new LibraryManagedParam((JSONObject) object);
            putLibraryManaged(libraries, library);
        } else if (object instanceof JSONArray) {
            JSONArray objects = (JSONArray) object;
            Iterator iterator = objects.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element instanceof JSONObject) {
                    JSONObject json = (JSONObject) element;
                    LibraryManagedParam library = new LibraryManagedParam((JSONObject) json);
                    putLibraryManaged(libraries, library);
                }
            }
        }
    }

    public static @NotNull String getJSONString(Map<String, Map<String, LibraryManagedParam>> libraries) {
        JSONArray jsonArray = new JSONArray();
        for(String key : libraries.keySet()) {
            Map<String, LibraryManagedParam> category = libraries.get(key);
            for(String name : category.keySet()) {
                LibraryManagedParam libraryManagedParam = category.get(name);
                if(libraryManagedParam != null) {
                    jsonArray.add(libraryManagedParam.getJSONObject());
                }
            }
        }

        return jsonArray.toJSONString(JSONStyle.LT_COMPRESS);
    }

    private static void putLibraryManaged(Map<String, Map<String, LibraryManagedParam>> libraries, @NotNull LibraryManagedParam library) {
        Map<String, LibraryManagedParam> category = libraries.get(library.getCategory());
        if(category == null) {
            category = new HashMap<>();
            libraries.put(library.getCategory(), category);
        }
        if(category.get(library.getName()) != null) {
            category.remove(library.getName());
        }
        category.put(library.getName(), library);
    }

    public void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_fullname);
        setFullname(object != null ? object.toString() : "");
        object = jsonObject.get(key_name);
        name = object != null ? object.toString() : "";
        object = jsonObject.get(key_version);
        if (object instanceof JSONObject) {
            version = new Version((JSONObject) object);
        } else {
            version = null;
        }
        object = jsonObject.get(key_can);
        if (object instanceof JSONObject) {
            can = new Can((JSONObject) object);
        } else {
            can = null;
        }
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_fullname, getFullname());
        jsonObject.put(key_name, name);
        jsonObject.put(key_version, version.getJSONObject());
        jsonObject.put(key_can, can.getJSONObject());

        return jsonObject;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(@NotNull String fullname) {
        this.fullname = fullname;

        String paths = fullname.indexOf('@') == 0 ? fullname.substring(1) : fullname;
        String[] tokens = paths.split(File.separator);
        if (tokens.length == 1) {
            this.category = tokens[0];
        } else if (tokens.length > 1) {
            this.category = tokens[1];
        } else {
            this.category = "";
        }

        tokens = category.split("-");
        if (tokens.length > 1) {
            this.category = tokens[0];
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public String getCurrentVersion() {
        return version == null ? "" : version.current;
    }

    public void setCurrentVersion(String currentVersion) {
        version.current = currentVersion;
    }

    public String getLatestVersion() {
        return version == null ? "" : version.latest;
    }

    public Version getVersion() {
        return version;
    }

    public String getCanState() {
        if(can == null) {
            return Status.NOTAPPLIED.toString();
        }

        if(can.apply == false) {
            return Status.APPLIED.toString();
        } else if(can.remove) {
            return Status.UPDATABLE.toString() + "(" + getLatestVersion() + ")";
        } else {
            return Status.NOTAPPLIED.toString();
        }
    }

    public void setCanState(boolean isApply, boolean isRemove) {
        can.apply = isApply;
        can.remove = isRemove;
    }
}

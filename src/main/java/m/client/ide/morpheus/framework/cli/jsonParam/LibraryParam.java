package m.client.ide.morpheus.framework.cli.jsonParam;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.application.ApplicationManager;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.Status;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LibraryParam extends AbstractJsonElement {

    private static final String key_id = "id";
    private static final String key_downloadUrl = "downloadUrl";
    private static final String key_path = "path";
    private static final String key_repository = "repository";
    private static final String key_format = "format";
    private static final String key_contentType = "contentType";
    private static final String key_lastModified = "lastModified";
    private static final String key_lastDownloaded = "lastDownloaded";
    private static final String key_uploader = "uploader";
    private static final String key_uploaderIp = "uploaderIp";
    private static final String key_fileSize = "fileSize";
    private static final String key_blobCreated = "blobCreated";

    private String id;
    private String downloadUrl;
    private String path;
    private String repository;
    private String format;
    private Checksum checksum;
    private String contentType;
    private String lastModified;
    private String lastDownloaded;
    private String uploader;
    private String uploaderIp;
    private String fileSize;
    private String blobCreated;
    private Npm npm;
    private String category;
    private String name;
    private LibraryManagedParam libraryManageParam;

    public LibraryParam(JSONObject jsonObject) {
        super(jsonObject);
    }

    public LibraryParam(String jsonString) throws ParseException {
        super(jsonString);
    }

    public static void parseLibrary(String jsonString, Map<String, Map<String, LibraryParam>> libraries) throws ParseException {
        JSONParser parser = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
        Object object = parser.parse(((String) jsonString).getBytes());
        if (object instanceof JSONObject) {
            LibraryParam library = new LibraryParam((JSONObject) object);
            putLibrary(libraries, library);
        } else if (object instanceof JSONArray) {
            JSONArray objects = (JSONArray) object;
            Iterator iterator = objects.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element instanceof JSONObject) {
                    JSONObject json = (JSONObject) element;
                    LibraryParam library = new LibraryParam((JSONObject) json);
                    putLibrary(libraries, library);
                }
            }
        }
    }

    private static void putLibrary(@NotNull Map<String, Map<String, LibraryParam>> libraries, @NotNull LibraryParam library) {
        Map<String, LibraryParam> category = libraries.get(library.getCategory());
        if(category == null) {
            category = new HashMap<> ();
            libraries.put(library.getCategory(), category);
        }
        if(category.get(library.getName()) != null) {
            category.remove(library.getName());
        }
        category.put(library.getName(), library);
    }

    public void update(@NotNull JSONObject json) {
        Object object = json.get(key_id);
        id = object != null ? object.toString() : "";
        object = json.get(key_downloadUrl);
        downloadUrl = object != null ? object.toString() : "";
        object = json.get(key_path);
        setPath(object != null ? object.toString() : "");
        object = json.get(key_repository);
        repository = object != null ? object.toString() : "";
        object = json.get(key_format);
        format = object != null ? object.toString() : "";
        object = json.get(Checksum.key);
        if (object instanceof JSONObject) {
            checksum = new Checksum((JSONObject) object);
        } else {
            checksum = null;
        }

        object = json.get(key_contentType);
        contentType = object != null ? object.toString() : "";
        object = json.get(key_lastModified);
        lastModified = object != null ? object.toString() : "";
        object = json.get(key_lastDownloaded);
        lastDownloaded = object != null ? object.toString() : "";
        object = json.get(key_uploader);
        uploader = object != null ? object.toString() : "";
        object = json.get(key_uploaderIp);
        uploaderIp = object != null ? object.toString() : "";
        object = json.get(key_fileSize);
        fileSize = object != null ? object.toString() : "";
        object = json.get(key_blobCreated);
        blobCreated = object != null ? object.toString() : "";
        object = json.get(Npm.key);
        if (object instanceof JSONObject) {
            npm = new Npm((JSONObject) object);
            name = npm.getName();
        } else {
            npm = null;
        }
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_id, id);
        jsonObject.put(key_downloadUrl, downloadUrl);
        jsonObject.put(key_path, path);
        jsonObject.put(key_repository, repository);
        jsonObject.put(key_format, format);
        jsonObject.put(Checksum.key, checksum.getJSONObject());

        jsonObject.put(key_contentType, contentType);
        jsonObject.put(key_lastModified, lastModified);
        jsonObject.put(key_lastDownloaded, lastDownloaded);
        jsonObject.put(key_uploader, uploader);
        jsonObject.put(key_uploaderIp, uploaderIp);
        jsonObject.put(key_fileSize, fileSize);
        jsonObject.put(key_blobCreated, blobCreated);
        jsonObject.put(Npm.key, npm.getJSONObject());

        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
        String paths = path.indexOf('@') == 0 ? path.substring(1) : path;
        String[] tokens = paths.split(File.separator);
        if (tokens.length == 1) {
            this.category = tokens[0];
        } else if (tokens.length > 1) {
            this.category = tokens[1];
        } else {
            this.category = "";
        }

        this.name = tokens[tokens.length-1];
        tokens = category.split("-");
        if (tokens.length > 1) {
            this.category = tokens[0];
        }
    }

    public String getCategory() {
        return category;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastDownloaded() {
        return lastDownloaded;
    }

    public void setLastDownloaded(String lastDownloaded) {
        this.lastDownloaded = lastDownloaded;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUploaderIp() {
        return uploaderIp;
    }

    public void setUploaderIp(String uploaderIp) {
        this.uploaderIp = uploaderIp;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getBlobCreated() {
        return blobCreated;
    }

    public void setBlobCreated(String blobCreated) {
        this.blobCreated = blobCreated;
    }

    public @NotNull Npm getNpm() {
        return npm;
    }

    public void setNpm(Npm npm) {
        this.npm = npm;
    }

    public String getRevision() {
        if(libraryManageParam != null) {
            String version = libraryManageParam.getCurrentVersion();
            if(version == null || version.isEmpty()) {
                version = libraryManageParam.getLatestVersion();
            }
            if(version != null && !version.isEmpty()) {
                return version;
            }
        }

        return npm == null ? "" : npm.getVersion();
    }

    public static void testLibraryParam() {
        String fileName = "/json/libraries.json";
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                readResFile(fileName);
            }
        });
    }

    private static void readResFile(String fileName) {
        try {
            InputStream in = null;
            try {
                in = LibraryParam.class.getResourceAsStream(fileName);
                byte[] buff = new byte[1024];

                String jsonString = "";
                for (int count = 0; (count = in.read(buff)) != -1; ) {
                    jsonString += new String(buff, 0, count);
                }

                Map<String, Map<String, LibraryParam>> libraries = new HashMap<>();

                parseLibrary(jsonString, libraries);

                for(String categoryKey : libraries.keySet()) {
                    Map<String, LibraryParam> category = libraries.get(categoryKey);
                    for(String libraryKey : category.keySet()) {
                        CommonUtil.log(Log.LEVEL_DEBUG, LibraryParam.class, null, libraryKey + " : " + category.get(libraryKey).getId());
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } finally {
                if (in != null) in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStatus() {
        return libraryManageParam != null ? libraryManageParam.getCanState() : Status.NOTAPPLIED.toString();
    }

    public void setManageParam(LibraryManagedParam libraryManageParam) {
        this.libraryManageParam = libraryManageParam;
    }

    public LibraryManagedParam getLibraryManageParam() {
        return libraryManageParam;
    }
}

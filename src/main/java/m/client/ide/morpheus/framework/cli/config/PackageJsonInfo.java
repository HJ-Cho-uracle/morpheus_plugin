package m.client.ide.morpheus.framework.cli.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageJsonInfo {
    static final @NotNull String key_name = "name";
    static final @NotNull String key_private = "private";
    static final @NotNull String key_version = "version";
    static final @NotNull String key_description = "description";
    static final @NotNull String key_scripts = "scripts";
    static final @NotNull String key_sync = "sync";
    static final @NotNull String key_postinstall = "postinstall";
    static final @NotNull String key_keywords = "keywords";
    static final @Nullable String key_dependencies = "dependencies";
    static final @Nullable String key_devDependencies = "devDependencies";
    static final @Nullable String key_author = "author";
    static final @Nullable String key_license = "license";

    private @NotNull String projectName;
    private @NotNull boolean isPrivate;
    private @NotNull String version;
    private @NotNull String description;
    private ScriptsInfo scripts;
    private List<String> keyworks;
    private @Nullable Object dependencies;
    private @Nullable Object devDependencies;
    private @Nullable String author;
    private @Nullable String license;

    public static class ScriptsInfo {
        String sync;
        String postInstall;

        public ScriptsInfo(String sync, String postInstall) {
            this.sync = sync;
            this.postInstall = postInstall;
        }

        public ScriptsInfo getCopy() {
            return new ScriptsInfo(sync, postInstall);
        }
    }

    public PackageJsonInfo() {
        clear();
    }


    public PackageJsonInfo(@Nullable @org.jetbrains.annotations.SystemIndependent @org.jetbrains.annotations.NonNls String basePath) {
        this();
        File baseFolder = new File(basePath);
        setProjectName(baseFolder.getName());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");

        sb.append(key_name).append(" = ").append(projectName).append("\n");
        sb.append(key_private).append(" = ").append(isPrivate).append("\n");
        sb.append(key_version).append(" = ").append(version).append("\n");
        sb.append(key_description).append(" = ").append(description).append("\n");
        sb.append(key_scripts).append(" = ").append(scripts).append("\n");
        sb.append(key_keywords).append(" = ").append(keyworks).append("\n");
        sb.append(key_dependencies).append(" = ").append(dependencies).append("\n");
        sb.append(key_devDependencies).append(" = ").append(devDependencies).append("\n");
        sb.append(key_author).append(" = ").append(author).append("\n");
        sb.append(key_license).append(" = ").append(license).append("\n");

        return sb.toString();
    }

    public void clear() {
        projectName = "";
        isPrivate = true;
        version = "1.0.0";
        description = "Hybrid Mobill Application Morpheus";
        scripts = new ScriptsInfo("morpheus sync", "morpheus sync");
        keyworks = new ArrayList<>();
        dependencies = null;
        devDependencies = null;
        author = "";
        license = "ISC";
    }

    public void update(@NotNull PackageJsonInfo info) {
        projectName = info.projectName;
        isPrivate = info.isPrivate;
        version = info.version;
        description = info.description;
        scripts = info.scripts.getCopy();
        keyworks = info.keyworks;
        dependencies = info.dependencies;
        devDependencies = info.devDependencies;
        author = info.author;
        license = info.license;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public @NotNull boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate == null ?  true : isPrivate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ScriptsInfo getScripts() {
        return scripts;
    }

    public void setScripts(ScriptsInfo scripts) {
        this.scripts = scripts;
    }

    public List<String> getKeywords() {
        return keyworks;
    }

    public void setKeywords(List<String> keywords) {
        this.keyworks = keywords;
    }

    public Object getDependencies() {
        return dependencies != null ? dependencies : new JSONObject();
    }

    public void setDependencies(Object dependencies) {
        this.dependencies = dependencies;
    }

    public @Nullable Object getDevDependencies() {
        return devDependencies != null ? devDependencies : new JSONObject();
    }

    public void setDevDependencies(Object devDependencies) {
        this.devDependencies = devDependencies;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}

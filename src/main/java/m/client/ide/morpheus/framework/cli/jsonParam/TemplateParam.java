package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class TemplateParam extends AbstractJsonElement {

    private static final String key_id = "id";
    private static final String key_owner = "owner";
    private static final String key_name = "name";
    private static final String key_full_name = "full_name";
    private static final String key_description = "description";
    private static final String key_empty = "empty";
    private static final String key_private = "private";
    private static final String key_fork = "fork";
    private static final String key_parent = "parent";
    private static final String key_mirror = "mirror";
    private static final String key_size = "size";
    private static final String key_language = "language";
    private static final String key_language_url = "language_url";
    private static final String key_html_url = "html_url";
    private static final String key_link = "link";
    private static final String key_ssh_url = "ssh_url";
    private static final String key_clone_url = "clone_url";
    private static final String key_original_url = "original_url";
    private static final String key_website = "website";
    private static final String key_stars_count = "stars_count";
    private static final String key_forks_count = "forks_count";
    private static final String key_watchers_count = "watchers_count";
    private static final String key_open_issue_count = "open_issue_count";
    private static final String key_open_pr_counter = "open_pr_counter";
    private static final String key_release_counter = "release_counter";
    private static final String key_default_branch = "default_branch";
    private static final String key_archived = "archived";
    private static final String key_created_at = "created_at";
    private static final String key_updated_at = "updated_at";
    private static final String key_permissions = "permissions";
    private static final String key_has_issues = "has_issues";
    private static final String key_internal_tracker = "internal_tracker";
    private static final String key_has_wiki = "has_wiki";
    private static final String key_has_pull_requests = "has_pull_requests";
    private static final String key_has_projects = "has_projects";
    private static final String key_ignore_whitespace_conflicts = "ignore_whitespace_conflicts";
    private static final String key_allow_merge_commits = "allow_merge_commits";
    private static final String key_allow_rebase = "allow_rebase";
    private static final String key_allow_rebase_explicit = "allow_rebase_explicit";
    private static final String key_allow_squash_merge = "allow_squash_merge";
    private static final String key_allow_rebase_update = "allow_rebase_update";
    private static final String key_default_delete_branch_after_merge = "default_delete_branch_after_merge";
    private static final String key_default_merge_style = "default_merge_style";
    private static final String key_default_allow_maintainer_edit = "default_allow_maintainer_edit";
    private static final String key_avatar_url = "avatar_url";
    private static final String key_internal = "internal";
    private static final String key_mirror_interval = "mirror_interval";
    private static final String key_mirror_updated = "mirror_updated";
    private static final String key_repo_transfer = "repo_transfer";
    private Integer id;
    private Owner owner;
    private String name;
    private String full_name;
    private String description;
    private Boolean empty;
    private Boolean privat;
    private Boolean fork;
    private @Nullable String parent;
    private Boolean mirror;
    private Integer size;
    private String language;
    private String language_url;
    private String html_url;
    private String link;
    private String ssh_url;
    private String clone_url;
    private String original_url;
    private String website;
    private Integer stars_count;
    private Integer forks_count;
    private Integer watchers_count;
    private Integer open_issue_count;
    private Integer open_pr_counter;
    private Integer release_counter;
    private String default_branch;
    private Boolean archived;
    private String created_at;
    private String updated_at;
    private Permissions permissions;
    private Boolean has_issues;
    private InternalTracker internal_tracker;
    private Boolean has_wiki;
    private Boolean has_pull_requests;
    private Boolean has_projects;
    private Boolean ignore_whitespace_conflicts;
    private Boolean allow_merge_commits;
    private Boolean allow_rebase;
    private Boolean allow_rebase_explicit;
    private Boolean allow_squash_merge;
    private Boolean allow_rebase_update;
    private Boolean default_delete_branch_after_merge;
    private String default_merge_style;
    private Boolean default_allow_maintainer_edit;
    private @Nullable String avatar_url;
    private Boolean internal;
    private @Nullable String mirror_interval;
    private String mirror_updated;
    private @Nullable String repo_transfer;

    public TemplateParam(JSONObject jsonObject) {
        super(jsonObject);
    }

    public TemplateParam(String jsonString) throws ParseException {
        super(jsonString);
    }

    public static void parseTemplate(String jsonString, Map<String, TemplateParam> templates) throws ParseException {
        JSONParser parser = new JSONParser(JSONPARSER_MODE);
        Object object = parser.parse((String) jsonString);
        if (object instanceof JSONObject) {
            TemplateParam template = new TemplateParam((JSONObject) object);
            templates.put(template.name, template);
        } else if (object instanceof JSONArray) {
            JSONArray objects = (JSONArray) object;
            Iterator iterator = objects.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element instanceof JSONObject) {
                    JSONObject json = (JSONObject) element;
                    TemplateParam template = new TemplateParam((JSONObject) json);
                    templates.put(template.name, template);
                }
            }
        }
    }

    public void update(@NotNull JSONObject json) {
        Object object = json.get(key_id);
        id = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_owner);
        if (object instanceof JSONObject) {
            owner = new Owner((JSONObject) object);
        } else {
            owner = null;
        }

        object = json.get(key_name);
        name = object != null ? object.toString() : "";
        object = json.get(key_full_name);
        full_name = object != null ? object.toString() : "";
        object = json.get(key_description);
        description = object != null ? object.toString() : "";
        object = json.get(key_empty);
        empty = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_private);
        privat = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_fork);
        fork = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_parent);
        parent = object != null ? object.toString() : "";
        object = json.get(key_mirror);
        mirror = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_size);
        size = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_language);
        language = object != null ? object.toString() : "";
        object = json.get(key_language_url);
        language_url = object != null ? object.toString() : "";
        object = json.get(key_html_url);
        html_url = object != null ? object.toString() : "";
        object = json.get(key_link);
        link = object != null ? object.toString() : "";
        object = json.get(key_ssh_url);
        ssh_url = object != null ? object.toString() : "";
        object = json.get(key_clone_url);
        clone_url = object != null ? object.toString() : "";
        object = json.get(key_original_url);
        original_url = object != null ? object.toString() : "";
        object = json.get(key_website);
        website = object != null ? object.toString() : "";
        object = json.get(key_stars_count);
        stars_count = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_forks_count);
        forks_count = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_watchers_count);
        watchers_count = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_open_issue_count);
        open_issue_count = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_open_pr_counter);
        open_pr_counter = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_release_counter);
        release_counter = object instanceof Integer ? (Integer) object : 0;
        object = json.get(key_default_branch);
        default_branch = object != null ? object.toString() : "";
        object = json.get(key_archived);
        archived = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_created_at);
        created_at = object != null ? object.toString() : "";
        object = json.get(key_updated_at);
        updated_at = object != null ? object.toString() : "";
        object = json.get(key_permissions);
        if (object instanceof JSONObject) {
            permissions = new Permissions((JSONObject) object);
        } else {
            permissions = null;
        }

        object = json.get(key_has_issues);
        has_issues = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_internal_tracker);
        if (object instanceof JSONObject) {
            internal_tracker = new InternalTracker((JSONObject) object);
        } else {
            internal_tracker = null;
        }

        object = json.get(key_has_wiki);
        has_wiki = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_has_pull_requests);
        has_pull_requests = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_has_projects);
        has_projects = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_ignore_whitespace_conflicts);
        ignore_whitespace_conflicts = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_allow_merge_commits);
        allow_merge_commits = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_allow_rebase);
        allow_rebase = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_allow_rebase_explicit);
        allow_rebase_explicit = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_allow_squash_merge);
        allow_squash_merge = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_allow_rebase_update);
        allow_rebase_update = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_default_delete_branch_after_merge);
        default_delete_branch_after_merge = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_default_merge_style);
        default_merge_style = object != null ? object.toString() : "";
        object = json.get(key_default_allow_maintainer_edit);
        default_allow_maintainer_edit = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_avatar_url);
        avatar_url = object != null ? object.toString() : "";
        object = json.get(key_internal);
        internal = object instanceof Boolean ? (Boolean) object : false;
        object = json.get(key_mirror_interval);
        mirror_interval = object != null ? object.toString() : "";
        object = json.get(key_mirror_updated);
        mirror_updated = object != null ? object.toString() : "";
        object = json.get(key_repo_transfer);
        repo_transfer = object != null ? object.toString() : "";
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_id, id);
        jsonObject.put(key_owner, owner.getJSONObject());
        jsonObject.put(key_name, name);
        jsonObject.put(key_full_name, full_name);
        jsonObject.put(key_description, description);
        jsonObject.put(key_empty, empty);
        jsonObject.put(key_private, privat);
        jsonObject.put(key_fork, fork);
        jsonObject.put(key_parent, parent);
        jsonObject.put(key_mirror, mirror);
        jsonObject.put(key_size, size);
        jsonObject.put(key_language, language);
        jsonObject.put(key_language_url, language_url);
        jsonObject.put(key_html_url, html_url);
        jsonObject.put(key_link, link);
        jsonObject.put(key_ssh_url, ssh_url);
        jsonObject.put(key_clone_url, clone_url);
        jsonObject.put(key_original_url, original_url);
        jsonObject.put(key_website, website);
        jsonObject.put(key_stars_count, stars_count);
        jsonObject.put(key_forks_count, forks_count);
        jsonObject.put(key_watchers_count, watchers_count);
        jsonObject.put(key_open_issue_count, open_issue_count);
        jsonObject.put(key_open_pr_counter, open_pr_counter);
        jsonObject.put(key_release_counter, release_counter);
        jsonObject.put(key_default_branch, default_branch);
        jsonObject.put(key_archived, archived);
        jsonObject.put(key_created_at, created_at);
        jsonObject.put(key_updated_at, updated_at);
        jsonObject.put(key_permissions, permissions.getJSONObject());
        jsonObject.put(key_has_issues, has_issues);
        jsonObject.put(key_internal_tracker, internal_tracker.getJSONObject());
        jsonObject.put(key_has_wiki, has_wiki);
        jsonObject.put(key_has_pull_requests, has_pull_requests);
        jsonObject.put(key_has_projects, has_projects);
        jsonObject.put(key_ignore_whitespace_conflicts, ignore_whitespace_conflicts);
        jsonObject.put(key_allow_merge_commits, allow_merge_commits);
        jsonObject.put(key_allow_rebase, allow_rebase);
        jsonObject.put(key_allow_rebase_explicit, allow_rebase_explicit);
        jsonObject.put(key_allow_squash_merge, allow_squash_merge);
        jsonObject.put(key_allow_rebase_update, allow_rebase_update);
        jsonObject.put(key_default_delete_branch_after_merge, default_delete_branch_after_merge);
        jsonObject.put(key_default_merge_style, default_merge_style);
        jsonObject.put(key_default_allow_maintainer_edit, default_allow_maintainer_edit);
        jsonObject.put(key_avatar_url, avatar_url);
        jsonObject.put(key_internal, internal);
        jsonObject.put(key_mirror_interval, mirror_interval);
        jsonObject.put(key_mirror_updated, mirror_updated);
        jsonObject.put(key_repo_transfer, repo_transfer);

        return jsonObject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public Boolean getPrivat() {
        return privat;
    }

    public void setPrivat(Boolean privat) {
        this.privat = privat;
    }

    public Boolean getFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Boolean getMirror() {
        return mirror;
    }

    public void setMirror(Boolean mirror) {
        this.mirror = mirror;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage_url() {
        return language_url;
    }

    public void setLanguage_url(String language_url) {
        this.language_url = language_url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSsh_url() {
        return ssh_url;
    }

    public void setSsh_url(String ssh_url) {
        this.ssh_url = ssh_url;
    }

    public String getClone_url() {
        return clone_url;
    }

    public void setClone_url(String clone_url) {
        this.clone_url = clone_url;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getStars_count() {
        return stars_count;
    }

    public void setStars_count(Integer stars_count) {
        this.stars_count = stars_count;
    }

    public Integer getForks_count() {
        return forks_count;
    }

    public void setForks_count(Integer forks_count) {
        this.forks_count = forks_count;
    }

    public Integer getWatchers_count() {
        return watchers_count;
    }

    public void setWatchers_count(Integer watchers_count) {
        this.watchers_count = watchers_count;
    }

    public Integer getOpen_issue_count() {
        return open_issue_count;
    }

    public void setOpen_issue_count(Integer open_issue_count) {
        this.open_issue_count = open_issue_count;
    }

    public Integer getOpen_pr_counter() {
        return open_pr_counter;
    }

    public void setOpen_pr_counter(Integer open_pr_counter) {
        this.open_pr_counter = open_pr_counter;
    }

    public Integer getRelease_counter() {
        return release_counter;
    }

    public void setRelease_counter(Integer release_counter) {
        this.release_counter = release_counter;
    }

    public String getDefault_branch() {
        return default_branch;
    }

    public void setDefault_branch(String default_branch) {
        this.default_branch = default_branch;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public Boolean getHas_issues() {
        return has_issues;
    }

    public void setHas_issues(Boolean has_issues) {
        this.has_issues = has_issues;
    }

    public InternalTracker getInternal_tracker() {
        return internal_tracker;
    }

    public void setInternal_tracker(InternalTracker internal_tracker) {
        this.internal_tracker = internal_tracker;
    }

    public Boolean getHas_wiki() {
        return has_wiki;
    }

    public void setHas_wiki(Boolean has_wiki) {
        this.has_wiki = has_wiki;
    }

    public Boolean getHas_pull_requests() {
        return has_pull_requests;
    }

    public void setHas_pull_requests(Boolean has_pull_requests) {
        this.has_pull_requests = has_pull_requests;
    }

    public Boolean getHas_projects() {
        return has_projects;
    }

    public void setHas_projects(Boolean has_projects) {
        this.has_projects = has_projects;
    }

    public Boolean getIgnore_whitespace_conflicts() {
        return ignore_whitespace_conflicts;
    }

    public void setIgnore_whitespace_conflicts(Boolean ignore_whitespace_conflicts) {
        this.ignore_whitespace_conflicts = ignore_whitespace_conflicts;
    }

    public Boolean getAllow_merge_commits() {
        return allow_merge_commits;
    }

    public void setAllow_merge_commits(Boolean allow_merge_commits) {
        this.allow_merge_commits = allow_merge_commits;
    }

    public Boolean getAllow_rebase() {
        return allow_rebase;
    }

    public void setAllow_rebase(Boolean allow_rebase) {
        this.allow_rebase = allow_rebase;
    }

    public Boolean getAllow_rebase_explicit() {
        return allow_rebase_explicit;
    }

    public void setAllow_rebase_explicit(Boolean allow_rebase_explicit) {
        this.allow_rebase_explicit = allow_rebase_explicit;
    }

    public Boolean getAllow_squash_merge() {
        return allow_squash_merge;
    }

    public void setAllow_squash_merge(Boolean allow_squash_merge) {
        this.allow_squash_merge = allow_squash_merge;
    }

    public Boolean getAllow_rebase_update() {
        return allow_rebase_update;
    }

    public void setAllow_rebase_update(Boolean allow_rebase_update) {
        this.allow_rebase_update = allow_rebase_update;
    }

    public Boolean getDefault_delete_branch_after_merge() {
        return default_delete_branch_after_merge;
    }

    public void setDefault_delete_branch_after_merge(Boolean default_delete_branch_after_merge) {
        this.default_delete_branch_after_merge = default_delete_branch_after_merge;
    }

    public String getDefault_merge_style() {
        return default_merge_style;
    }

    public void setDefault_merge_style(String default_merge_style) {
        this.default_merge_style = default_merge_style;
    }

    public Boolean getDefault_allow_maintainer_edit() {
        return default_allow_maintainer_edit;
    }

    public void setDefault_allow_maintainer_edit(Boolean default_allow_maintainer_edit) {
        this.default_allow_maintainer_edit = default_allow_maintainer_edit;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public String getMirror_interval() {
        return mirror_interval;
    }

    public void setMirror_interval(String mirror_interval) {
        this.mirror_interval = mirror_interval;
    }

    public String getMirror_updated() {
        return mirror_updated;
    }

    public void setMirror_updated(String mirror_updated) {
        this.mirror_updated = mirror_updated;
    }

    public String getRepo_transfer() {
        return repo_transfer;
    }

    public void setRepo_transfer(String repo_transfer) {
        this.repo_transfer = repo_transfer;
    }
}

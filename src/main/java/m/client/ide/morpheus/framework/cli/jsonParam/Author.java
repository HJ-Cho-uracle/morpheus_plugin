package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

public class Author extends AbstractJsonElement {

    private static final String key_id = "id";
    private static final String key_login = "login";
    private static final String key_login_name = "login_name";
    private static final String key_full_name = "full_name";
    private static final String key_email = "email";
    private static final String key_avatar_url = "avatar_url";
    private static final String key_language = "language";
    private static final String key_is_admin = "is_admin";
    private static final String key_last_login = "last_login";
    private static final String key_created = "created";
    private static final String key_restricted = "restricted";
    private static final String key_active = "active";
    private static final String key_prohibit_login = "prohibit_login";
    private static final String key_location = "location";
    private static final String key_website = "website";
    private static final String key_description = "description";
    private static final String key_visibility = "visibility";
    private static final String key_followers_count = "followers_count";
    private static final String key_following_count = "following_count";
    private static final String key_starred_repos_count = "starred_repos_count";
    private static final String key_username = "username";

    private Integer         id;
    private String          login;
    private String          login_name;
    private String          full_name;
    private String          email;
    private String          avatar_url;
    private String          language;
    private Boolean         is_admin;
    private String          last_login;
    private String          created;
    private Boolean         restricted;
    private Boolean         active;
    private Boolean         prohibit_login;
    private String          location;
    private String          website;
    private String          description;
    private String          visibility;
    private Integer         followers_count;
    private Integer         following_count;
    private Integer         starred_repos_count;
    private String          username;

    public Author(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Author(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_id);
        id = object instanceof Integer ? (Integer) object : 0;
        object = jsonObject.get(key_login);
        login = object != null ? object.toString() : "";
        object = jsonObject.get(key_login_name);
        login_name = object != null ? object.toString() : "";
        object = jsonObject.get(key_full_name);
        full_name = object != null ? object.toString() : "";
        object = jsonObject.get(key_email);
        email = object != null ? object.toString() : "";
        object = jsonObject.get(key_avatar_url);
        avatar_url = object != null ? object.toString() : "";
        object = jsonObject.get(key_language);
        language = object != null ? object.toString() : "";
        object = jsonObject.get(key_is_admin);
        is_admin = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_last_login);
        last_login = object != null ? object.toString() : "";
        object = jsonObject.get(key_created);
        created = object != null ? object.toString() : "";
        object = jsonObject.get(key_restricted);
        restricted = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_active);
        active = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_prohibit_login);
        prohibit_login = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_location);
        location = object != null ? object.toString() : "";
        object = jsonObject.get(key_website);
        website = object != null ? object.toString() : "";
        object = jsonObject.get(key_description);
        description = object != null ? object.toString() : "";
        object = jsonObject.get(key_visibility);
        visibility = object != null ? object.toString() : "";
        object = jsonObject.get(key_followers_count);
        followers_count = object instanceof Integer ? (Integer) object : 0;
        object = jsonObject.get(key_following_count);
        following_count = object instanceof Integer ? (Integer) object : 0;
        object = jsonObject.get(key_starred_repos_count);
        starred_repos_count = object instanceof Integer ? (Integer) object : 0;
        object = jsonObject.get(key_username);
        username = object != null ? object.toString() : "";
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_id, id);
        jsonObject.put(key_login, login);
        jsonObject.put(key_login_name, login_name);
        jsonObject.put(key_full_name, full_name);
        jsonObject.put(key_email, email);
        jsonObject.put(key_avatar_url, avatar_url);
        jsonObject.put(key_language, language);
        jsonObject.put(key_is_admin, is_admin);
        jsonObject.put(key_last_login, last_login);
        jsonObject.put(key_created, created);
        jsonObject.put(key_restricted, restricted);
        jsonObject.put(key_active, active);
        jsonObject.put(key_prohibit_login, prohibit_login);
        jsonObject.put(key_location, location);
        jsonObject.put(key_website, website);
        jsonObject.put(key_description, description);
        jsonObject.put(key_visibility, visibility);
        jsonObject.put(key_followers_count, followers_count);
        jsonObject.put(key_following_count, following_count);
        jsonObject.put(key_starred_repos_count, starred_repos_count);
        jsonObject.put(key_username, username);

        return jsonObject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getProhibit_login() {
        return prohibit_login;
    }

    public void setProhibit_login(Boolean prohibit_login) {
        this.prohibit_login = prohibit_login;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(Integer followers_count) {
        this.followers_count = followers_count;
    }

    public Integer getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(Integer following_count) {
        this.following_count = following_count;
    }

    public Integer getStarred_repos_count() {
        return starred_repos_count;
    }

    public void setStarred_repos_count(Integer starred_repos_count) {
        this.starred_repos_count = starred_repos_count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

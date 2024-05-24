package m.client.ide.morpheus.framework.cli.jsonParam;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateInfoParam extends AbstractJsonElement {

    private static final String key_id = "id";
    private static final String key_tag_name = "tag_name";
    private static final String key_target_commitish = "target_commitish";
    private static final String key_name = "name";
    private static final String key_body = "body";
    private static final String key_url = "url";
    private static final String key_html_url = "html_url";
    private static final String key_tarball_url = "tarball_url";
    private static final String key_zipball_url = "zipball_url";
    private static final String key_draft = "draft";
    private static final String key_prerelease = "prerelease";
    private static final String key_created_at = "created_at";
    private static final String key_published_at = "published_at";
    private static final String key_author = "author";
    private static final String key_assets = "assets";

    private Integer         id;
    private String          tag_name;
    private String          target_commitish;
    private String          name;
    private String          body;
    private String          url;
    private String          html_url;
    private String          tarball_url;
    private String          zipball_url;
    private Boolean         draft;
    private Boolean         prerelease;
    private String          created_at;
    private String          published_at;
    private Author          author;
    private List<String>    assets;

    public TemplateInfoParam(JSONObject jsonObject) {
        super(jsonObject);
    }

    public TemplateInfoParam(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_id);
        id = object instanceof Integer ? (Integer) object : 0;
        object = jsonObject.get(key_tag_name);
        tag_name = object != null ? object.toString() : "";
        object = jsonObject.get(key_target_commitish);
        target_commitish = object != null ? object.toString() : "";
        object = jsonObject.get(key_name);
        name = object != null ? object.toString() : "";
        object = jsonObject.get(key_body);
        body = object != null ? object.toString() : "";
        object = jsonObject.get(key_url);
        url = object != null ? object.toString() : "";
        object = jsonObject.get(key_html_url);
        html_url = object != null ? object.toString() : "";
        object = jsonObject.get(key_tarball_url);
        tarball_url = object != null ? object.toString() : "";
        object = jsonObject.get(key_zipball_url);
        zipball_url = object != null ? object.toString() : "";
        object = jsonObject.get(key_draft);
        draft = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_prerelease);
        prerelease = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_created_at);
        created_at = object != null ? object.toString() : "";
        object = jsonObject.get(key_published_at);
        published_at = object != null ? object.toString() : "";
        object = jsonObject.get(key_author);
        if (object instanceof JSONObject) {
            author = new Author((JSONObject) object);
        } else {
            author = null;
        }

        object = jsonObject.get(key_assets);
        assets = new ArrayList<String>();
        if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
            Iterator iterator = array.iterator();
            while(iterator.hasNext()) {
                Object element = iterator.next();
                if (element instanceof JSONObject) {
                    String asset = object != null ? object.toString() : "";
                    if (!asset.isEmpty())
                        assets.add(asset);
                }
            }
        }
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_id, id);
        jsonObject.put(key_tag_name, tag_name);
        jsonObject.put(key_target_commitish, target_commitish);
        jsonObject.put(key_name, name);
        jsonObject.put(key_body, body);
        jsonObject.put(key_url, url);
        jsonObject.put(key_html_url, html_url);
        jsonObject.put(key_tarball_url, tarball_url);
        jsonObject.put(key_zipball_url, zipball_url);
        jsonObject.put(key_draft, draft);
        jsonObject.put(key_prerelease, prerelease);
        jsonObject.put(key_created_at, created_at);
        jsonObject.put(key_published_at, published_at);
        jsonObject.put(key_author, author.getJSONObject());

        JSONArray assetArray = new JSONArray();
        for(String asset : assets) {
            assetArray.add(asset);
        }
        jsonObject.put(key_assets, assetArray);

        return jsonObject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTarget_commitish() {
        return target_commitish;
    }

    public void setTarget_commitish(String target_commitish) {
        this.target_commitish = target_commitish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getTarball_url() {
        return tarball_url;
    }

    public void setTarball_url(String tarball_url) {
        this.tarball_url = tarball_url;
    }

    public String getZipball_url() {
        return zipball_url;
    }

    public void setZipball_url(String zipball_url) {
        this.zipball_url = zipball_url;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Boolean getPrerelease() {
        return prerelease;
    }

    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<String> getAssets() {
        return assets;
    }

    public void setAssets(List<String> assets) {
        this.assets = assets;
    }
}


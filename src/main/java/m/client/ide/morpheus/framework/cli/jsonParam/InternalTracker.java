package m.client.ide.morpheus.framework.cli.jsonParam;

import org.jetbrains.annotations.NotNull;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public class InternalTracker extends AbstractJsonElement {

    private static final String key_enable_time_tracker = "enable_time_tracker";
    private static final String key_allow_only_contributors_to_track_time = "allow_only_contributors_to_track_time";
    private static final String key_enable_issue_dependencies = "enable_issue_dependencies";

    private Boolean enable_time_tracker;
    private Boolean allow_only_contributors_to_track_time;
    private Boolean enable_issue_dependencies;

    public InternalTracker(JSONObject jsonObject) {
        super(jsonObject);
    }

    public InternalTracker(String jsonString) throws ParseException {
        super(jsonString);
    }

    @Override
    protected void update(@NotNull JSONObject jsonObject) {
        Object object = jsonObject.get(key_enable_time_tracker);
        enable_time_tracker = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_allow_only_contributors_to_track_time);
        allow_only_contributors_to_track_time = object instanceof Boolean ? (Boolean) object : false;
        object = jsonObject.get(key_enable_issue_dependencies);
        enable_issue_dependencies = object instanceof Boolean ? (Boolean) object : false;
    }

    @Override
    protected Object getJSONObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(key_enable_time_tracker, enable_time_tracker);
        jsonObject.put(key_allow_only_contributors_to_track_time, enable_time_tracker);
        jsonObject.put(key_enable_issue_dependencies, enable_issue_dependencies);

        return jsonObject;
    }

    public Boolean isEnable_time_tracker() {
        return enable_time_tracker;
    }

    public void setEnable_time_tracker(Boolean enable_time_tracker) {
        this.enable_time_tracker = enable_time_tracker;
    }

    public Boolean isAllow_only_contributors_to_track_time() {
        return allow_only_contributors_to_track_time;
    }

    public void setAllow_only_contributors_to_track_time(Boolean allow_only_contributors_to_track_time) {
        this.allow_only_contributors_to_track_time = allow_only_contributors_to_track_time;
    }

    public Boolean isEnable_issue_dependencies() {
        return enable_issue_dependencies;
    }

    public void setEnable_issue_dependencies(Boolean enable_issue_dependencies) {
        this.enable_issue_dependencies = enable_issue_dependencies;
    }

}

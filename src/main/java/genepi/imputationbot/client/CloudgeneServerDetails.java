package genepi.imputationbot.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CloudgeneServerDetails {

    private final JSONObject details;

    public CloudgeneServerDetails(JSONObject details) {
        this.details = details;
    }

    public String getName() {
        return details.getString("name");
    }

    public CloudgeneUser getUser() {
        JSONObject user = details.getJSONObject("user");
        return new CloudgeneUser(user);
    }

    public List<CloudgeneApplication> getApps() {
        List<CloudgeneApplication> processed = new ArrayList<>();
        JSONArray raw = details.getJSONArray("apps");

        for (int i = 0; i < raw.length(); ++i) {
            JSONObject r = raw.getJSONObject(i);
            CloudgeneApplication p = new CloudgeneApplication(r);
            processed.add(p);
        }

        return processed;
    }
}

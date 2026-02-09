package genepi.imputationbot.client;

import org.json.JSONObject;

public class CloudgeneApplication {

    private final JSONObject app;

    public CloudgeneApplication(JSONObject app) {
        this.app = app;
    }

    public String getId() {
        return app.getString("id");
    }

    public String getName() {
        return app.getString("name");
    }

    public String getVersion() {
        return app.getString("version");
    }
}

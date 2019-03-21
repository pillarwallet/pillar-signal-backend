package org.whispersystems.textsecuregcm.microservices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CorePlatform {

    private final Logger logger = LoggerFactory.getLogger(CorePlatform.class);

    public static final String CONNECTION_STATE_ACCEPTED = "STATE_ACCEPTED";
    public static final String CONNECTION_STATE_BLOCKED = "STATE_BLOCKED";
    public static final String CONNECTION_STATE_MUTED = "STATE_MUTED";

    public static final String ERROR_CORE_PLATFORM_FAILED = "ERROR_CORE_PLATFORM_FAILED";
    public static final String ERROR_CORE_CONNECTION_FAILED = "ERROR_CORE_CONNECTION_FAILED";

    private String corePlatformUrl;

    public CorePlatform (String url){
        this.corePlatformUrl = url;
    }

    public Future<String> getConnectionState(String userId, String targetUserId, String sourceIdentityKey, String targetIdentityKey) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(String.format("%s/connection/v2?userId=%s&targetUserId=%s&sourceIdentityKey=%s&targetIdentityKey=%s", corePlatformUrl, userId, targetUserId, sourceIdentityKey, targetIdentityKey));
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("User-Agent", "Signal-Java-Backend");
            if (connection.getResponseCode() != 200){
                connection.disconnect();
                logger.info("CorePlatform failed: " + ERROR_CORE_CONNECTION_FAILED);
                completableFuture.cancel(false);
                return completableFuture;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonResponse = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonResponse.append(line);
            }
            br.close();
            connection.disconnect();
            JSONObject response = new JSONObject(jsonResponse.toString());
            String state = CONNECTION_STATE_BLOCKED;
            if (response.optJSONObject("targetConnection") == null) throw new IOException();
            JSONObject targetConnection = response.optJSONObject("targetConnection");
            if (!targetConnection.isNull("status")){
                switch (targetConnection.getString("status")){
                    case "muted":
                        state = CONNECTION_STATE_MUTED;
                        break;
                    case "accepted":
                        state = CONNECTION_STATE_ACCEPTED;
                        break;
                }
            }
            completableFuture.complete(state);
        } catch (IOException | JSONException e) {
            if (connection != null) connection.disconnect();
            logger.info("CorePlatform failed: " + ERROR_CORE_PLATFORM_FAILED);
            completableFuture.cancel(false);
        }
        return completableFuture;
    }

}

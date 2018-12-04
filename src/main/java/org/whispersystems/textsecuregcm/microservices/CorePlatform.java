package org.whispersystems.textsecuregcm.microservices;

import org.eclipse.jetty.util.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.controllers.MessageController;
import org.whispersystems.textsecuregcm.controllers.NoSuchUserException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CorePlatform {

    private final Logger logger = LoggerFactory.getLogger(CorePlatform.class);

    public static final String CONNECTION_STATE_ACCEPTED = "STATE_ACCEPTED";
    public static final String CONNECTION_STATE_BLOCKED = "STATE_BLOCKED";
    public static final String CONNECTION_STATE_MUTED = "STATE_MUTED";

    public static final String ERROR_CORE_PLATFORM_FAILED = "ERROR_CORE_PLATFORM_FAILED";
    public static final String ERROR_CORE_CONNECTION_FAILED = "ERROR_CORE_CONNECTION_FAILED";

    private String state;
    private String corePlatformUrl;

    public CorePlatform (String url){
        this.corePlatformUrl = url;
    }

    public interface Callback {
        void onSuccess(String status) throws NoSuchUserException;
        void onError(String code);
    }

    public void getConnectionState(String receiverId, String connectionAccessKey, Callback cb) throws NoSuchUserException {
        try {
            URL url = new URL(String.format("%s/connection?userId=%s&accessKey=%s", corePlatformUrl, receiverId, connectionAccessKey));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("User-Agent", "Signal-Java-Backend");
            if (connection.getResponseCode() != 200){
                cb.onError(ERROR_CORE_CONNECTION_FAILED);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonResponse = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonResponse.append(line);
            }
            br.close();
            connection.disconnect();
            JSONArray response = new JSONArray(jsonResponse.toString());
            String state = CONNECTION_STATE_BLOCKED;
            if (response.isNull(0)) throw new IOException();
            JSONObject connectionData = response.optJSONObject(0);
            if (!connectionData.isNull("status")){
                switch (connectionData.getString("status")){
                    case "muted":
                        state = CONNECTION_STATE_MUTED;
                        break;
                    case "accepted":
                        state = CONNECTION_STATE_ACCEPTED;
                        break;
                }
            }
            cb.onSuccess(state);
        } catch (IOException | JSONException e) {
            cb.onError(ERROR_CORE_PLATFORM_FAILED);
            e.printStackTrace();
        }
    }

}

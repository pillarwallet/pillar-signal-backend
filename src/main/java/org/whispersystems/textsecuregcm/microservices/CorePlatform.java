package org.whispersystems.textsecuregcm.microservices;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.MicroServicesConfiguration;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.CertificateException;
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
    private SSLConnectionSocketFactory sslConnectionSocketFactory;

    public CorePlatform (MicroServicesConfiguration config){
        this.corePlatformUrl = config.getCorePlatformUrlInternal();
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(config.getCorePlatformCertPath()), "".toCharArray());
            SSLContext sslContext = SSLContexts
                .custom()
                .loadKeyMaterial(keyStore, "".toCharArray())
                .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                .build();
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLS", "SSL"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
            );
        } catch (IOException
                | KeyStoreException
                | NoSuchAlgorithmException
                | CertificateException
                | UnrecoverableKeyException
                | KeyManagementException e) {
            logger.info("CorePlatform create failed!");
            e.printStackTrace();
        }
    }

    public Future<String> getConnectionState(String userId, String targetUserId, String sourceIdentityKey, String targetIdentityKey) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();
            HttpGet httpGet = new HttpGet(String.format("%s/connection/v2?userId=%s&targetUserId=%s&sourceIdentityKey=%s&targetIdentityKey=%s", corePlatformUrl, userId, targetUserId, sourceIdentityKey, targetIdentityKey));
            httpGet.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpGet.setHeader("User-Agent", "Signal-Java-Backend");
            HttpResponse httpResponse = httpClient.execute(httpGet);;
            if (httpResponse.getStatusLine().getStatusCode() != 200){
                httpClient.close();
                logger.info("CorePlatform failed: " + ERROR_CORE_CONNECTION_FAILED);
                completableFuture.cancel(false);
                return completableFuture;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer jsonResponse = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonResponse.append(line);
            }
            br.close();
            httpClient.close();
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
            try {
                if (httpClient != null) httpClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            logger.info("CorePlatform failed: " + ERROR_CORE_PLATFORM_FAILED);
            completableFuture.cancel(false);
        }
        return completableFuture;
    }

}

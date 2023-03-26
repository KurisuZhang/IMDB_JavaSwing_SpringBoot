import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class ImdbThread implements Runnable{
    PoolingHttpClientConnectionManager phcm;
    String tt;
    Movie movie;

    public Movie getMovie() {
        return movie;
    }

    public ImdbThread(PoolingHttpClientConnectionManager phcm, String tt) {
        this.phcm = phcm;
        this.tt = tt;
    }

    @Override
    public void run() {
        try {
            MultiThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MultiThread() throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(phcm).build();
        String url = "https://imdb-api.com/en/API/Title/k_plkqxabh/" + tt;
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            httpEntity = response.getEntity();
        } else {
            // raise error
            System.exit(0);
        }
        String movieJson = EntityUtils.toString(httpEntity, "utf8");

        JSONObject jsonObjects = new JSONObject(movieJson);
        String image = jsonObjects.getString("image");
        image = image.replaceAll("imdb-api.com/images/original","m.media-amazon.com/images/M");
        image = image.replaceAll("._V1_Ratio0.6751_AL_.jpg","._V1_UY296_CR1,0,200,296_AL_.jpg");
        movie = new Movie(jsonObjects.getString("title"), jsonObjects.getString("year"),
                image,jsonObjects.getString("directors"),
                jsonObjects.getString("stars"), jsonObjects.getString("genres"),
                String.valueOf(jsonObjects.get("imDbRating")));
    }
}

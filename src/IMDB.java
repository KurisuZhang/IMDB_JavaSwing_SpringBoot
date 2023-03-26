import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.*;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class IMDB {
    private String SearchName;
    private JPanel contentPane;
    private ArrayList<Movie> movies;
    private PoolingHttpClientConnectionManager phcm;

    public IMDB(String SearchName, JPanel contentPane) throws IOException {
        this.SearchName = SearchName;
        this.contentPane = contentPane;
        this.phcm = new PoolingHttpClientConnectionManager();
        this.movies = new ArrayList<>();
    }


    public ArrayList<Movie> doTheSearch() throws IOException {
        String MoviesJsonInfo = getMoviesInfo(SearchName);
        // determine whether input regular letters
        if (!MoviesJsonInfo.equals("error")){
            ArrayList<String> movieIdArray = getIdArray(MoviesJsonInfo,contentPane);
            // determine whether you can find the movie you input
            if (!(movieIdArray ==null)){
                Set<Thread> threadSet = new HashSet<Thread>();
                ArrayList<ImdbThread> ImdbThreadSet = new ArrayList<>();
                // multi thread for each movie
                for (String tt : movieIdArray) {
                    ImdbThread imdbThread = new ImdbThread(phcm, tt);
                    Thread thread = new Thread(imdbThread);
                    threadSet.add(thread);
                    ImdbThreadSet.add(imdbThread);
                    thread.start();
                }
                // wait for all thread end
                for (Thread t : threadSet) {
                    while (t.isAlive()) {
                        continue;
                    }
                }
                // add all movie to the ArrayList<Movie> movies;
                for (ImdbThread imdbThread : ImdbThreadSet){
                    movies.add(imdbThread.getMovie());
                }
                // close http client
                phcm.close();

            }else {
                JOptionPane.showMessageDialog(contentPane, "can't find the movie you input", "warning", JOptionPane.WARNING_MESSAGE);
                phcm.close();
            }
        }else {
            JOptionPane.showMessageDialog(contentPane, "Check network connection or try another movie again", "warning", JOptionPane.WARNING_MESSAGE);
            phcm.close();
        }
        return movies;
    }



    private static ArrayList<String> getIdArray(String MoviesJsonInfo,JPanel contentPane) {
        ArrayList<String> IdArray = new ArrayList<String>();
        JSONObject jsonObjects = new JSONObject(MoviesJsonInfo);

        if (Objects.equals(jsonObjects.get("results").toString(), "null")){
            IdArray = null;
        }else {
            JSONArray jsonArray = jsonObjects.getJSONArray("results");
            // at most display 5 movies
            int arrayLength = jsonArray.length();
            if (arrayLength>=3){
                arrayLength = 3;
            }
            int finalArrayLength = arrayLength;
//            Thread findThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    JOptionPane.showMessageDialog(contentPane, String.valueOf(finalArrayLength)+" movie have found", "Searching", JOptionPane.PLAIN_MESSAGE);
//                }
//            });
//            findThread.start();
            for (int i = 0; i < arrayLength; i++) {
                JSONObject movieItem = jsonArray.getJSONObject(i);
                IdArray.add(movieItem.getString("id"));
            }
        }
        return IdArray;
    }

    private static String getMoviesInfo(String SearchName) throws IOException {
        // set time out
        String MoviesJsonInfo = "";
        int timeout = 7;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();


        // concact url
        String url = "https://imdb-api.com/en/API/SearchMovie/k_plkqxabh/" + SearchName;
        url = url.replaceAll(" ", "%20");
        HttpGet httpGet = null;

        try {
            httpGet = new HttpGet(url);
        } catch (IllegalArgumentException ex){
            MoviesJsonInfo = "error";
        }

        if (!MoviesJsonInfo.equals("error")){
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                HttpEntity httpEntity = null;
                if (response.getStatusLine().getStatusCode() == 200) {
                    httpEntity = response.getEntity();
                    MoviesJsonInfo = EntityUtils.toString(httpEntity, "utf8");
                } else {
                    MoviesJsonInfo = "error";
                }

            }catch (Exception exception){
                MoviesJsonInfo = "error";
            }
        }
        return MoviesJsonInfo;
    }
}

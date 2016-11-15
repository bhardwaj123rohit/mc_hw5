package com.example.rohit.asyntask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    ProgressBar showPrgress;
    TextView down;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPrgress = (ProgressBar) findViewById(R.id.progressBar2);
         down = (TextView) findViewById(R.id.textView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            down.setText(savedInstanceState.getString("parsed"));

        } else {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    new DownloadFilesTask().execute(new URL("https://www.iiitd.ac.in/about"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                down.setText("No network connection available.");
            }        }

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("parsed", down.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }


    private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {

        protected String doInBackground(URL... url) {

                try {
                    return downloadUrl(url[0]);

                } catch (IOException e) {
                    return "Unable to retrieve web page. URL may be invalid.";
                }

        }

        private String downloadUrl(URL url) throws IOException {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            int response = conn.getResponseCode();
            InputStream is = conn.getInputStream();

            // Reading the content
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1000000];
            reader.read(buffer);

            publishProgress(100);
            if (is != null) {
                is.close();
            }

            return new String(buffer);
        }

        protected void onProgressUpdate(Integer... progress) {
           MainActivity.this.setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Document doc = Jsoup.parse(result);
            /*down.setText(doc.select("div#content-content").contains("")*/
            Elements ele = (doc.select("div#content-content"));
            for(Element e : ele){
                //Log.d("output",e.getElementsByTag("p").text());
                down.setText(e.getElementsByTag("p").text());

            }
            //down.setText(doc.select("div#content-content").select("p").first().text());

            Log.d("Receiving Data ", "The response is: " + result);



        }
    }

    private void setProgressPercent(Integer progres) {
        showPrgress.setProgress(progres);
    }

}

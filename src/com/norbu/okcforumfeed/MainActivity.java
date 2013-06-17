package com.norbu.okcforumfeed;

import java.io.IOException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   public void startMain(View view) {
      // hit http://www.okcupid.com/forum
      // scrape "posted WHEN" values
      // open each section: http://www.okcupid.com/forum?sid=11
      // scrape last-two comment times

      String url = "http://www.okcupid.com/forum";
      ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) {
         new DownloadWebpageTask().execute(url);
      } else {
         textView.setText("No network connection available.");
      }
   }

   // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
   // URL string and uses it to create an HttpUrlConnection. Once the connection
   // has been established, the AsyncTask downloads the contents of the webpage as
   // an InputStream. Finally, the InputStream is converted into a string, which is
   // displayed in the UI by the AsyncTask's onPostExecute method.
   private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
      @Override
      protected String doInBackground(String... urls) {

         // params comes from the execute() call: params[0] is the url.
         try {
            return downloadUrl(urls[0]);
         } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
         }
      }
      // onPostExecute displays the results of the AsyncTask.
      @Override
      protected void onPostExecute(String result) {
         textView.setText(result);
      }
   }
}

package com.norbu.okcforumfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

   private static final String DEBUG_TAG = "DownloadWebpageTask";

   @Override
   protected String doInBackground(String... urls) {

      try {
         return downloadUrl(urls[0]);
      } catch (IOException e) {
         return "Unable to retrieve web page. URL may be invalid.";
      }
   }
   
   @Override
   protected void onPostExecute(String result) {
      //textView.setText(result);
   }
   
   private String downloadUrl(String myurl) throws IOException {

      InputStream is = null;
      try {
         URL url = new URL(myurl);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setReadTimeout(10000 /* milliseconds */);
         conn.setConnectTimeout(15000 /* milliseconds */);
         conn.setRequestMethod("GET");
         conn.setDoInput(true);
         // Starts the query
         conn.connect();
         int response = conn.getResponseCode();
         Log.d(DEBUG_TAG, "The response is: " + response);
         is = conn.getInputStream();

         // Convert the InputStream into a string
         String contentAsString = readIt(is);
         return contentAsString;

         // Makes sure that the InputStream is closed after the app is
         // finished using it.
      } finally {
         if (is != null) is.close();
      }
   }
   
   private String readIt(InputStream is) throws IOException, UnsupportedEncodingException {
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      reader.readLine();

      String nextline;
      while ((nextline = reader.readLine()) != null) {
         System.out.println(nextline);
      }
      
      return "";
  }
}

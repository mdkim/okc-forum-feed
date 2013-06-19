package com.norbu.okcforumfeed;

import java.io.IOException;

import android.os.AsyncTask;
import android.widget.TextView;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

   private MainActivity mainActivity;

   public DownloadWebpageTask(MainActivity mainActivity) {
      this.mainActivity = mainActivity;
   }

   @Override
   protected String doInBackground(String... urls) {
      Debug.println("doInBackground");
      try {
         return downloadUrl();
      } catch (IOException e) {
         return "Unable to retrieve web page. URL may be invalid.";
      }
   }
   
   @Override
   protected void onPostExecute(String result) {
      TextView textView = this.mainActivity.getTextView();
      textView.setText(result);
   }
   
   private String downloadUrl() throws IOException {
      
      Debug.println("downloadUrl");
      
      new OkcForumFeed().go();
      return "THIS IS A STRING";
   }
}

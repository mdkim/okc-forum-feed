package com.norbu.okcforumfeed;

import java.util.List;

import android.os.AsyncTask;

public class OkcDownloadTask extends AsyncTask<String, Void, List<OkcThread>> {

   private OkcThreadArrayAdapter okcThreadArrayAdapter;
   private boolean isRunning = false;

   public OkcDownloadTask(OkcThreadArrayAdapter okcThreadArrayAdapter) {
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;
   }

   @Override
   protected List<OkcThread> doInBackground(String... urls) {
      Debug.println("doInBackground");
      
      this.isRunning  = true;
      OkcForumFeed okcff = new OkcForumFeed();
      List<OkcThread> okcThreadList = okcff.downloadOkcThreadList();
      
      this.isRunning = false;
      return okcThreadList;
   }
   
   @Override
   protected void onPostExecute(List<OkcThread> result) {
      OkcThreadArrayAdapter okcThreadArrayAdapter = this.okcThreadArrayAdapter;
      for (OkcThread okcThread : result) {
         okcThreadArrayAdapter.add(okcThread);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}

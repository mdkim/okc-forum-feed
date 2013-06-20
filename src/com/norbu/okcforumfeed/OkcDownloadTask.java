package com.norbu.okcforumfeed;

import java.util.List;

import android.os.AsyncTask;

public class OkcDownloadTask extends AsyncTask<String, Void, List<OkcThread>> {

   private OkcThreadArrayAdapter okcThreadArrayAdapter;

   public OkcDownloadTask(OkcThreadArrayAdapter okcThreadArrayAdapter) {
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;
   }

   @Override
   protected List<OkcThread> doInBackground(String... urls) {
      Debug.println("doInBackground");
      
      OkcForumFeed okcff = new OkcForumFeed();
      List<OkcThread> okcThreadList = okcff.downloadOkcThreadList();
      return okcThreadList;
   }
   
   @Override
   protected void onPostExecute(List<OkcThread> result) {
      OkcThreadArrayAdapter okcThreadArrayAdapter = this.okcThreadArrayAdapter;
      for (OkcThread okcThread : result) {
         okcThreadArrayAdapter.add(okcThread);
      }
   }
}

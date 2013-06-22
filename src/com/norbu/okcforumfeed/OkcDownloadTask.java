package com.norbu.okcforumfeed;

import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class OkcDownloadTask extends AsyncTask<String, Void, List<OkcThread>> {

   private MainActivity mainActivity;
   //
   private boolean isRunning = false;
   private OkcException lastOkcException;
   
   public OkcDownloadTask(MainActivity mainActivity) {
      this.mainActivity = mainActivity;
   }

   @Override
   protected void onPreExecute() {
      ProgressDialog progressDialog = this.mainActivity.getProgressDialog();
      progressDialog.setProgress(1);
      progressDialog.show();
   }

   @Override
   protected List<OkcThread> doInBackground(String... urls) {
      Debug.println("doInBackground");

      this.isRunning  = true;
      List<OkcThread> okcThreadList = null;
      try {
         
         OkcThreadArrayAdapter okcThreadArrayAdapter = this.mainActivity.getOkcThreadArrayAdapter();
         Date lastUpdated = okcThreadArrayAdapter.getLastUpdated();
         okcThreadArrayAdapter.setClearAllIsUpdated();
         okcThreadList = OkcForumFeed.downloadOkcThreadList(lastUpdated, this.mainActivity.getProgressDialog());
      } catch (OkcException e) {
         e.printStackTrace();
         this.lastOkcException = e;
      }
      this.isRunning = false;
      return okcThreadList;
   }
   
   @Override
   protected void onPostExecute(List<OkcThread> result) {
      
      Date now = new Date();
      String now_s = OkcThread.sdf_hhmm_ddMMMyyyy.format(now);
      if (result == null) {
         // OkcException caught in doInBackground()
         MainActivity.showOkcExceptionDialog(this.mainActivity.getAlertDialog(), this.lastOkcException);
         this.onPostExecute_Finish("Could not refresh:\n" + now_s);
         return;
      }

      OkcThreadArrayAdapter okcThreadArrayAdapter = this.mainActivity.getOkcThreadArrayAdapter();
      if (okcThreadArrayAdapter.getCount() > 0) {
         // insert at beginning with setIsUpdated(true) if okcThreadAdapter has items
         int i=0;
         for (OkcThread okcThread : result) {
            okcThread.setIsUpdated(true);
            okcThreadArrayAdapter.insert(okcThread, i++);
         }
      } else {
         // normal population of okcThreadArrayAdapter
         for (OkcThread okcThread : result) {
            okcThreadArrayAdapter.add(okcThread);
         }
      }
      okcThreadArrayAdapter.setLastUpdated(now);
      this.onPostExecute_Finish("Last updated:\n" + now_s);
   }
   private void onPostExecute_Finish(String text) {
      TextView textView = this.mainActivity.getTextView();
      textView.setText(text);
      this.mainActivity.resetProgressDialog();
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}

package com.norbu.okcforumfeed;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class OkcDownloadTask extends AsyncTask<String, Void, List<OkcThread>> {

   private OkcThreadArrayAdapter okcThreadArrayAdapter;
   private boolean isRunning = false;
   private TextView textView;
   private ProgressDialog progressDialog;
   private AlertDialog alertDialog;
   private OkcException lastOkcException;

   public OkcDownloadTask(MainActivity mainActivity) {
      this.okcThreadArrayAdapter = mainActivity.getOkcThreadArrayAdapter();
      this.textView = mainActivity.getTextView();
      this.progressDialog = mainActivity.getProgressDialog();
      this.alertDialog = mainActivity.getAlertDialog();
   }

   @Override
   protected void onPreExecute() {
      this.progressDialog.setProgress(1);
      this.progressDialog.show();
   }

   @Override
   protected List<OkcThread> doInBackground(String... urls) {
      Debug.println("doInBackground");

      this.isRunning  = true;
      List<OkcThread> okcThreadList = null;
      try {
         
         Date lastUpdated = this.okcThreadArrayAdapter.getLastUpdated();
         this.okcThreadArrayAdapter.setClearAllIsUpdated();
         okcThreadList = OkcForumFeed.downloadOkcThreadList(lastUpdated, this.progressDialog);
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
         MainActivity.showOkcExceptionDialog(alertDialog, this.lastOkcException);
         this.textView.setText("Could not refresh:\n" + now_s);
         this.progressDialog.dismiss();
         return;
      }

      OkcThreadArrayAdapter okcThreadArrayAdapter = this.okcThreadArrayAdapter;

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
      this.okcThreadArrayAdapter.setLastUpdated(now);
      this.textView.setText("Last updated:\n" + now_s);
      this.progressDialog.dismiss();
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}

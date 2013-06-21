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
      OkcForumFeed okcff = new OkcForumFeed();
      List<OkcThread> okcThreadList = null;
      try {
         okcThreadList = okcff.downloadOkcThreadList(this.progressDialog);
      } catch (OkcException e) {
         e.printStackTrace();
         this.lastOkcException = e;
      }
      this.isRunning = false;
      return okcThreadList;
   }
   
   @Override
   protected void onPostExecute(List<OkcThread> result) {
      
      String date_s = OkcThread.sdf_hhmm_ddMMMyyyy.format(new Date());
      if (result == null) {
         // OkcException caught in doInBackground()
         showOkcExceptionDialog(alertDialog, this.lastOkcException);
         this.textView.setText("Could not refresh:\n" + date_s);
      } else {

         OkcThreadArrayAdapter okcThreadArrayAdapter = this.okcThreadArrayAdapter;
         for (OkcThread okcThread : result) {
            okcThreadArrayAdapter.add(okcThread);
         }
         this.textView.setText("Last updated:\n" + date_s);
      }
      this.progressDialog.dismiss();
   }
   private static void showOkcExceptionDialog(AlertDialog alertDialog, OkcException e) {
      String title;
      if (e.isCauseIOException()) {
         title = "Network connectivity issue";
      } else {
         title = "Unexpected error";
      }
      alertDialog.setTitle(title);
      alertDialog.setMessage(e.getMessage());
      alertDialog.show();
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}

package com.norbu.okcforumfeed;

import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class OkcDownloadTask extends AsyncTask<String, Void, List<OkcThread>> {

   private MainActivity mainActivity;
   private OkcThreadArrayAdapter okcThreadArrayAdapter;
   private boolean isRunning = false;
   private TextView textView;
   private ProgressDialog progressDialog;

   public OkcDownloadTask(MainActivity mainActivity, OkcThreadArrayAdapter okcThreadArrayAdapter, TextView textView) {
      this.mainActivity = mainActivity;
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;
      this.textView = textView;
   }

   @Override
   protected void onPreExecute() {
      this.progressDialog = new ProgressDialog(this.mainActivity); // THEME
      this.progressDialog.setTitle("Downloading from web");
      this.progressDialog.setMessage("Examining forum threads ...");
      this.progressDialog.setCancelable(true);
      this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      this.progressDialog.setIndeterminate(false);
      this.progressDialog.setMax(20);
      progressDialog.show();
   }

   @Override
   protected List<OkcThread> doInBackground(String... urls) {
      Debug.println("doInBackground");

      this.isRunning  = true;
      OkcForumFeed okcff = new OkcForumFeed();
      List<OkcThread> okcThreadList = okcff.downloadOkcThreadList(this.progressDialog);

      this.isRunning = false;
      return okcThreadList;
   }

   @Override
   protected void onPostExecute(List<OkcThread> result) {
      OkcThreadArrayAdapter okcThreadArrayAdapter = this.okcThreadArrayAdapter;
      for (OkcThread okcThread : result) {
         okcThreadArrayAdapter.add(okcThread);
      }
      String date_s = OkcThread.sdf_hhmm_ddMMMyyyy.format(new Date());
      this.textView.setText("Last updated: " + date_s);

      this.progressDialog.dismiss();
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}

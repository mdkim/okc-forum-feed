package com.norbu.okcforumfeed;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

   private OkcDownloadTask okcDownloadTask;
   // fields used by OkcDownloadTask->OkcForumFeed
   private TextView textView;
   private OkcThreadArrayAdapter okcThreadArrayAdapter;
   private ProgressDialog progressDialog;
   private AlertDialog alertDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Debug.init(this);
      Debug.println("MainActivity.onCreate");
      
      // for OkcDownloadTask.onPostExecute()
      TextView textView = (TextView) findViewById(R.id.textView1);
      this.textView = textView;

      // for OkcDownloadTask.onPostExecute()
      ListView listView = (ListView) findViewById(R.id.listView1);
      OkcThreadArrayAdapter okcThreadArrayAdapter = new OkcThreadArrayAdapter(this);
      listView.setAdapter(okcThreadArrayAdapter);
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;

      listView.setOnItemClickListener(new ListView.OnItemClickListener(){
         @Override
         public void onItemClick(AdapterView<?> a, View v, int i, long l) {
            // default action onClick is to open in WebView
            openOkcThreadWebView(i);
         }
      });
      this.registerForContextMenu(listView);
      
      // for OkcDownloadTask.doInBackground()
      AlertDialog alertDialog = new AlertDialog.Builder(this).create(); // THEME
      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
         }
      });
      this.alertDialog = alertDialog;
      
      // for OkcForumFeed.downloadOkcThreadList()
      ProgressDialog progressDialog = new ProgressDialog(this); // THEME
      progressDialog.setTitle("Downloading from web");
      progressDialog.setMessage("Examining forum threads ...");
      progressDialog.setCancelable(true);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      progressDialog.setIndeterminate(false);
      progressDialog.setMax(20);
      this.progressDialog = progressDialog;
      
      this.okcDownloadTask = new OkcDownloadTask(this);
   }

   @Override
   public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
       super.onCreateContextMenu(menu, v, menuInfo);
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.context_menu, menu);
   }
   @Override
   public boolean onContextItemSelected(MenuItem item) {
      
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      int position = info.position;
      
      switch (item.getItemId()) {
      case R.id.action_webview:
         openOkcThreadWebView(position);
         return true;
      case R.id.action_browser:
         openOkcThreadInBrowser(position);
         return true;
      default:
         // cancel
         return super.onContextItemSelected(item);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // TO DO: Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   private void openOkcThreadInBrowser(int i) {
      String url = getOkcThreadUrl(i);
      
      Uri uri = Uri.parse(url);
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(browserIntent);
   }
   private void openOkcThreadWebView(int i) {
      String url = getOkcThreadUrl(i);
      
      Intent intent = new Intent(this, WebViewActivity.class);
      intent.putExtra(WebViewActivity.INTENT_URL, url);
      startActivity(intent);
   }

   private String getOkcThreadUrl(int i) {
      OkcThread okcThread = this.okcThreadArrayAdapter.getItem(i);
      String url = "http://okcupid.com/forum?disable_mobile=1&tid=" + okcThread.getTid() + "&low=" + okcThread.getTidLastPage();
      Debug.println("ACTION:url=" + url);
      return url;
   }
   
   // Refresh button clicked
   public void startMain(View view) {

      Debug.println("startMain");

      ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      if (networkInfo == null || !networkInfo.isConnected()) {
         this.textView.setText("No network connection");
         return;
      }
      if (!this.okcDownloadTask.isRunning()) {
         this.okcDownloadTask.execute();
         this.textView.setText("Working ...");
         
         // create new Task, since Task executed only once
         this.okcDownloadTask = new OkcDownloadTask(this);
      } else {
         this.textView.setText("Wait please ...");
      }
   }

   @Override
   protected void onStart() {
      super.onStart();
      Debug.println("MainActivity.onStart");
   }
   @Override
   protected void onResume() {
      super.onResume();
      Debug.println("MainActivity.onResume");
   }
   @Override
   protected void onPause() {
      super.onPause();
      
      Debug.println("MainActivity.onPause");
   }
   @Override
   protected void onStop() {
      super.onStop();
      
      Debug.println("MainActivity.onStop");
   }

   public TextView getTextView() {
      return this.textView;
   }
   public OkcThreadArrayAdapter getOkcThreadArrayAdapter() {
      return this.okcThreadArrayAdapter;
   }
   public ProgressDialog getProgressDialog() {
      return this.progressDialog;
   }
   public AlertDialog getAlertDialog() {
      return this.alertDialog;
   }
}

package com.norbu.okcforumfeed;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
import android.graphics.Typeface;
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

   public static Typeface tf_roboto_bc, tf_roboto;
   private static final String CACHE_THREADARRAYADAPTER_FILE = "okc.taa.cache";
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

      Debug.println("MainActivity.onCreate");
      
      tf_roboto_bc = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
      tf_roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
      
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
      this.progressDialog = createProgressDialog(this);
      
      try {
         Debug.init(this, true, Debug.INFO);
      } catch (OkcException e) {
         e.printStackTrace();
         showOkcExceptionDialog(this.alertDialog, e);
      }
   }

   public static ProgressDialog createProgressDialog(Context context) {
      // for OkcForumFeed.downloadOkcThreadList()
      ProgressDialog progressDialog = new ProgressDialog(context); // THEME
      progressDialog.setTitle("Downloading from web");
      progressDialog.setMessage("Examining forum threads ...");
      progressDialog.setCancelable(true);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      progressDialog.setIndeterminate(false);
      progressDialog.setMax(20);
      return progressDialog;
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
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle item selection
       switch (item.getItemId()) {
       case R.id.action_clear:
           this.okcThreadArrayAdapter.clear();
           this.okcThreadArrayAdapter.setLastUpdated(null);
           this.textView.setText(getString(R.string.textView1));
           this.clearCache();
           return true;
       case R.id.action_settings:
           // not implemented
           return true;
       default:
           return super.onOptionsItemSelected(item);
       }
   }
   
   private void openOkcThreadInBrowser(int i) {
      String url = getOkcThreadUrl(i);
      
      Uri uri = Uri.parse(url);
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(browserIntent);
      
      this.okcThreadArrayAdapter.setVisited(i);
   }
   private void openOkcThreadWebView(int i) {
      String url = getOkcThreadUrl(i);
      
      Intent intent = new Intent(this, WebViewActivity.class);
      intent.putExtra(WebViewActivity.INTENT_URL, url);
      startActivity(intent);
      
      this.okcThreadArrayAdapter.setVisited(i);
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
      if (this.okcDownloadTask == null || !this.okcDownloadTask.isRunning()) {
         
         // create new Task, since Task executed only once
         this.okcDownloadTask = new OkcDownloadTask(this);
         
         this.okcDownloadTask.execute();
         this.textView.setText("Working ...");
      } else {
         this.textView.setText("Wait please ...");
      }
   }

   @Override
   protected void onStart() {
      super.onStart();
      Debug.println("MainActivity.onStart");
      
      // read okcThreadArrayAdapter from cache
      File file = new File(getCacheDir(), CACHE_THREADARRAYADAPTER_FILE);
      if (!file.exists()) return;
      FileReader reader = null;
      try {
         reader = new FileReader(file);
         this.okcThreadArrayAdapter.deserializeFromReader(reader);
         if (reader != null) reader.close();
      } catch (IOException e) {
         showOkcExceptionDialog(this.alertDialog, new OkcException(e));
      } catch (OkcException e) {
         showOkcExceptionDialog(this.alertDialog, e);
      }
      
      Date lastUpdated = this.okcThreadArrayAdapter.getLastUpdated();
      String lastUpdated_s;
      if (lastUpdated == null) {
         this.textView.setText(getString(R.string.textView1));
      } else {
         lastUpdated_s = OkcThread.sdf_hhmm_ddMMMyyyy.format(lastUpdated);
         this.textView.setText("(From cache) Last updated:\n" + lastUpdated_s);
      }
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
      
      // store okcThreadArrayAdapter to cache
      File file = new File(getCacheDir(), CACHE_THREADARRAYADAPTER_FILE);
      FileWriter fw = null;
      try {
         fw = new FileWriter(file);
         this.okcThreadArrayAdapter.serializeToWriter(fw);
         if (fw != null) fw.close();
      } catch (IOException e) {
         showOkcExceptionDialog(this.alertDialog, new OkcException(e));
      } catch (OkcException e) {
         showOkcExceptionDialog(this.alertDialog, e);
      }
   }
   private void clearCache() {
      File file = new File(getCacheDir(), CACHE_THREADARRAYADAPTER_FILE);
      FileWriter fw;
      try {
         fw = new FileWriter(file);
         fw.close();
      } catch (IOException e) {
         showOkcExceptionDialog(this.alertDialog, new OkcException(e));
      }
   }
   
   // cannot call this from doInBackground() thread
   public static void showOkcExceptionDialog(AlertDialog alertDialog, OkcException e) {
      e.printStackTrace();
      
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
   public void resetProgressDialog() {
      this.progressDialog.dismiss();
      this.progressDialog = MainActivity.createProgressDialog(this);
   }
}

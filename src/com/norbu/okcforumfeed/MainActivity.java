package com.norbu.okcforumfeed;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

   private TextView textView;
   private OkcThreadArrayAdapter okcThreadArrayAdapter;
   private OkcDownloadTask okcDownloadTask;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Debug.init(this);
      
      TextView textView = (TextView) findViewById(R.id.textView1);
      this.textView = textView;

      ListView listView = (ListView) findViewById(R.id.listView1);
      OkcThreadArrayAdapter okcThreadArrayAdapter = new OkcThreadArrayAdapter(this);
      listView.setAdapter(okcThreadArrayAdapter);
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;

      listView.setOnItemClickListener(new ListView.OnItemClickListener(){
         @Override
         public void onItemClick(AdapterView<?> a, View v, int i, long l) {
            OkcThread okcThread = (OkcThread) a.getItemAtPosition(i);
            String url = "http://okcupid.com/forum?disable_mobile=1&tid=" + okcThread.getTid() + "&low=" + okcThread.getTidLastPage();
            Debug.println("ACTION:url=" + url);
            Uri uri = Uri.parse(url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browserIntent);
         }
      });

      this.okcDownloadTask = new OkcDownloadTask(this, this.okcThreadArrayAdapter, this.textView);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

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
         
         // Task executed only once
         this.okcDownloadTask = new OkcDownloadTask(this, this.okcThreadArrayAdapter, this.textView);
      } else {
         this.textView.setText("Wait please ...");
      }
   }

   public TextView getTextView() {
      return this.textView;
   }
}

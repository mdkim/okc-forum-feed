package com.norbu.okcforumfeed;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

   private TextView textView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      TextView textView = (TextView) findViewById(R.id.textView1);
      this.textView = textView;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   public void startMain(View view) {
      // hit http://www.okcupid.com/forum
      // scrape "posted WHEN" values
      // open each section: http://www.okcupid.com/forum?sid=11
      // scrape last-two comment times

      String url = "http://www.okcupid.com/forum";
      ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) {
         new DownloadWebpageTask().execute(url);
      } else {
         this.textView.setText("No network connection available.");
      }
   }
}

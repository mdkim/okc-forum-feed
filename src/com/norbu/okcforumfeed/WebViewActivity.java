package com.norbu.okcforumfeed;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class WebViewActivity extends Activity {

   public static final String INTENT_URL = "com.norbu.okcforumfeed.INTENT_URL";
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      Debug.println("WebViewActivity.onCreate");
      
      setContentView(R.layout.activity_web_view);
      // Show the Up button in the action bar.
      setupActionBar();
      
      Intent intent = getIntent();
      String url = intent.getStringExtra(INTENT_URL);
      WebView myWebView = (WebView) findViewById(R.id.webView1);
      myWebView.setWebViewClient(new WebViewClient()); // do not open browser
      myWebView.loadUrl(url);
   }

   /**
    * Set up the {@link android.app.ActionBar}, if the API is available.
    */
   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
   private void setupActionBar() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
         getActionBar().setDisplayHomeAsUpEnabled(true);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.web_view, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case android.R.id.home:
         // This ID represents the Home or Up button. In the case of this
         // activity, the Up button is shown. Use NavUtils to allow users
         // to navigate up one level in the application structure. For
         // more details, see the Navigation pattern on Android Design:
         //
         // http://developer.android.com/design/patterns/navigation.html#up-vs-back
         //
         NavUtils.navigateUpFromSameTask(this);
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

}

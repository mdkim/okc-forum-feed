package com.norbu.okcforumfeed;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

   private TextView textView;
   private OkcThreadArrayAdapter okcThreadArrayAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      TextView textView = (TextView) findViewById(R.id.textView1);
      this.textView = textView;
      
      ListView listView = (ListView) findViewById(R.id.listView1);
      OkcThreadArrayAdapter okcThreadArrayAdapter = new OkcThreadArrayAdapter(this);
      listView.setAdapter(okcThreadArrayAdapter);
      this.okcThreadArrayAdapter = okcThreadArrayAdapter;
      
      // temp
      Debug.init(this);
   }

   
   
/*
   @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
   
      //get selected items
      String selectedValue = (String) getListAdapter().getItem(position);
      Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
   
   }*/

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
      if (networkInfo != null && networkInfo.isConnected()) {
         new OkcDownloadTask(this.okcThreadArrayAdapter).execute();
      } else {
         this.textView.setText("No network connection available.");
      }
   }

   public TextView getTextView() {
      return this.textView;
   }
}

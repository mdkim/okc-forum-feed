package com.norbu.okcforumfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OkcThreadArrayAdapter extends ArrayAdapter<OkcThread> {

   private static final String JSON_ARRAY = "JSON_ARRAY", LAST_UPDATED = "LAST_UPDATED";

   private Date lastUpdated;
   
   private final Context context;
   
   public OkcThreadArrayAdapter(Context context) {
      super(context, R.layout.list_okcthread);
      this.context = context;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      View rowView = inflater.inflate(R.layout.list_okcthread, parent, false);
      TextView textView1 = (TextView) rowView.findViewById(R.id.okcthread1);
      TextView textView2 = (TextView) rowView.findViewById(R.id.okcthread2);
      TextView textView3 = (TextView) rowView.findViewById(R.id.okcthread3);
      
      OkcThread okcThread = this.getItem(position);
      if (okcThread.getIsUpdated()) {
         textView1.setBackgroundColor(0xffffffcc);
         textView2.setBackgroundColor(0xffffffcc);
         textView3.setBackgroundColor(0xffffffcc);
      }
      
      textView1.setText(okcThread.getSname());
      textView2.setText(okcThread.getTname());
      String text3 = okcThread.getTdate_formatted() + ", " + okcThread.getTposter();
      if (okcThread.getTidLastPage() != null) text3 += " (Last Page)";
      textView3.setText(text3);

      return rowView;
   }

   public Date getLastUpdated() {
      return this.lastUpdated;
   }
   public void setLastUpdated(Date lastUpdated) {
      this.lastUpdated = lastUpdated;
   }
   public void setClearAllIsUpdated() {
      int len = this.getCount();
      OkcThread okcThread;
      for (int i=0; i < len; i++) {
         okcThread = this.getItem(i);
         okcThread.setIsUpdated(false);
      }
   }

   public void serializeToWriter(Writer writer) throws OkcException {
      int len = this.getCount();
      List<JSONObject> list = new ArrayList<JSONObject>(len);
      JSONObject jsonOkcThread;
      for (int i=0; i < len; i++) {
         jsonOkcThread = this.getItem(i).getJSONObject();
         list.add(jsonOkcThread);
      }
      JSONArray jsonArray = new JSONArray(list);
      // jsonArrayAdapter contains jsonArray and lastUpdated
      JSONObject jsonArrayAdapter = new JSONObject();      
      try {
         jsonArrayAdapter.put(JSON_ARRAY, jsonArray);
         jsonArrayAdapter.put(LAST_UPDATED, OkcThread.toLong(this.lastUpdated));
         writer.write(jsonArrayAdapter.toString());
      } catch (IOException e) {
         throw new OkcException(e);
      } catch (JSONException e) {
         throw new OkcException(e);
      }
   }

   public void deserializeFromReader(Reader reader) throws OkcException {      
      BufferedReader br = new BufferedReader(reader);
      StringBuffer sb = new StringBuffer();
      String nextline;
      try {
         while ((nextline = br.readLine()) != null) {
            sb.append(nextline);
            sb.append("\n");
         }
      } catch (IOException e) {
         throw new OkcException(e);
      }
      String text = sb.toString();
      try {
         // jsonArrayAdapter contains jsonArray and lastUpdated
         JSONObject jsonArrayAdapter = new JSONObject(text);
         JSONArray jsonArray = (JSONArray) jsonArrayAdapter.get(JSON_ARRAY);
         JSONObject jsonObject;
         for (int i=0; i < jsonArray.length(); i++) {
            jsonObject = (JSONObject) jsonArray.get(i);
            this.add(new OkcThread(jsonObject));
         }
         Date lastUpdated = OkcThread.toDate((Long) jsonArrayAdapter.get(LAST_UPDATED));
         this.lastUpdated = lastUpdated;
      } catch (JSONException e) {
         throw new OkcException(e);
      }

   }
}

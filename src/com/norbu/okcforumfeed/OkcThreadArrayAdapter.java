package com.norbu.okcforumfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OkcThreadArrayAdapter extends ArrayAdapter<OkcThread> {

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
      textView1.setText(okcThread.getOkcSection().getSname());
      textView2.setText(okcThread.getTname());
      textView3.setText(okcThread.getTdate_formatted() + ", " + okcThread.getTposter());

      return rowView;
   }
}

package com.norbu.okcforumfeed;

import android.content.Context;
import android.util.Log;
import java.io.*;

public class Debug {

   private static PrintWriter pw = null;
   static boolean IS_DEBUG = true;

   public static void println(Object obj) {
      if (!Debug.IS_DEBUG) return;
      //System.out.println(obj);
      Log.d("OKCFF", obj.toString());
      pw.println(obj.toString());
   }

   public static void init(Context context) {
      try {
         FileOutputStream fos = context.openFileOutput("System.out", 0);
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
         pw = new PrintWriter(bw);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}

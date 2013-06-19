package com.norbu.okcforumfeed;

import android.util.Log;

public class Debug {

   static boolean IS_DEBUG = false;

   public static void println(Object obj) {
      if (!Debug.IS_DEBUG) return;
      //System.out.println(obj);
      Log.d("OKCFF", obj.toString());
   }
}

package com.norbu.okcforumfeed;

import android.content.Context;
import android.util.Log;
import java.io.*;

public class Debug {

   public static boolean IS_DEBUG = false;
   public static final int ERROR=4, WARNING=3, INFO=2, VERBOSE = 1;

   private static PrintWriter pw = null;
   private static int minlevel;

   public static void println(Object obj, int level) {
      if (level < minlevel) return;
      println(obj);
   }
   public static void println(Object obj) {
      if (!Debug.IS_DEBUG) return;
      Log.d("OKCFF", obj.toString());
      if (pw != null) pw.println(obj.toString());
   }

   public static void init(Context context, boolean isDebug, int minlevel) throws OkcException {
      IS_DEBUG = isDebug;
      Debug.minlevel = minlevel;

      //initPrintWriter(context);
      System.out.println("test message");
   }
   @SuppressWarnings("unused")
   private static void initPrintWriter(Context context) throws OkcException {
      FileOutputStream fos;
      try {
         fos = context.openFileOutput("System.out", 0);
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
         pw = new PrintWriter(bw);
         System.out.println("getFilesDir=" + context.getFilesDir().getAbsolutePath());
      } catch (FileNotFoundException e) {
         throw new OkcException(e);
      }
   }
}

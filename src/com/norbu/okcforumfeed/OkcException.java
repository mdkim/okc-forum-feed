package com.norbu.okcforumfeed;

public class OkcException extends Exception {

   private static final long serialVersionUID = 1L;
   
   public OkcException(String msg) {
      super(msg);
   }

   public OkcException(Throwable e) {
      super(e);
   }
}

package com.norbu.okcforumfeed;

public class OkcException extends Exception {

   private static final long serialVersionUID = 1L;
   
   public OkcException(String msg) {
      super(msg);
   }
   public OkcException(Throwable e) {
      super(e);
   }
   public OkcException(String msg, Throwable e) {
      super(msg, e);
   }
   
   public boolean isCauseIOException() {
      Throwable cause = this.getCause();
      if (cause == null) return false;
      if (cause instanceof java.io.IOException) {
         return true;
      }
      return false;
   }

   @Override
   public String getMessage() {
      Throwable cause = this.getCause();
      if (cause == null) return super.getMessage();
      String msg = cause.getMessage();
      return msg;
   }
}

package com.norbu.okcforumfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

class HttpScanner {

   private Date connectionDate = new Date();  
   private HttpURLConnection conn;
   private InputStream is;
   public Scanner scanner;

   // dump to Debug.println()
   public void testReadAll() throws OkcException {
      java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(this.is));
      try {
         String line;
         while ((line = reader.readLine()) != null) {
            Debug.println(line);
         }
      } catch (IOException e) {
         throw new OkcException(e);
      }
   }

   public HttpScanner(String url_s) throws OkcException {
      try {
         URL url = new URL(url_s);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setReadTimeout(10000); // milliseconds
         conn.setConnectTimeout(15000);
         conn.setRequestMethod("GET");
         conn.setDoInput(true);
         conn.connect();
         this.conn = conn;
         
         int response = conn.getResponseCode();
         if (response != 200) {
            if (conn != null) conn.disconnect();
            throw new OkcException("response = " + response);
         }
         Debug.println("response=" + response);
         InputStream is = conn.getInputStream();
         this.is = is;
         
         Scanner scanner = new Scanner(is);
         this.scanner = scanner;
      } catch (IOException e) {
         throw new OkcException(e);
      }
   }

   public void closeAll() {
      try {

         if (scanner != null) scanner.close();
         if (is != null) is.close();
         if (conn != null) conn.disconnect();
      } catch (IOException e) {
         e.printStackTrace();
      }
      Debug.println("closeAll() finished");
   }

   public Date getConnectionDate() {
      return this.connectionDate;
   }
}
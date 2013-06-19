package com.norbu.okcforumfeed;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

class OkcForumFeed {

   private static final String URL_SECTIONS = "http://www.okcupid.com/forum";
   private static final String URL_THREADS_PRE = "http://www.okcupid.com/forum?sid=";

   private static final Pattern PATT_SID_NAME = Pattern.compile("<a href=\\\"/forum\\?sid=([0-9]+?)\\\">([^<]+?)</a>");
   //private static final Pattern PATT_SDATE = Pattern.compile("posted <span class=\"fancydate\" id=\"fancydate_[0-9]+?\">(.+?)</span>");
   private static final Pattern PATT_SDATE = Pattern.compile("posted (.+?)</p>");

   private static final Pattern PATT_TID_NAME0 = Pattern.compile("<a href=\\\"/forum\\?tid=([0-9]+)\\\"[^>]*?>([^<]+)</a>");
   private static final Pattern PATT_TID_NAME1 = Pattern.compile("(.*?)<p class=\"created_by\">.*?</p>"); // group1 => "Last page"
   private static final Pattern PATT_TID_NAME2 = Pattern.compile("<a href=\\\"/forum\\?tid=[0-9]+?&amp;low=([0-9]+?)\\\">Last page</a>");
   //private static final Pattern PATT_TDATE = Pattern.compile("<a href=\\\"/profile/.+?/\\\">([^<]+)</a>, <span class=\"fancydate\" id=\"fancydate_[0-9]+?\">(.+?)</span>");
   private static final Pattern PATT_TDATE = Pattern.compile("<a href=\\\"/profile/.+?/\\\">([^<]+)</a>,\\s*(.+?)\\s*</p>");

   public static void main(String[] args) {
      Debug.IS_DEBUG = true;
      new OkcForumFeed().go();
   }

   void go() {
      
      Date lastUpdated = getLastUpdated();
      
      Map<String, OkcSection> okcSectionMap = new LinkedHashMap<String, OkcSection>();
      List<OkcThread> okcThreadList = new ArrayList<OkcThread>();
      HttpScanner httpScanner = null;
      try {

         Debug.println("--- PARSING SECTIONS ---");
         // okcSections
         httpScanner = getHttpScanner(URL_SECTIONS + "?enable_mobile=1");
         while (true) {
            OkcSection nextSection = findNextOkcSection(httpScanner);
            if (nextSection == null) break;
            Debug.println(isAfterLastUpdated(nextSection.getSdate_d(), lastUpdated) + ": " + nextSection);
            okcSectionMap.put(nextSection.getSid(), nextSection);
         }
         httpScanner.closeAll();

         Debug.println("\n--- PARSING THREADS ---");
         // okcThreads
         for (OkcSection nextSection : okcSectionMap.values()) {
            if (!isAfterLastUpdated(nextSection.getSdate_d(), lastUpdated)) {
               Debug.println("\n--- SKIPPING THREADS (sid=" + nextSection.getSid() + ") ---");
               continue;
            }
            Debug.println("\n--- PARSING THREADS (sid=" + nextSection.getSid() + ") ---");
            httpScanner = getHttpScanner(URL_THREADS_PRE + nextSection.getSid() + "&enable_mobile=1");
            while (true) {
               OkcThread nextThread = findNextOkcThread(httpScanner, nextSection);
               if (nextThread == null) break;
               Debug.println(nextThread);
               nextSection.addThread(nextThread);
               okcThreadList.add(nextThread);
            }
            httpScanner.closeAll();
         }

      // display ALL THREADS (SORTED) after lastUpdated
         // scrollable
         // make each open a link to tid+lastPage
         Debug.println("\n--- ALL THREADS (SORTED) after " + lastUpdated + " ---");
         Collections.sort(okcThreadList, OkcThread.OKC_THREAD_DATE_COMPARATOR);
         for (OkcThread nextThread : okcThreadList) {
            if (!isAfterLastUpdated(nextThread.getTdate_d(), lastUpdated)) {
               Debug.println("...");
               continue;
            }
            Debug.println(nextThread);
         }
         
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (httpScanner != null) httpScanner.closeAll();
      }
   }

   private static OkcSection findNextOkcSection(HttpScanner httpScanner) throws OkcException {
      
      Scanner scanner = httpScanner.scanner;
      OkcSection okcSection = new OkcSection(httpScanner.getConnectionDate());
      // sid, sname
      String foundText = scanner.findWithinHorizon(PATT_SID_NAME, 0);
      if (foundText == null) return null;

      Matcher m = PATT_SID_NAME.matcher(foundText);
      boolean isFound = m.matches();
      if (!isFound) throw new OkcException("PATT_SID_NAME not found: " + foundText);
      okcSection.setSid(m.group(1));
      okcSection.setSname(m.group(2));

      // sdate
      foundText = scanner.findWithinHorizon(PATT_SDATE, 0);
      if (foundText == null) return null;

      m = PATT_SDATE.matcher(foundText);
      isFound = m.matches();
      if (!isFound) throw new OkcException("PATT_SID_SDATE not found: " + foundText);
      okcSection.setSdate(m.group(1));

      return okcSection;
   }

   private static OkcThread findNextOkcThread(HttpScanner httpScanner, OkcSection okcSection) throws OkcException {

      Scanner scanner = httpScanner.scanner;
      OkcThread okcThread = new OkcThread(httpScanner.getConnectionDate(), okcSection);
      // tid, tname
      String foundText = scanner.findWithinHorizon(PATT_TID_NAME0, 0);
      if (foundText == null) return null;

      Matcher m = PATT_TID_NAME0.matcher(foundText);
      boolean isFound = m.matches();
      if (!isFound) throw new OkcException("PATT_TID_NAME0 not found: " + foundText);
      okcThread.setTid(m.group(1));
      okcThread.setTname(m.group(2));

      // tidLastPage
      foundText = scanner.findWithinHorizon(PATT_TID_NAME1, 0);
      if (foundText == null) return null;

      m = PATT_TID_NAME1.matcher(foundText);
      isFound = m.matches();
      if (!isFound) throw new OkcException("PATT_TID_NAME1 not found: " + foundText);
      String lastPageGroup = m.group(1);

      m = PATT_TID_NAME2.matcher(lastPageGroup);
      isFound = m.find();
      if (isFound) {
         String tidLastPage = m.group(1);
         okcThread.setTidLastPage(tidLastPage);
      }

      // tposter, tdate
      foundText = scanner.findWithinHorizon(PATT_TDATE, 0);
      if (foundText == null) return null;

      m = PATT_TDATE.matcher(foundText);
      isFound = m.matches();
      if (!isFound) throw new OkcException("PATT_TDATE not found: " + foundText);
      okcThread.setTposter(m.group(1));
      okcThread.setTdate(m.group(2));

      return okcThread;
   }

   private static HttpScanner getHttpScanner(String url) throws OkcException {
      
      // temp
      //HttpScanner httpScanner0 = new HttpScanner(url);
      //httpScanner0.testReadAll();
      //httpScanner0.closeAll();
      
      HttpScanner httpScanner = new HttpScanner(url);
      return httpScanner;
   }

   // temporary implementation
   private static Date getLastUpdated() {
      Date lastUpdated = new Date();
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Alaska"));
      cal.setTime(lastUpdated);
      cal.add(Calendar.HOUR, -12);
      lastUpdated = cal.getTime();
      return lastUpdated;
   }
   private static boolean isAfterLastUpdated(Date date, Date lastUpdated) {
      boolean isAfterLastUpdated = (date.compareTo(lastUpdated) > 0);
      return isAfterLastUpdated;
   }
}


package com.norbu.okcforumfeed;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import java.text.DateFormat;

import com.norbu.okcforumfeed.OkcException;

class OkcSection {

   private Date connectionDate;
   private List<OkcThread> okcThreadList = null;

   private String sid, sname, sdate;
   private Date sdate_d;
   
   // "h:mma"
   private static final DateFormat sdf_hmma = new SimpleDateFormat("h:mma", Locale.ENGLISH);
   
   public OkcSection(Date connectionDate) {
      this.connectionDate = connectionDate;
   }

   @Override
   public String toString() {
      return "[sid=" + sid + ", sname=" + sname + ", sdate_d=" + sdate_d + ", sdate=" + sdate + "]";
   }

   // parsing date: Just Now, N minutes ago, Today, Yesterday, Mmm Dd
   public static Date parseDate(String text, Date connectionDate) throws OkcException {
      if (text == null) return null;
      // <em>Just now!</em>
      if (text.indexOf("Just now!") > -1) {
         return connectionDate;
      }
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(OkcForumFeed.OKC_TIMEZONE));
      cal.setTime(connectionDate);
      // 9 minutes ago
      if (text.endsWith("minutes ago")) {
         String[] text1 = text.split(" ", 2);
         if (text1.length < 2) throw new OkcException("unexpected minutes: " + text);
         int minutes;
         try {
            minutes = Integer.parseInt(text1[0]);
         } catch (NumberFormatException e) {
            throw new OkcException(e);
         }
         cal.add(Calendar.MINUTE, -minutes);
         return cal.getTime();
      }
      // Today &ndash; 4:54pm
      if (text.startsWith("Today &ndash; ")) {
         String text1 = text.substring(14); // length of "Today &ndash; " is 14
         setCalendarTimeOfDay(cal, text1);
         return cal.getTime();
      }
      // Yesterday &ndash; 11:46am
      if (text.startsWith("Yesterday")) {
         String text1 = text.substring(18); // length of "Yesterday &ndash; " is 18
         setCalendarTimeOfDay(cal, text1);
         cal.add(Calendar.DATE, -1);
         return cal.getTime();
      }
      // Jun 16
      if (text.indexOf(", ") < 0) text += ", " + cal.get(Calendar.YEAR);
      //"MMM d, yyyy"
      DateFormat sdf_MMM_d_yyyy = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US);
      Date date1;
      try {
         date1 = sdf_MMM_d_yyyy.parse(text);
      } catch (ParseException e) {
         throw new OkcException(e);
      }      
      return date1;
   }
   private static void setCalendarTimeOfDay(Calendar cal, String time_text) throws OkcException {
      Date date1;
      try {
         date1 = sdf_hmma.parse(time_text);
      } catch (ParseException e) {
         throw new OkcException(e);
      }
      Calendar cal1 = Calendar.getInstance();
      cal1.setTime(date1);
      cal.set(Calendar.HOUR_OF_DAY, cal1.get(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE));
   }
   
   public void addThread(OkcThread nextThread) {
      if (this.okcThreadList == null) this.okcThreadList = new ArrayList<OkcThread>();
      this.okcThreadList.add(nextThread);
   }
   
   // getters, setters
   public String getSid() {
      return sid;
   }
   public void setSid(String sid) throws OkcException {
      try {
         new BigDecimal(sid);
      } catch (NumberFormatException e) {
         throw new OkcException("unexpected sid=" + sid);
      }
      this.sid = sid;
   }
   public String getSname() {
      return sname;
   }
   public void setSname(String sname) {
      this.sname = sname;
   }
   public String getSdate() {
      return sdate;
   }
   public void setSdate(String sdate) throws OkcException {
      this.sdate = sdate;
      this.setSdate_d(parseDate(sdate, this.connectionDate));
   }
   public Date getSdate_d() {
      return sdate_d;
   }
   private void setSdate_d(Date sdate_s) {
      this.sdate_d = sdate_s;
   }
}
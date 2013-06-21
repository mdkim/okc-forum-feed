package com.norbu.okcforumfeed;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.norbu.okcforumfeed.OkcException;

class OkcThread {

   public static final SimpleDateFormat sdf_hhmm_ddMMMyyyy = new SimpleDateFormat("hh:mma dd MMM yyyy [z]", Locale.US);
   private Date connectionDate;
   
   private String tid, tname, tidLastPage, tposter, tdate;
   private Date tdate_d;
   private OkcSection okcSection;

   public static final Comparator<OkcThread> OKC_THREAD_DATE_COMPARATOR = new Comparator<OkcThread>() {
      public int compare(OkcThread t1, OkcThread t2) {
         return t2.getTdate_d().compareTo(t1.getTdate_d());
      }
   };
   
   public OkcThread(Date connectionDate, OkcSection okcSection) {
      this.connectionDate = connectionDate;
      this.okcSection = okcSection;
   }

   @Override
   public String toString() {
      return "[okSection=" + okcSection.getSid() + ", " + okcSection.getSname() + "\ntid=" + tid + ", tname=" + tname + ", tidLastPage="
            + tidLastPage + ", tposter=" + tposter + ", tdate_d=" + tdate_d + ", " + tdate + "]";
   }

   // getters, setters
   public String getTid() {
      return tid;
   }
   public void setTid(String tid) throws OkcException {
      try {
         new BigDecimal(tid);
      } catch (NumberFormatException e) {
         throw new OkcException("unexpected tid=" + tid);
      }
      this.tid = tid;
   }
   public String getTname() {
      return tname;
   }
   public void setTname(String tname) {
      tname = tname.replaceAll("(?:\\n|\\r)", " ");
      tname = tname.trim();
      tname = OkcSection.parseHtml(tname);
      this.tname = tname;
   }
   public String getTidLastPage() {
      return tidLastPage;
   }
   public void setTidLastPage(String tidLastPage) {
      this.tidLastPage = tidLastPage;
   }
   public String getTposter() {
      return tposter;
   }
   public void setTposter(String tposter) {
      this.tposter = tposter;
   }
   public String getTdate() {
      return this.tdate;
   }
   public void setTdate(String tdate) throws OkcException {
      this.tdate = tdate;
      this.setTdate_d(OkcSection.parseDate(tdate, this.connectionDate));
   }
   public String getTdate_formatted() {
      if (this.tdate_d == null) return null;
      String text = sdf_hhmm_ddMMMyyyy.format(this.tdate_d);
      return text;      
   }
   public Date getTdate_d() {
      return this.tdate_d;
   }
   private void setTdate_d(Date tdate_d) {
      this.tdate_d = tdate_d;
   }
   public OkcSection getOkcSection() {
      return okcSection;
   }
}
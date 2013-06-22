package com.norbu.okcforumfeed;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.norbu.okcforumfeed.OkcException;

class OkcThread {

   public static final SimpleDateFormat sdf_hhmm_ddMMMyyyy = new SimpleDateFormat("hh:mma dd MMM yyyy [z]", Locale.US);

   private String tid, tname, tidLastPage, tposter, tdate;
   private Date tdate_d;
   //private OkcSection okcSection; // only needed for sname
   private String sname;
   private Date connectionDate;
   private boolean isUpdated;
   
   private static final String TID="TID", TNAME="TNAME", TID_LAST_PAGE="TID_LAST_PAGE", TPOSTER="TPOSTER", TDATE="TDATE",
         TDATE_D="TDATE_D", SNAME="SNAME", CONNECTION_DATE="CONNECTION_DATE", IS_UPDATED="IS_UPDATED";
   
   public static final Comparator<OkcThread> OKC_THREAD_DATE_COMPARATOR = new Comparator<OkcThread>() {
      public int compare(OkcThread t1, OkcThread t2) {
         return t2.getTdate_d().compareTo(t1.getTdate_d());
      }
   };
   
   public OkcThread(Date connectionDate, OkcSection okcSection) {
      this.connectionDate = connectionDate;
      //this.okcSection = okcSection;
      this.sname = okcSection.getSname();
   }

   public OkcThread(JSONObject jsonObject) throws JSONException {
      this.tid = jsonObject.isNull(TID) ? null : jsonObject.getString(TID);
      this.tname = jsonObject.isNull(TNAME) ? null : jsonObject.getString(TNAME);
      this.tidLastPage = jsonObject.isNull(TID_LAST_PAGE) ? null : jsonObject.getString(TID_LAST_PAGE);
      this.tposter = jsonObject.isNull(TPOSTER) ? null : jsonObject.getString(TPOSTER);
      this.tdate = jsonObject.isNull(TDATE) ? null : jsonObject.getString(TDATE);
      this.sname = jsonObject.isNull(SNAME) ? null : jsonObject.getString(SNAME);
      // Date
      this.tdate_d = jsonObject.isNull(TDATE_D) ? null : toDate((Long) jsonObject.get(TDATE_D));
      this.connectionDate = jsonObject.isNull(CONNECTION_DATE) ? null : toDate((Long) jsonObject.get(CONNECTION_DATE));
      // boolean
      this.isUpdated = jsonObject.isNull(IS_UPDATED) ? false : (Boolean) jsonObject.get(IS_UPDATED);
   }

   @Override
   public String toString() {
      return "[okSection=" + sname + "\ntid=" + tid + ", tname=" + tname + ", tidLastPage="
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
   public Date getTdate_d() {
      return this.tdate_d;
   }
   private void setTdate_d(Date tdate_d) {
      this.tdate_d = tdate_d;
   }
   public String getTdate_formatted() {
      if (this.tdate_d == null) return null;
      String text = sdf_hhmm_ddMMMyyyy.format(this.tdate_d);
      return text;      
   }
   public String getSname() {
      return this.sname;
   }
   public boolean getIsUpdated() {
      return this.isUpdated;
   }
   public void setIsUpdated(boolean isUpdated) {
      this.isUpdated = isUpdated;
   }

   public JSONObject getJSONObject() throws OkcException {
      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put(TID, this.tid);
         jsonObject.put(TNAME, this.tname);
         jsonObject.put(TID_LAST_PAGE, this.tidLastPage);
         jsonObject.put(TPOSTER, this.tposter);
         jsonObject.put(TDATE, this.tdate);
         jsonObject.put(TDATE_D, toLong(this.tdate_d));
         jsonObject.put(SNAME, this.sname);
         jsonObject.put(CONNECTION_DATE, toLong(this.connectionDate));
         jsonObject.put(IS_UPDATED, this.isUpdated);
      } catch (JSONException e) {
         throw new OkcException(e);
      }
      return jsonObject;
   }
   public static Long toLong(Date date) throws OkcException {
      if (date == null) return null;
      Long date_long;
      try {
         date_long = Long.valueOf(date.getTime());
      } catch (NumberFormatException e) {
         throw new OkcException(e);
      }
      return date_long;
   }
   public static Date toDate(Long date_long) {
      if (date_long == null) return null;
      long date_l = date_long.longValue();
      Date date = new Date(date_l);
      return date;
   }  
}
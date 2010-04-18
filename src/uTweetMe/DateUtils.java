package uTweetMe;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

/**
 * @brief utils for parsing date
 */

public class DateUtils {
   static Hashtable months = null;

   /**
    *  Parse date in format like "Sun Mar 08 01:20:09 +0000 2009"
    * @param i_dateString in string date
    * @return date/time
    */
   static long ParseDate(String i_dateString) {
      Vector parts = split(i_dateString, " ");
      final int year = Integer.parseInt((String)parts.elementAt(5));
      final int month = getMonthFromString((String)parts.elementAt(1));
      final int day = Integer.parseInt((String)parts.elementAt(2));

      Vector timeParts = split((String)parts.elementAt(3), ":");
      final int hour = Integer.parseInt((String)timeParts.elementAt(0));
      final int min = Integer.parseInt((String)timeParts.elementAt(1));
      final int sec = Integer.parseInt((String)timeParts.elementAt(2));

      TimeZone timezoneGMT = TimeZone.getTimeZone("GMT");
      Calendar calendar = Calendar.getInstance(timezoneGMT);
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, month);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, min);
      calendar.set(Calendar.SECOND, sec);
      calendar.set(Calendar.MILLISECOND, 0);

      return calendar.getTime().getTime();
   }

   /**
    *  Parse date in format like "2009-03-02T07:38:09Z"
    * @param i_dateString in string date
    * @return date/time
    */
   static long ParseSearchDate(String i_dateString) {
      Vector parts = split(i_dateString, "T");
      final String strDate = (String)parts.elementAt(0);

      String strTime = (String)parts.elementAt(1);
      strTime = strTime.substring(0, strTime.indexOf("Z"));

      Vector dateParts = split(strDate, "-");
      Vector timeParts = split(strTime, ":");

      final int year = Integer.parseInt((String)dateParts.elementAt(0));
      final int month = Integer.parseInt((String)dateParts.elementAt(1));
      final int day = Integer.parseInt((String)dateParts.elementAt(2));

      final int hour = Integer.parseInt((String)timeParts.elementAt(0));
      final int min = Integer.parseInt((String)timeParts.elementAt(1));
      final int sec = Integer.parseInt((String)timeParts.elementAt(2));

      TimeZone timezoneGMT = TimeZone.getTimeZone("GMT");
      Calendar calendar = Calendar.getInstance(timezoneGMT);
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, month - 1);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, min);
      calendar.set(Calendar.SECOND, sec);
      calendar.set(Calendar.MILLISECOND, 0);

      return calendar.getTime().getTime();
   }


   /**
    * Formats date to string representation
    * @param i_time in, date/time to format
    * @return string date representation
    */
   static String FormatDate(long i_time) {
      Date date = new Date(i_time);
      TimeZone timezoneLocal = TimeZone.getDefault();
      Calendar calendar = Calendar.getInstance(timezoneLocal);
      calendar.setTimeZone(timezoneLocal);
      calendar.setTime(date);

      // Add leading zero, if minute has 1 digit.
      String minute = String.valueOf(calendar.get(Calendar.MINUTE));
      if (1 == minute.length()) {
         minute = "0" + minute;
      }

      // If we have 12:XX PM, then Calendar.HOUR returns 0. We change it to 12.
      final boolean am = (calendar.get(Calendar.AM_PM) == Calendar.AM);
      int hour = calendar.get(Calendar.HOUR);
      if (hour == 0 && !am) {
         hour = 12;
      }

      String text = String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
         String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
         String.valueOf(calendar.get(Calendar.YEAR)) + " " +
         String.valueOf(calendar.get(Calendar.HOUR)) + ":" +
         minute + " " + (am ? "AM" : "PM");
      return text;
   }

   private static int getMonthFromString(String i_monthStr) {
      
      if (null == months) {
         months = new Hashtable();
         months.put("Jan", new Integer(Calendar.JANUARY));
         months.put("Feb", new Integer(Calendar.FEBRUARY));
         months.put("Mar", new Integer(Calendar.MARCH));
         months.put("Apr", new Integer(Calendar.APRIL));
         months.put("May", new Integer(Calendar.MAY));
         months.put("Jun", new Integer(Calendar.JUNE));
         months.put("Jul", new Integer(Calendar.JULY));
         months.put("Aug", new Integer(Calendar.AUGUST));
         months.put("Sep", new Integer(Calendar.SEPTEMBER));
         months.put("Oct", new Integer(Calendar.OCTOBER));
         months.put("Nov", new Integer(Calendar.NOVEMBER));
         months.put("Dec", new Integer(Calendar.DECEMBER));
      }

      final Integer mon = (Integer) months.get(i_monthStr);
      return mon.intValue();
   }

   private static Vector split(String i_string, String i_separator) {
      int begin = 0;
      Vector res = new Vector();
      int end = i_string.indexOf(i_separator, begin);
      while (end != -1) {
         String sub = i_string.substring(begin, end);
         res.addElement(sub);
         begin = end + 1;
         end = i_string.indexOf(i_separator, begin);
      }
      String sub = i_string.substring(begin);
      res.addElement(sub);
      return res;
   }
}

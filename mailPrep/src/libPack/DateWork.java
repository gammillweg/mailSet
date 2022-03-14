 package libPack;


 import java.text.DateFormatSymbols;
import java.util.*;

public class DateWork
{
   public DateWork()
   {
      fillWeekStrMap();
      fillWeekIntMap();  // order sensitive... uses results form fillWeekStrMap()
   }


   // Returns an array filled with abbreviated spellings of all 12 months
   // January is in [0]
   public String[] getMonthAbbreviations()
   {
      String[] months = new DateFormatSymbols().getShortMonths();

      //The array, months, returned from geMonths() is one member
      // to long.  The last member is empty. Here I chop the last off.
      String[] monthsAry = new String[months.length-1];
      monthsAry = Arrays.copyOfRange(months, 0, 12);

      return monthsAry;
   }

   // Return an array filled with the complete spelling of all 12 months
   // January is in [0].
   public String[] getMonthFullNames()
   {
      String[] months = new DateFormatSymbols().getMonths();
      // months returned from geMonths() is one member to long
      // So here I cut the last off.
      String[] monthsAry = new String[months.length-1];
      monthsAry = Arrays.copyOfRange(months, 0, 12);
      return monthsAry;
   }

   // Get the current month[0] and year[1]
   // The returned value from Calendar is zero based.
   // The value of the int is one less than what may be expected.
   // Integer Jan == 0  (NOT 1)  Integer Dec == 11 (NOT 12)
   public int[] getCurrentMonthYear()
   {
      Calendar calendar = GregorianCalendar.getInstance();
      int[] monthYear = new int[2];
      monthYear[0] = calendar.get(Calendar.MONTH);
      monthYear[1] = calendar.get(Calendar.YEAR);

      // The returned value from Calendar is zero based.
      // The value of the int is one less than what may be expected.
      // Integer Jan == 0  (NOT 1)  Integer Dec == 11 (NOT 12)
      return monthYear;
   }

   // Returns the date of the fourth Tuesday of the current month
   public int getTheFourthTueOfTheCurrentMonth()
   {
      int fourthTue = 0;
      int firstDOW = getFirstDOWCurrentMonth();
      
      // if Tue or less need to account for days till Tue, then jump 22 days
      // if past Tue need to account for days left in the week plus
      // the 3 days till Tue then jump 21 days
      if (firstDOW < 4)
      {
         fourthTue = (4 - firstDOW) + 21;
      }
      else
      {
         fourthTue = (8 - firstDOW) + 24;  // 3+21=42 (3 days till Tue)
      }
      //internalMsgCtrl.out("fourthTue: " + fourthTue);
      return fourthTue;
   }

   // Returns the first "day of the week" (DOW) of the current month.
   // Calendar, DOW: Sunday == 1
   public int getFirstDOWCurrentMonth()
   {
      Date today =  GregorianCalendar.getInstance().getTime();
      long timestamp = today.getTime();

      Calendar calendar = GregorianCalendar.getInstance();
      calendar.clear();
      calendar.setTimeInMillis(timestamp);
      while (calendar.get(Calendar.DATE) > 1)
      {
         // Subtract 1 day until first day of month.
         calendar.add(Calendar.DATE, -1);
      }
      int dow = calendar.get(Calendar.DAY_OF_WEEK);

      // Remember, in Calendar, day of week (dow): Sunday is 1

      // This is the first day of the first week of the current month
      return dow;
   }

   //==============================================================
   // A set, initialize in the constructor to create a map of DOW
   // Sunday=1, Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6, Saturday=7
   
   // Create a private Map to validate DOW in public getDOWStr()
   private Map< String, Integer> weekStrMap = null;
   private void fillWeekStrMap()
   {
      // create calendar and locale
      Calendar cal = GregorianCalendar.getInstance();
      Locale locale = Locale.getDefault();

      // call the getdisplaynames method
      Map<String, Integer> map = cal.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
      weekStrMap = new TreeMap< String, Integer>(map);

      // print the results
      // System.out.printf("Whole list:%n%s%n", weekNavMap);
   }

   // Create a private Map to validate DOW in public getDOWInt()
   // Reverses the Key and Value of weekStrMap (from cal.getDisplayNames())
   // into weekIntMap
   private Map< Integer, String> weekIntMap = null;
   private void fillWeekIntMap()
   {
    weekIntMap = new TreeMap<Integer, String>();
    for (Map.Entry<String, Integer> entry: weekStrMap.entrySet())
    {
       weekIntMap.put(entry.getValue(), entry.getKey());
    }

      // print the results
      //System.out.printf("Whole list:%n%s%n", weekIntMap);
   }
   
   // Converts the String DOW to an integer DOW
   // Calendar, DOW: Sunday == 1
   // Valid DOW: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
   public int getDOWStrToInt(String dow)
   {
      //-------------------------------------------------
      // Insure a correctly spelled Day Of Week (dow)
      int cnt = 0;
      String errStr = "Invalid argument: " + dow + ", valid arguments are: ";
      for (String key: weekStrMap.keySet())
      {
         if (key.equals(dow)) break;
         errStr += key + ",";
         cnt++;
      }
      if (cnt == 7)
      {
         throw new IllegalArgumentException(errStr);
      }
      //-------------------------------------------------


      int dowVal = weekStrMap.get(dow);
      return dowVal;
   }
   
   // Converts the integer DOW to a String DOW
   // Calendar, DOW: Sunday == 1
   // Valid int DOW: 1, 2, 3, 4, 5, 6, 7
   // Return DOW: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
   public String getDOWIntToStr(int dow)
   {
      //-------------------------------------------------
      if ((dow < 1) || (dow > 7))
      {
         throw new IllegalArgumentException("Range Error: dow=" + dow + ": getDOWStr(): dow range is 1-7");
      }
      //-------------------------------------------------


      String dowStr = weekIntMap.get(dow);
      return dowStr;
   }
   //==============================================================
}
   
   

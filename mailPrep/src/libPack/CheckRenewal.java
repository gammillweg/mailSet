package libPack;

import libPack.InternalMsgCtrl.errKey;

import java.util.ArrayList;
import java.util.Collections;

// FIXIT -- if you can.. if an invalid date, like "jkj-23" then not all invalid dates are shown.
// one has to fix and rerun.  I found this for (maybe) a blank date and a date like "Jan 22" (no "-")

public class CheckRenewal
{
   public CheckRenewal(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();

      // RenewalMonth is one greater than the current month but still Java 0 based
      // Example: if current jan (value 0) then renewalMonth is Feb (value 1)
      renewalMonth = shared.getRenewalMonthInt();

      renewalYear = shared.getRenewalYearInt();
   }

   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;
   int renewalMonth;
   int renewalYear;

   // These are special months where the Bulletin is not published.
   // Hence, no issue can or should ever expire in July or August
   final int JULY = 6;    // Read the comment on the return from Java Class Calendar (just below)
   final int AUGUST = 7;  // Read the comment on the return from Java Class Calendar (just below)
   
   // only used by this method so defined here

   // The returned value from Calendar is zero based.
   // The value of the int is one less than what may be expected.
   // Integer Jan == 0  (NOT 1)  Integer Dec == 11 (NOT 12)

   enum MONTHS
   {
      Jan(0),  // Read the comment on the return from Java Class Calendar (just above)
      Feb(1),
      Mar(2),
      Apr(3),
      May(4),
      Jun(5),
      Jul(6),
      Aug(7),
      Sep(8),
      Sept(8),
      Oct(9),
      Nov(10),
      Dec(11);

      private final int month;
      // constructor
      MONTHS(int month)
      {
         this.month = month;
      }
      // Return the INteger value of the enum
      public int intValueOf() { return this.month; }
   }

   // Displays error message if finds a date older than the renewal date, then return true
   // Else returns false (false is a good return)
   public boolean containsExpired(Addr[] addrZipAry)
   {
      boolean errFlg = false;
      ArrayList<String> expiredAL = new ArrayList<String>();

      //int line = 1; The data has been sorted so the line number is not useful to the user.
      Addr addr = null;
      try
      {
         //int dbCnt = 1;
         for(int inx = 0; inx < addrZipAry.length; inx++)
         {
            addr = addrZipAry[inx];
            if (addr.renewal.contains("-"))
            {
               // Example format:  "Sep-09"
               String[] usersRenewalAry = addr.renewal.split("-");

               // Following two lines pull the int value from enum MONTHS of a matching string.
               // 1) Convert the string with ary[0] to a MONTHS enum
               // 2) Convert the MONTHS enum to it's int value
               MONTHS monthsEnum = MONTHS.valueOf(usersRenewalAry[0]);
               int mon = monthsEnum.intValueOf();
               //System.out.println(dbCnt + ") " + monthsEnum);
               //dbCnt++;

               // compare the month int value of July and August
               if (mon==JULY)
               {
                  internalMsgCtrl.err(errKey.Message, false, "There is an expiration in July.  This should not happen.");
                  internalMsgCtrl.err(errKey.Message, false, "   " + addr.csv);
                  return true;
               }
               if (mon == AUGUST)
               {
                  internalMsgCtrl.err(errKey.Message, false,"There is an expiration in August.  This should not happen.");
                  internalMsgCtrl.err(errKey.Message, false,"   " + addr.csv);
                  return true;
               }

               int usersRenewalYear = Integer.valueOf(usersRenewalAry[1]);

               // will certainly not be past expired if the year has not been obtained.
               if (usersRenewalYear > renewalYear) continue;

               if (usersRenewalYear < renewalYear)
               {
                  String[] csv = addr.csv.split(",");
                  // Then the renewal date is an Expired date and should not be in the data
                  //internalMsgCtrl.err(errKey.Message,"Expired  " + addr.renewal + ":\t" + csv[0] + ", " + csv[1]);
                  //String expired = "Expired  " + addr.renewal + ":\t" + csv[0] + ", " + csv[1];
                  String expired = csv[0] + ", " + csv[1] + ":  Expired " + addr.renewal;
                  expiredAL.add(expired);
               }

               if (mon < renewalMonth)
               {
                  String[] csv = addr.csv.split(",");
                  // Then the renewal date is an Expired date and should not be in the data
                  //internalMsgCtrl.err(errKey.Message,"Expired  " + addr.renewal + ":\t" + csv[0] + ", " + csv[1]);
                  //String expired = "Expired  " + addr.renewal + ":\t" + csv[0] + ", " + csv[1];
                  String expired = csv[0] + ", " + csv[1] + ":  Expired " + addr.renewal;
                  expiredAL.add(expired);
               }
            }
            else if (addr.renewal.contains("COMP"))
            {
               // Strings contraining "COMP" are an exception to the formatting error rules
               // Do nothing
            }
            else
            {
               if (addr.renewal.isEmpty())
               {
                  String[] csv = addr.csv.split(",");
                  String expired = csv[0] + ", " + csv[1] + ":  Empty field " + "[]";
                  expiredAL.add(expired);
               }
               else
               {
                  // Does not contain a "-" or breaks a formatting rule
                  String[] csv = addr.csv.split(",");
                  String expired = csv[0] + ", " + csv[1] + ":  Format Error [" + addr.renewal + "]";
                  expiredAL.add(expired);
               }
            }

            //line++; The data is sorted... the line number is not useful to the user
            // else is likely a COMP and is to be ignored
         }
      }
      catch (IllegalArgumentException excp)
      {
         // expiredAL will be reformatted per two field by ":"
         String[] csv = addr.csv.split(",");
         String errMsg = csv[0] + ", " + csv[1] + ":" + "  Invalid date " + addr.renewal;
         expiredAL.add(errMsg);
      }

      if (expiredAL.size() > 0)
      {
         // The original string was "Expired xxx-xx: last, first"
         // But I wanted to sort on the last name so the string became
         //     "last, first: Expired xxx=xx"
         // Here I sort on the last name, then in the loop I reformat back to leading "Expired"
         Collections.sort(expiredAL);

         String addressFilePathName = shared.getAddressListFilePathName();
         internalMsgCtrl.err(errKey.Message, false, "Processed file: " + addressFilePathName);
         internalMsgCtrl.err(errKey.Message, false, "The following are rejected \"Renewal\" column dates:");
         for (String str : expiredAL)
         {
            String[] ary = str.split(":");
            String tmp = ary[1] + ":\t" + ary[0];
            internalMsgCtrl.err(errKey.Message, false, tmp);
         }
         errFlg = true;
      }

      // on error returns errFlg == true;
      return  errFlg;
   }

   // Used to find if the current subscription is about to expire
   public boolean isExpiring(Addr addr)
   {
      boolean errFlg = false;
      try
      {
         // I do not test for formatting because the entire list was previously tested.
         
         // Example format:  "Sep-09"
         String[] ary = addr.renewal.split("-");

         // Following two lines pull the int value from enum MONTHS of a matching string.
         // 1) Convert the string with ary[0] to a MONTHS enum
         // 2) Convert the MONTHS enum to it's int value
         MONTHS monthsEnum = MONTHS.valueOf(ary[0]);
         int mon = monthsEnum.intValueOf();

         if (mon == renewalMonth)
         {
            int yr = Integer.valueOf(ary[1]);
            if (yr <= renewalYear)
            {
               // Then the renewal date will Expire this month
               errFlg = true; // true return is an error
            }
         }
      }
      catch (IllegalArgumentException excp)
      {
         //excp.printStackTrace();
         internalMsgCtrl.err(errKey.ExceptionMsg, false, excp.toString());
         internalMsgCtrl.err(errKey.Message, false, "Invalid renewal data");
         return true;  // true is an error return
      }

      // return of true: the renewal date is expiring
      return  errFlg;
   }
}

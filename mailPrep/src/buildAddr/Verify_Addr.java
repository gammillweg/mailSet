package buildAddr;

import configPack.XMLData;
import libPack.ExcelUtils;
import libPack.InternalMsgCtrl;
import libPack.MailPrepUtils;
import libPack.Shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Verify_Addr
{
   Shared shared = null;
   XMLData xmlData = null;
   MailPrepUtils bulletinAssemblyUtils = null;
   InternalMsgCtrl internalMsgCtrl = null;
   public Verify_Addr(Shared shared)
   {
      this.shared = shared;
      xmlData = shared.getXMLData();
      bulletinAssemblyUtils = new MailPrepUtils(shared);
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   public boolean verifyAddrCSV_Main(ArrayList<String> AddressListAL)
   {
      boolean noReminder = shared.getNoReminder();
      if (noReminder==false)
      {
         checkDateReminder();
      }
      return checkForHeaders(AddressListAL);
   }

   private boolean checkForHeaders(ArrayList<String> fileContent)
   {
      ArrayList<String> headerStringAL = getHeaderContent(fileContent);
      if (headerStringAL.size()==0) return false;

      // String[] addressListColumns = xmlData.getAddressListColumns();
      String[] addressListHeadings = xmlData.getAddressListHeadings();

      for (int inx = 0; inx < addressListHeadings.length; inx++)
      {
         if (!addressListHeadings[inx].equals(headerStringAL.get(inx)))
         //if (headerStringAL.get(inx) != addressListHeadings[inx])
         {
            String errMsg = ("Expected header:  " + addressListHeadings[inx] +
                                   ", Found:  " + headerStringAL.get(inx));
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false, errMsg);
            //errList.add("Expected:  " + headerConfiguredList[inx][1] + ", Found:  " + headerStringList[inx]);
            return false;
         }
      }
      return true;
   }

   // Gets the header in the same order as GetHeaderColumns()
   // Returns a list of headers (Row 1 from fileContent)
   private ArrayList<String> getHeaderContent(ArrayList<String> fileContent)
   {
      // Returns members like "Zip, G", "Plus4, H", and "Renewal, J"
      String[] headerColumnAry = xmlData.getAddressListColumns();
      ExcelUtils excelUtils = shared.getExcelUtils();

      ArrayList<String> headerAL = new ArrayList<String>(headerColumnAry.length);

      // Get the columns out of the content list
      try {
         for (String str : headerColumnAry)
         {
            int column = excelUtils.excelColumnToInt(str);
            // We are interested in the only row 1 of fileContent
            ArrayList<String> columnAL = bulletinAssemblyUtils.returnByColumn(fileContent, column, 1);
            headerAL.add(columnAL.get(0));
         }
      } catch (Exception e)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Unable to obtain valid headers in addressList.csv.");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "Check line 1, insure contains all members.");
         return new ArrayList<String>(0);
      }
      return headerAL;
   }

   // The Configuration file must be updated with current USPS zip codes regurally
   // The element DATEREMINDER contains a date that is set some weeks in the future.
   // Here we simply check the date, and give a warning if necessary.  After the config
   // file is checked to see if correct, or updated, the DateReminder date stamp must
   // be manually changed.

   // Check a reminder date under elmement DATEREMINDER in the config xlm file
   // If past the date post a message, else silent.
   private void checkDateReminder()
   {
      // Period Example
      // Period p = Period.between(birthday, today);
      // long p2 = ChronoUnit.DAYS.between(birthday, today);
      // internalMsgCtrl.out("You are " + p.getYears() + " years, " + p.getMonths() +
      //                    " months, and " + p.getDays() +
      //                    " days old. (" + p2 + " days total)");

      //LocalDate date = LocalDate.now();
      //String text = date.format(formatter);
      //LocalDate parsedDate = LocalDate.parse(text, formatter);

      // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
      try
      {
         final String datePattern = "MM/dd/yyyy";

         String dateReminderStr = xmlData.get_DateReminder();
         String reminderUpdatedStr = xmlData.get_ReminderUpdated();

         if (verifyDateString(dateReminderStr, reminderUpdatedStr))
         {
            if (dateReminderStr==null) return;
            if (reminderUpdatedStr==null) return;

            LocalDate today = LocalDate.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
            LocalDate parsedReminder = LocalDate.parse(dateReminderStr, dateFormat);
            LocalDate parsedUpdated = LocalDate.parse(reminderUpdatedStr, dateFormat);
            long chronReminder = ChronoUnit.DAYS.between(today, parsedReminder);
            long chronUpdated = ChronoUnit.DAYS.between(parsedUpdated, today);

            String configFilePathName = shared.getConfigFilePathName();
            internalMsgCtrl.err(InternalMsgCtrl.errKey.LineBreak, false, "");
            internalMsgCtrl.out("-- Verify updates to the configuration file. --");
            internalMsgCtrl.out("You asked to be reminded to update it by:  " + dateReminderStr);
            internalMsgCtrl.out("The last update was done on:               " + reminderUpdatedStr);
            if (chronReminder < 0) {
               internalMsgCtrl.out("The reminder date is in the past by:       " + chronReminder + " days.");
            } else {
               internalMsgCtrl.out("An update to the config file is due in:    " + chronReminder + " days.");
            }
            internalMsgCtrl.out("It has been " + chronUpdated + " days since the last update.");
            internalMsgCtrl.err(InternalMsgCtrl.errKey.LineBreak, false, "");

            return;
         }
      }
      catch (Exception excp)
      {
         // It is expected... if we end up here, the date format is incorrect
         String configFilePathName = shared.getConfigFilePathName();
         internalMsgCtrl.err(InternalMsgCtrl.errKey.LineBreak, false, "");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "In file:  " + configFilePathName);
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false, "DATEREMINDER Date format must be: mm/dd/yyyy.");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false, "Error in VerifyCSV.checkDateReminder()");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "This is not a fatal error; but get if fixed.");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "(Perhaps the --verbose option might be helpful.)");
         String errMsg = excp.toString();
         internalMsgCtrl.err(InternalMsgCtrl.errKey.ExceptionMsg, false, errMsg);
         internalMsgCtrl.err(InternalMsgCtrl.errKey.LineBreak, false, "");
         return;
      }

   }

   // Returns true if the either of the strings is
   private boolean verifyDateString (String dateReminderStr,
                                     String reminderUpdateStr)
   {
      char[] chars = dateReminderStr.toCharArray();
      int inx = 0;
      boolean failFlg = false;
      for (; inx <chars.length; inx++)
      {
         char ch = chars[inx];
         if (Character.isDigit(ch)) continue;
         if (ch == '/') continue;;
         failFlg = true;
      }
      if (inx == 10)
      {
         chars = reminderUpdateStr.toCharArray();
         inx = 0;
         for (; inx < chars.length; inx++) {
            char ch = chars[inx];
            if (Character.isDigit(ch)) continue;
            if (ch=='/') continue;
            failFlg = true;
         }
      }
      if ((inx==10) && (failFlg==false)) return false;

      // Else the failFlg was set true in one of the two cases.
      // One of the following failed.

      String msg1 = "One of your date reminder strings has a problem.";
      String msg2 = "Look for one of these four problems:";
      String msg3 = "   Incorrect number of digits: 3 rather than 03 or 21 rather than 2021.";
      String msg4 = "   Spelled out month or day (Sept rather than 09).";
      String msg5 = "   deliminator other than \"/\"";
      String msg6 = "   The format is mm/dd/yyyy: MUST be exactly 10 character in length.";
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg1);
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg2);
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg3);
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg4);
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg5);
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, true, msg6);
      return true;
   }

}

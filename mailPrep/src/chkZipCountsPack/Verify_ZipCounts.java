package chkZipCountsPack;

import libPack.ExcelUtils;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;

public class Verify_ZipCounts
{
   public Verify_ZipCounts(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();
      excelUtils = shared.getExcelUtils();
   }
   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;
   ExcelUtils excelUtils = null;

   public boolean verifyZipCountCSV_Main(String[] ZC_zonesAry, ArrayList<String> zipCountsContentAL)
   {
       String  colStr = "";
       for (int inx = 0; inx < ZC_zonesAry.length; inx++)
      {
         int textCnt = 0;
         colStr = ZC_zonesAry[inx];
         ArrayList<String> colZipAL = excelUtils.CSVToColumn(colStr, zipCountsContentAL);
         // ensure all values below the first 5-digit integer is an integer (a zip code)
         String str = "";
         int cntEmpty = 0;
         try
         {
            for (int cnt = 0; cnt < colZipAL.size(); cnt++)
            {
               str = colZipAL.get(cnt);
               // I don't care what the value is, nor if it is 5 characters long.
               // I just want to know it parses to an integer.
               // There are several empty cells and 2 text strings above the zip codes.
               if (str.isEmpty())
               {
                  // There are a LOT of empty cells.  Only a few at the top
                  // but LOTS of empty cells after the last digit.  So, once
                  // we hit 20 cells... an arbitrarily large enough number,
                  // be break.
                  if (cntEmpty++ == 20)
                     break;
                  continue;
               }
               if (textCnt++ < 2) continue;

               Integer.parseInt(str);
            }
         }
         catch (Exception excp)
         {
            String zipCountsPath = shared.getZipCountsFilePathName();
            // Something other than an integer zip was found
            String errStr1 = "In file: " + zipCountsPath + "\n";
            String errStr2 = "              In column " + colStr + ", [" + str + "]:  Is not a zip code";
            String errStr = errStr1 + errStr2;
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, errStr);
            internalMsgCtrl.out("---------------------------------------------------");
            internalMsgCtrl.out("There are a few empty cells in the zip code column.");
            internalMsgCtrl.out("In addition there are 2 cells with text.");
            internalMsgCtrl.out("ONLY TWO cells with text are allowed!");
            internalMsgCtrl.out("---------------------------------------------------");
            internalMsgCtrl.out("You may have a configuration problem.  Check <ZIPCOUNTS> values.");
            internalMsgCtrl.out("---------------------------------------------------");
            return false;
         }
      }
      return true;
   }
}

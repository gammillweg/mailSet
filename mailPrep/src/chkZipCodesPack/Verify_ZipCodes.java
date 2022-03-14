package chkZipCodesPack;

import configPack.XMLData;
import libPack.InternalMsgCtrl;
import libPack.MailPrepUtils;
import libPack.Shared;

import java.util.ArrayList;

public class Verify_ZipCodes
{
    public Verify_ZipCodes(Shared shared)
   {
      this.shared = shared;
      bulletinAssemblyUtils = new MailPrepUtils(shared);
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }
   Shared shared = null;
   XMLData xmlData = null;
   MailPrepUtils bulletinAssemblyUtils = null;
   InternalMsgCtrl internalMsgCtrl = null;

   public boolean verifyZipCodesCSV_Main(ArrayList<String> columnContent)
   {
      // I expect the first column to be a heading, so skip it.
      // the rest must be empty strings or 5-digit Integers
      for (int inx = 1; inx < columnContent.size(); inx++)
      {
         String str = columnContent.get(inx);
         if (str.isEmpty()) continue;

         // Must contain 5 characters if a 5-digit zip code
         // I cut this... Excel might drop the leading zero... making a zip 03842 on 4-digits
//         if (str.length() != 5)
//         {
//            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Check line " +
//                               (inx+1) + ", " + str + ", is not 5 digits.");
//            return false;
//         }


         // Should be zip 5-digit Integers
         try
         {
            Integer.parseInt(str);
         }
         catch (Exception excp)
         {
            // Something other than an Integer Zip code
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Check line " +
                               (inx+1) + ", " + str +" is not a zip code.");
            return false;
         }
      }

      return true;
   }
}

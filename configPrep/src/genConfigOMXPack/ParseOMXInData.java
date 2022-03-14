package genConfigOMXPack;

import libPack.InternalFatalError;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;

public class ParseOMXInData
{
   public ParseOMXInData(Shared shared)
   {
      internalMsgCtrl = shared.getInternalMsgCtrl();
      lineBreak = shared.getLineBreak();
   }
   // Denver OMX str length is greater than 500 so the arbitrary 300 is a reasonable check
   InternalMsgCtrl internalMsgCtrl = null;
   final int MinStrLen = 300;
   String lineBreak = "";

   public String parseInDataAL(ArrayList<String> strAL) throws InternalFatalError
   {
      try
      {
         StringBuilder sb = new StringBuilder();

         // We know that strAry is a single member, one long string at least MinStrLen long
         String strg = strAL.get(0);
         String[] strAry = strg.split(", ");
         sb.append("<OMX>" + lineBreak);
         for (String str : strAry)
         {
            sb.append("  <omx>" + str + "</omx>" + lineBreak);
         }
         sb.append("</OMX>" + lineBreak);

         String outStr = sb.toString();
         return outStr;
      }
      catch (Exception exc)
      {
         throw new InternalFatalError();
      }
   }

   public boolean verifyOMXInData(ArrayList<String> strAL)
   {
      // There should only be one member in strAL... one long string
      String str = strAL.get(0);
      if (strAL.size() == 1)
      {
         if (str.length() > MinStrLen)
         {
            // I could be real though and parse to make sure only digits, dashes, commas and spaces.
            // But I think that overkill.
            return true;
         }
         return false;
      }
      else
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "You very likely copied incorrectly.");
         internalMsgCtrl.out("Copy only the center column with 3 digit zip codes, not the either of the outer columns.");
         return false;
      }
   }
}

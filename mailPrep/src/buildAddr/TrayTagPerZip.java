package buildAddr;

import configPack.XMLData;
import libPack.Addr;
import libPack.InternalMsgCtrl;
import libPack.MailPrepUtils;
import libPack.Shared;

import java.util.Arrays;

/*********************************************************
 * 190218
 * The C# version of this class used Linq to parse. Linq is supposed a
 * fast parser.  Java does not support Linq.  Java supports Lambda expressions.
 * But, I read, Lambda is not necessarily any faster than normal loops.
 * On looking at Lambda I decided I didn't care to learn another method of
 * coding just now.  On trying to get the C# code to work with loops I
 * came up with a more efficient way to run the loops.  On trying to convert
 * my loops to this more effecting way... my loops did not work well.
 * SO... I have killed the entire thing and have rewritten the entire class.
 * But, in the end, I think my new way may be even better.
 * The loops counts and jump through sets of zips to avoid multiple tests
 * on previously process code.
 * ********************************************************/

public class TrayTagPerZip
{
   public TrayTagPerZip(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();
      xmlData = shared.getXMLData ();
      genLabelsUtils = shared.getGenLabelsUtils();
   }

   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;
   MailPrepUtils genLabelsUtils = null;
   XMLData xmlData = null;
   int minTrayTagCnt = -1;

   // Fills the Tray member of each Addr member in the addrList
   // Return is a modified input list.
   public void trayTagPerZip_Main(Addr[] addrZipAry)
   {
      String minTrayTagCntStr = xmlData.get_MinTrayCnt();
      this.minTrayTagCnt = Integer.valueOf(minTrayTagCntStr);

      // addrAL is sorted.  To that I add d5Ary, d3Ary and trayTagAry
      // All 4 are synchronized... in the SAME ORDER

      // These two simply fill an array with the int value of each zip
      // 5d with 5 digits and 3d with the first 3 digits for each zip
      // in addrZipAry
      int[] d5Ary = setupD5Ary(addrZipAry);
      int[] d3Ary = setupD3Ary(addrZipAry);

      // trayTagAry and bundleAry are synchronized with each other and addrAL.
      // trayTagAry:  Ends up filled with trayTag designations like 800, DSF, ADC etc
      //           for each zip in addrAL.

      // fillDefaults(), fills trayTagAry with "MXD".  This string is overwritten by
      // higher priority stings (as in 800 OMX or SCF)... MXD is never check for.
      // MXD's are the members never found as a higher priority and not overwritten.
      String[]trayTagAry = fillDefaults(addrZipAry);

      // bundleAry: Ends up filled with USPS Bundle designations like D3, D5, OMX
      //            for each zip in addrAL.  Does little for MXD, OMX, SCF, and ADC
      //            but is necessary for 3D's and 5D's.  Saves later rgx comparisons.

      // read comment just above... bundleAry ends filled with "MXD"
      String[]bundleAry = fillDefaults(addrZipAry);

      // Reverse the USPS Priority order (see note on "How it works")
      String[]tmpAry = xmlData.get_priority();
      String[]uspsPriority = new String[tmpAry.length];
      int index = 0;
      for (int inx = tmpAry.length - 1; inx > -1; inx--)
      {
         uspsPriority[inx] = tmpAry[index++];
      }

      // The return is modifed trayTagAry
      assignTrayTags(uspsPriority, d5Ary, d3Ary, trayTagAry, bundleAry);

      //The return is a modifed addrAL
      // Assign the trayTags to addr
      for (int inx = 0; inx < addrZipAry.length; inx++)
      {
         Addr addr = addrZipAry[inx];
         addr.trayTag = trayTagAry[inx];
         addr.bundle = bundleAry[inx];
         //addr.trayTag + ", zone:  " + addr.zone);
      }
//      for (Addr addr : addrZipAry)
//      {
//         System.out.println("zip: " + addr.zip + ", tray: " + addr.trayTag);
//      }
   }

   private void assignTrayTags(String[]uspsPriority, int[]d5Ary, int[]d3Ary,
                               String[]trayTagAry, String[] bundleAry)
   {
      String xmlStr = "";
      for (String priority:uspsPriority)
      {
         // priority was reversed... lowest to highest
         switch (priority)
         {
            // Nothing needs to be done for MXD.
            // trayTagAry and bundleAry were filled with MXD.  Those will be overwritten by
            // higher priorities as we get to them.
            case "MXD":
               break;

            case "OMX":
               doOMX (d3Ary, trayTagAry, bundleAry, priority);
               break;

            case "DADC":
               xmlStr = xmlData.get_DADC ();
               doCombined (d3Ary, trayTagAry, bundleAry, priority, xmlStr);
               //debug3dtt(d3Ary, trayTagAry);
               break;

            case "SCF":
               xmlStr = xmlData.get_DEN_SCF ();
               doCombined (d3Ary, trayTagAry, bundleAry, priority, xmlStr);
               //debug3dtt(d3Ary, trayTagAry);

               // There well never be sufficient GJ_SCF to fill a trayTag
               // this here for "completeness
               xmlStr = xmlData.get_GJ_SCF ();
               doCombined (d3Ary, trayTagAry, bundleAry, "GJ_SCF", xmlStr);
               //debug3dtt(d3Ary, trayTagAry);
               break;

            case "D3":
               doDigits (d3Ary, trayTagAry, bundleAry, priority);
               break;

            case "D5":
               doDigits (d5Ary, trayTagAry, bundleAry, priority);
               break;

         }
      }
      // for (int inx = 0; inx < trayTagAry.endIndex; inx++)
      // {
      //    internalMsgCtrl.out(inx + ": " + trayTagAry[inx]);
      // }
   }

//   private void debug3dtt(int[] d3Ary, String[] trayTagAry)
//   {
//      int cnt = 0;
//      for (int inx = 0; inx < trayTagAry.length; inx++)
//      {
//         System.out.println("db3dtt: " + cnt + ")  " + d3Ary[inx] + ", " + trayTagAry[inx]);
//         cnt++;
//      }
//   }


   // processes both 3D and 5D
   // The return is a modified trayTagAry
   private void doDigits (int[]dAry, String[]trayTagAry, String[] bundleAry, String priority)
   {
      int startIndex = 0;
      int cnt = 0;
      int index = 0;
      boolean startFlg = true;
      String dStr = "";

      do
      {
         int val = dAry[index];

         for (int inx = index; inx < dAry.length; inx++)
         {
            if (dAry[inx] == val)
            {
               cnt = counter (val, dAry, inx);
               if (cnt >= minTrayTagCnt)
               {
                  if (priority.equals("D3"))
                  {
                     dStr = String.format ("%3d", val);
                  }
                  else if (priority.equals("D5"))
                  {
                     dStr = String.format ("%5d", val);
                  }
                  else
                  {
                     return;
                  }
                  if (startFlg)
                  {
                     startIndex = inx;
                     startFlg = false;
                  }
               }
            }
            break;
         }
         index += cnt;

         if (cnt >= minTrayTagCnt)
         {
            for (int inx = startIndex; inx < cnt+startIndex; inx++)
            {
               trayTagAry[inx] = dStr;
               bundleAry[inx] = priority;
            }
            // for (int inx = 0; inx < trayTagAry.endIndex; inx++)
            // {
            //    internalMsgCtrl.out (inx + ": " + trayTagAry[inx]);
            // }
            index = cnt+startIndex;
            startFlg = true;
         }
         cnt = 0;
      } while (index < dAry.length);
   }

   // processes both DADC and DEN_SCF and GJ_SCF
   // The return is a modified trayTagAry
   private void doCombined (int[]d3Ary, String[]trayTagAry, String[] bundleAry, String priority, String xmlStr)
   {

      /*******************************************
       * Side note 190219 at the time, both DADC and DEN_SCF are 800-812
       * So, all of DACD is immediately over written by SCF
       * And, GJ_SCF will NEVER meet the minimum for a trayTag, so is never written.
       * Even so... in the interest of completeness... all are processed.
       * ****************************************/

      // The data of xmlStr    looks like:  "800-812"
      //             expandAry looks like:  {800, 801, 802... 812}
      int[] expandAry = genLabelsUtils.expandRange (xmlStr);
      int startIndex = 0;
      int length = 0;
      int cnt = 0;
      boolean startFlg = true;
      for (int val:expandAry)
      {
         for (int inx = length; inx < d3Ary.length; inx++)
         {
            if (d3Ary[inx] == val)
            {
               cnt = counter (val, d3Ary, inx);
               if (startFlg)
               {
                  startIndex = inx;
                  startFlg = false;
               }
               break;
            }
         }
         // Accumulate the counter for all members of the DADC range
         length += cnt;
         //internalMsgCtrl.out("Length:  " + endIndex + ", cnt:  " + cnt);
         cnt = 0;
      }

      if (length >= minTrayTagCnt)
      {
         for (int inx = startIndex; inx < length + 3; inx++)
         {
            trayTagAry[inx] = priority;
            bundleAry[inx] = priority;
         }
         //for (int inx = 0; inx < trayTagAry.endIndex; inx++) {
         //internalMsgCtrl.out(inx + ": " + trayTagAry[inx]);
         //}
      }
   }

   // Count the number of positive comparisons
   // compareAry is expected to be a sorted ary
   // startIndex is expected to be the startIndex of the first active int
   private int counter (int active, int[]compareAry, int startIndex)
   {
      int count = 0;
      for (int inx = startIndex; inx < compareAry.length; inx++)
      {
         if (active == compareAry[inx])
         {
            count++;
            // String str =
            //    String.format ("count %d, active %d, startIndex %d, compare %d",
            //                   count, active, startIndex, compareAry[inx]);
            // internalMsgCtrl.out (str);
         }
         else
            break;
      }
      return count;
   }

   // The return is a modified trayTagAry
   // OMX does not have a minimum per trayTag
   private void doOMX (int[]d3Ary, String[]trayTagAry, String[] bundleAry, String priority)
   {
      int min3D = d3Ary[0] - 1;
      int max3D = d3Ary[d3Ary.length - 1] + 1;

      // The data of omxAry looks like:  {"090-099","375", "380-383", ... }
      String[]strAry = xmlData.get_omx ();
      for (String str:strAry)
      {
         // Side comment:  at this date 190218, our min3d is really small (305)
         // so the min check is not all that helpful.
         int[] ary = genLabelsUtils.expandRange (str);
         for (int val:ary)
         {
            // check of OMX "out of range" zip codes
            if ((val > min3D) && (val < max3D))
            {
               for (int inx = 0; inx < d3Ary.length; inx++)
               {
                  if (d3Ary[inx] == val)
                  {
                     trayTagAry[inx] = priority;
                     bundleAry[inx] = priority;
                  }
               }
            }
         }
      }
   }

   // Convert the string zip to an int zip and sort the array
   // (A post though... addrAL is sorted... I had forgotten this.
   // But, I leave this Integer work as is, now that I remember.)
   private int[] setupD5Ary (Addr[] addrZipAry)
   {
      int[] d5Ary = new int[addrZipAry.length];
      for (int inx = 0; inx < d5Ary.length; inx++)
      {
         int d5 = Integer.valueOf (addrZipAry[inx].zip);
         d5Ary[inx] = d5;
      }
      Arrays.sort (d5Ary);
      return d5Ary;
   }

   // Convert the string zip to an int zip and sort the array
   private int[] setupD3Ary (Addr[] addrZipAry)
   {
      int[] d3Ary = new int[addrZipAry.length];
      for (int inx = 0; inx < d3Ary.length; inx++)
      {
         String d3zip = addrZipAry[inx].zip.substring (0, 3);
         int d3 = Integer.valueOf (d3zip);
         d3Ary[inx] = d3;
      }
      Arrays.sort (d3Ary);
      return d3Ary;
   }

   // Fills both trayTagAry and bundleAry with a default value of "MXD"
   // This value will be overwritten by higher priority trayTags.
   private String[] fillDefaults (Addr[] addrZipAry)
   {
      String[]defaultAry = new String[addrZipAry.length];
      for (int inx = 0; inx < addrZipAry.length; inx++)
      {
         defaultAry[inx] = "MXD";
      }
      return defaultAry;
   }
}

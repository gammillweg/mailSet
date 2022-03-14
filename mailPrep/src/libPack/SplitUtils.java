package libPack;

import configPack.XMLData;
import libPack.ClassesPack.Segment;
import libPack.ClassesPack.Tray;

import java.util.Arrays;
import java.util.HashMap;

public class SplitUtils
{
   public SplitUtils(Shared shared)
   {
      this.shared = shared;
      xmlData = shared.getXMLData();
      internalMsgCtrl = shared.getInternalMsgCtrl();
      excelUtils = shared.getExcelUtils();

      labelsAcross = shared.getLabelsAcross();
      //------------------------------------------------
      // Class fields and done here once else would be
      // called for each line of the AddressList
      
      // Get the configured number of labels across a label sheet
      //that will be printed

      setGoals(labelsAcross);
      columnIntValuesAry = getHeaderColumnIntValues();
      //------------------------------------------------
   }

   private Shared shared = null;
   private XMLData xmlData = null;
   InternalMsgCtrl internalMsgCtrl = null;

   private final int labelsAcross;
   private int[] goals = null;
   private int[] columnIntValuesAry = null;

   private ExcelUtils excelUtils = null;
   
   //-----------------------------------------------------------------------
   // These two declarations work together to define the array goals
   // 4*3=12 the starting goal
   // 5*3=15
   // 6*3=18
   // of GoalPasses == 4 then on up
   // 7*3=21
   // Originally I did have GoalPasses==4 and did get segments of 21 members; but
   // decided 21 was to many labels per package.
   // Also -- one could start with RowsGoal == 3
   // the goals start with 3*3=9, 4*3=12, 5*3=15 6*3=18 (for GoalPasses == 4)
   // (I like 4/3)
   // larger GoalPasses >> more chances,
   // smaller RowsGoal >> smaller packages

   final int GoalPasses = 4;
   final int RowsGoal = 3;
   //-----------------------------------------------------------------------

   
   // So as to avoid any side effect caused by modifying addrZipAry
   // addrZipAry is cloned into addrBundleAry

   // addrZipAry was sorted in Addr.zip order.
   // Now I sort in bundle order (which does not change the zip order).
   // Thus addrBundleAry is sorted in Addr.bundle order and within each bundle
   // is sorted in Addr.zip order.  (I do not have to sort by zip again.)
   public Addr[] createAddrBundleAry(Shared shared, Addr[] addrZipAry) throws InternalFatalError
   {
       try
       {
         Addr[] addrBundleAry = new Addr[addrZipAry.length];
         for (int inx = 0; inx < addrZipAry.length; inx++) {
            addrBundleAry[inx] = addrZipAry[inx].clone();
         }
          ClassesPack classesPack = shared.getClassesPack();
          ClassesPack.AddrBundleComparator addrBundleComparator =
                  classesPack.instantiateAddrBundleComparator();
         //Arrays.sort(addrBundleAry, new AddrBundleComparator());
          Arrays.sort(addrBundleAry, addrBundleComparator);

         return addrBundleAry;
      }
       catch (Exception excp)
       {
          internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Failed in SplitUtils.createAddrBundleAddr()");
          internalMsgCtrl.err(InternalMsgCtrl.errKey.ExceptionMsg, false, excp.toString());
          throw new InternalFatalError();
       }
   }
   
   //-------------------------------------------------------------------------

   // Called from the constructor
   // Return is Class member goals
   public void setGoals(int across)
   {
      // Goals example for across == 3, multiples of 3:    12, 15, 18
      goals = new int[GoalPasses];
      int step = RowsGoal;
      for (int inx = 0; inx < GoalPasses; inx++)
      {
         goals[inx] = step++ * across;
      }
   }

   // return is an int array: [0]: the number of cuts and [1]: a goal of the number
   // of labels per segment
   public int[] numberOfCuts(Tray tray)
   {
      // [0]: number of cuts
      // [1]: goal of the number of labels per segment
      int[] rtn = new int[2];

      int goal = -1;
      
      // The easiest... all in one segment
      // I stop looking at one less than max sized segment
      for (int inx = 0; inx < GoalPasses-1; inx++)
      {
         goal = goals[inx];
         if (tray.len < goal)
         {
            // Then all of this trayTag will fit in one segment
            rtn[0] = 1;
            rtn[1] = goal;
            return rtn;
         }
      }

      int cut = 0;
      int rem = 0;
      int minRem = 999; // just a big number to compare against
      int[] brace = null;
      HashMap<Integer, int[]> cutGoalMap = new HashMap<Integer, int[]>(GoalPasses);

      for (int inx = 0; inx < GoalPasses; inx++)
      {
         goal = goals[inx];
         if (goal > tray.len) break;

         cut = tray.len / goal;
         rem = tray.len % goal;

         //-----------------------------
         // store the cut with the goal in a map keyed by the remainder
         
         brace = new int[2];
         brace[0] = cut;
         brace[1] = goal;
         if (cutGoalMap.containsKey(rem) == false)
         {
            cutGoalMap.put(rem, brace);
         }

         // find the minimum remainer
         minRem = Math.min(minRem, rem);
         //-----------------------------

         // Try for perfect (%==0)
         if (rem == 0)
         {
            rtn[0] = cut;
            rtn[1] = goal;
            return rtn;
         }

         // So perfect was not obtainable
         // try for the next best thing... a multiple of across
         if (rem ==labelsAcross)
         {
            rtn[0] = cut;
            rtn[1] = goal;
            return rtn;
         }

         // So the next best thing is not obtainable
         // try for less than across
         if (rem < labelsAcross)
         {
            rtn[0] = cut;
            rtn[1] = goal;
            return rtn;
         }
      }

      // we did not find any close cut... so go with the minimum remainder
      brace = cutGoalMap.get(minRem);
      goal = brace[1];
      cut = brace[0];
      
      rtn[0] = cut;
      rtn[1] = goal;
      return rtn;
   }

   // Build a spread sheet header string from configured data
   // Return is class field headerCSVStr
   public String getHeaderCSVStr()
   {
      String headerCSVStr = "";
      String[] addressListHeadings = xmlData.getAddressListHeadings();

      for (String str : addressListHeadings)
      {
         headerCSVStr += str + ",";
      }

      // Remove the trailing ","
      headerCSVStr = headerCSVStr.substring(0, (headerCSVStr.length() - 1));
      
      return headerCSVStr;
   }

   // Get the integer value of each column per headers as defined in the
   // configuration file.  Note that this code expects Elements <ADDRESSLIST>
   // (which contains the column string), to be in the same order as Element
   // <MAILMERGEHEADER> (which contains the column header string).
   public int[] getHeaderColumnIntValues()
   {
      String[] addressListColumnsAry = xmlData.getAddressListColumns();
      int[] columnIntValuesAry  = new int[addressListColumnsAry.length];
      int inx = 0;
      for (String str : addressListColumnsAry)
      {
        columnIntValuesAry[inx++] = excelUtils.excelColumnToInt(str);
      }
      return columnIntValuesAry;
   }

   public String buildBulletinLableCSVStr(Addr addr)
   {
      String[] csvAry = addr.csv.split(",");
      String csvStr = "";
      for (int inx : columnIntValuesAry)
      {
         // Excel columns count from 1; the array counts from 0
         int index = inx -1;
         csvStr += csvAry[index] + ",";
      }

      // cut the trminating ","
      csvStr = csvStr.substring(0, (csvStr.length()-1));
      return csvStr;
   }

   //------------------------------------------------------------------------
   private void debug_segmentAry(Segment[] segmentAry)
   {
      int seqAryLen = segmentAry.length;
      for (Segment segment : segmentAry)
      {
         String dbStr = "";
         dbStr = Integer.toString(segment.segmentIndex);
         dbStr += ") ";
         dbStr += segment.trayTag;
         dbStr += ", start: ";
         dbStr += Integer.toString(segment.startIndex);
         dbStr += ", end: :";
         int endIndex = (segment.startIndex + segment.len) - 1;
         dbStr += Integer.toString(endIndex);
         dbStr += ", len: ";
         dbStr += Integer.toString(segment.len);
         dbStr += ", rem: ";
         int rem = segment.len % 3;
         dbStr += Integer.toString(rem);

         dbStr += ", [renewalAry: ";
         if (segment.renewalAry != null)
         {
            for (int index : segment.renewalAry)
            {
               dbStr += Integer.toString(index);
               dbStr += ", ";
            }
         }
         dbStr += "], ";

         dbStr += "[zipCntAry: ";
         if (segment.zipCntAry != null)
         {
            for (int index : segment.zipCntAry)
            {
               dbStr += Integer.toString(index);
               dbStr += ", ";
            }
         }
         dbStr += "]";
         
         System.out.println(dbStr);
      }
   }
}

package assembly;

import libPack.*;
import libPack.InternalMsgCtrl.errKey;

import java.util.ArrayList;

// The return is an ArrayList of CSV strings with segment labels
// blended into the current labels such that the printed label
// list may be sliced with a paper cutter at each segment.
// For example:
// If there are 3 mailing labels in the segment, no segment label is
// added to the end of the segment (assuming across == 3).  If there
// are 4 labels in the segment that the last row of labels will
// contain only 1 current mailing label and two blank labels.  These
// will be used for segment labels.  Two segment lables will be added
// to the return ArrayList before preceding to the next segment.
// All remaining segments, not used to fill rows across (as needed)
// will be added at the end of the ArrayList.

public class BlendCurrentToAL
{
   public BlendCurrentToAL(Shared shared, SplitUtils splitUtils)
   {
      this.shared = shared;
      this.splitUtils = splitUtils;
      internalMsgCtrl = shared.getInternalMsgCtrl();

      // The number of labels in in row (is a configured value)
      across = shared.getLabelsAcross();

      // getHeaderColumnIntValues gives use columns in an array
      // I don't need the columns, I need the number of columns, hence get length.
      int[] headings = splitUtils.getHeaderColumnIntValues();
      columns = headings.length;
   }

   private Shared shared = null;
   private SplitUtils splitUtils = null;
   private InternalMsgCtrl internalMsgCtrl = null;

   private int across = 0;
   private int columns = 0;

   public ArrayList<String> blendCurrentToAL_Main(
         ClassesPack.Segment[] segmentAry,
         ArrayList<String> segmentLabelAL,
         Addr[] currentBundleAry)  throws InternalFatalError
   {
         try
         {
            int len = segmentLabelAL.size() + currentBundleAry.length;
            ArrayList<String> currentListCSVAL = new ArrayList<String>(len);

            String headerCSVStr = splitUtils.getHeaderCSVStr();
            currentListCSVAL.add(headerCSVStr);

            // processs on each segment
            for (ClassesPack.Segment segment : segmentAry)
            {
               if (segmentLabelAL.size() > 0)
               {
                  int mailingLabelCnt = cntMailingLabels(segment);

                  if ((mailingLabelCnt % across)==0)
                  {
                     // The slice is even
                     ArrayList<String> al = slice(segment, currentBundleAry, mailingLabelCnt);
                     currentListCSVAL.addAll(al);
                  }
                  else
                     {
                     // The slice is uneven and need to be filled
                     ArrayList<String> al = slice(segment, currentBundleAry, mailingLabelCnt);
                     al = fillWithSegmentLabels(al, segmentLabelAL);
                     currentListCSVAL.addAll(al);
                  }
               }
               else
                  {
                  // The segment labels have been used up.  Flush out the mailing labels
                  int mailingLabelCnt = cntMailingLabels(segment);
                  ArrayList<String> al = slice(segment, currentBundleAry, mailingLabelCnt);
                  currentListCSVAL.addAll(al);
               }
            }
            // if all segments used up (must have been to get here)
            // the ALL the current mailing lables should also have been used up.

            if (segmentLabelAL.size() > 0)
            {
               // The mailing labels have all been used up.  Flust out the segment labels
               for (String str : segmentLabelAL)
               {
                  //String segmentCSVStr = buildSegementCSVStr(ary);
                  currentListCSVAL.add(str);
               }
            }

            return currentListCSVAL;
         }
         catch (Exception excp)
         {

               internalMsgCtrl.err(errKey.FatalError, false, "Failed in class BlendCurrentToAL");
               String errMsg = excp.toString();
               internalMsgCtrl.err(errKey.ExceptionMsg, false, errMsg);
               throw new InternalFatalError();
               //return null;
         }
   }

   // An uneven (across) number of labels must be filled out.
   // The return is a filled ArrayList "al"
   // There is an indirect return of segmentLabelAL's, as used members are removed from the list
   private ArrayList<String> fillWithSegmentLabels(
      ArrayList<String> al,  // contains the reformatted csv string for current mailing labels
      ArrayList<String> segmentLabelAL) // contains fill labels
   {
      int labelsNeeded = across - (al.size() % across);
      if (labelsNeeded > segmentLabelAL.size())
      {
         // It may be that there insufficient labels left to fill the need
         labelsNeeded = segmentLabelAL.size();
      }

      for (int inx = 0; inx < labelsNeeded; inx++)
      {
         String segmentCSVStr = segmentLabelAL.get(inx);
         al.add(segmentCSVStr);
      }

      // Remove from segmentLabelAL the segment labels just used
      // As labels are used from the top to the bottom, must remove them from the top
      // but do not want to remove any above until those below have been removed.
      // Thus we must count backwards, from the lower to the top
      for (int inx = labelsNeeded; inx > 0; inx--)
      {
         // -1 to account for the 0 based ArrayList
         // (If 1 to be removed, the the first index would be "1"; but it is 0 to be removed
         segmentLabelAL.remove(inx-1);
      }
      
      return al;
   }

   // A member of segmentLabelAL is a array of lines to build the segment label
   // the array need be converted into a CSV string suitable for our MS Word template
   // (It must look like address csv strings)
   private String buildSegementCSVStr(String[] ary)
   {
      String str = "";
      for (int inx = 0; inx < ary.length; inx++)
      {
         str += (ary[inx] == null) ? "," : ary[inx] + ",";
      }

      // need to fill out the number of columns with empty columns
      // the number of columns in the headers
      int cnt = (columns - ary.length) - 1;
      if (cnt > 0)
      {
         for (int inx = 0; inx < cnt; inx++) { str += ","; }
      }
      return str;
   }

   // Copy out of CurrentBundleAry those labels in the segment
   // And store a reformated Addr.csv String with only the heading
   // needed for a mailing label
   private ArrayList<String> slice(
         ClassesPack.Segment segment,
         Addr[] currentBundleAry,
         int mailingLabelCnt)
   {
      // an ArrayList for the return
      ArrayList<String> al = new ArrayList<String>(segment.len);

      for (int inx = segment.startIndex; inx < (segment.startIndex + segment.len); inx++)
      {
         // there are NO zero length segments
         
         // There could be null members in the array, add renewals are not in the array
         if (currentBundleAry[inx] == null) continue;

         String bulletinLabelCSVStr = splitUtils.buildBulletinLableCSVStr(currentBundleAry[inx]);
         al.add(bulletinLabelCSVStr);
      }
      return al;
   }

   private int cntMailingLabels(ClassesPack.Segment segment)
   {
      // Data in renewalAry is indices to Addr in addrBundleAry, one per renewal
      int renewalCnt = (segment.renewalAry == null) ? 0 : segment.renewalAry.length;

      // segment.len is the number of zip codes in the segment, including renewal
      return (segment.len - renewalCnt);
   }
}

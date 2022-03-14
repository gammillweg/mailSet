package assembly;

import libPack.*;
import libPack.ClassesPack.Brace;
import libPack.ClassesPack.Segment;
import libPack.ClassesPack.Tray;

import java.util.ArrayList;
import java.util.Arrays;

// Complete the filling of segmentAry:  further spit as needed (primarily 5D)
// Fill the Segment.renewalAry: find addrBundleAry members of each segment
// Fill the Segment.zipCntAry: break Tray.zipCntAry into by segment.
// (In the end all members of Segment are filled EXCEPT:  segmentCSVStr)
public class FillSegmentAry
{
   public FillSegmentAry(Shared shared, SplitUtils split)
   {
      this.shared = shared;
      this.split = split;
      this.classesPack = shared.getClassesPack();

      checkRenewal = new CheckRenewal(shared);
   }
   private Shared shared = null;
   private SplitUtils split = null;
   private ClassesPack classesPack = null;
   private CheckRenewal checkRenewal = null;

   public Segment[] fillSegments(ArrayList<Tray> trayAL, Addr[] addrBundleAry)
   {
      // Count the number of segments and allocate the sequenceAry
      int segments = segmentCnt(trayAL);
      Segment[] segmentAry = new Segment[segments];
         
      // Return is filled segmentAry
      initSegmentAry(trayAL, segmentAry);

      // Find renewals in each segment
      renewalPerSegment(trayAL, segmentAry, addrBundleAry);
      
      return segmentAry;
   }

   // Fill Segment.renewalAry
   // The return is a modified segmentAry
   private void renewalPerSegment(ArrayList<Tray> trayAL, Segment[] segmentAry,
                                  Addr[] addrBundleAry)
   {
      // find the segments of this tray
      int segmentIndex = 0;
      for (Tray tray : trayAL)
      {
         int trayStartIndex = tray.startIndex;
         int trayLen = tray.len;
         int trayEnd = (trayStartIndex + trayLen) - 1;

         int segmentEnd = -1;
         do
         {
            Segment segment = segmentAry[segmentIndex];
            int segmentStartIndex = segment.startIndex;
            int segmentLen = segment.len;
            segmentEnd = (segmentStartIndex + segmentLen) - 1;
            
            findRenewals(segment, addrBundleAry);

            segmentIndex++;
         } while (segmentEnd < trayEnd);
      }
   }

   // Run through the segments and assigning the tray segments to each label
   // These are sequence labels NOT bulletin address labels
   // The return is filled segmentAry
   private void initSegmentAry(ArrayList<Tray> trayAL, Segment[] segmentAry)
   {
      int segmentIndex = 0;
      for (Tray tray : trayAL)
      {
         ArrayList<Brace> braceAL = tray.bracedAL;
         // A (braceAL.size() > 1) has been properly subdivided into segments
         // However, ( == 1) may need to be further segmented for packaging
         if (braceAL.size() == 1)
         {
            segmentIndex = singleBrace(tray,segmentIndex, segmentAry);
            continue;
         }

         // Braces greater than 1 are all properly segmented
         segmentIndex = mutipleBraces(tray, segmentIndex, segmentAry);
      }
   }

   // A braceAL.size() > 1 has been properly subdivided into segments.
   // The return is the incremented segmentIndex and a modified segmentAry.
   private int mutipleBraces(Tray tray, int segmentIndex, Segment[] segmentAry)
   {
      ArrayList<Brace> bracedAL = tray.bracedAL;
      int[] zipCntAry = tray.zipCntAry;

      // Total Brace segment issues via zipCntAry.
      int startIndex = tray.startIndex;
      for (Brace brace : bracedAL)
      {
         int len = 0;
         int inx = 0;
         for (inx = brace.startIndex; inx < brace.endIndex; inx++)
         {
            len += zipCntAry[inx];            
         }
         len += zipCntAry[inx];

         segmentAry[segmentIndex] = classesPack.instantiateSegment();
         segmentAry[segmentIndex].segmentIndex = segmentIndex;
         segmentAry[segmentIndex].trayTag = tray.trayTag;
         segmentAry[segmentIndex].startIndex = startIndex;
         segmentAry[segmentIndex].len = len;

         // fill the segments zip count array
         segmentAry[segmentIndex].zipCntAry = Arrays.copyOfRange(zipCntAry, brace.startIndex, (inx+1));
         
         // segmentAry[segmentIndex].renewalAL will be filled later
         startIndex += len;
         segmentIndex++;
      }
      return segmentIndex;
   }

   // A braceAL.size() == 1 may need to be further segmented for packaging.
   // The return is the incremented segmentIndex and a modified segmentAry.
   private int singleBrace(Tray tray, int segmentIndex, Segment[] segmentAry)
   {
      int[] zipCntAry = tray.zipCntAry;
      int[] cutsAry = split.numberOfCuts(tray);
      if (cutsAry[0] > 1)
      {
         // This segment needs to be broken. Do not be concerned with breaking
         // a unit zip code.
         int len = tray.len / cutsAry[0];
         int rem = tray.len % cutsAry[0];

         int startIndex = tray.startIndex;
         for (int inx = 0; inx < cutsAry[0]; inx++)
         {
            segmentAry[segmentIndex] = classesPack.instantiateSegment();
            segmentAry[segmentIndex].segmentIndex = segmentIndex;
            segmentAry[segmentIndex].trayTag = tray.trayTag;
            segmentAry[segmentIndex].startIndex = startIndex;
            segmentAry[segmentIndex].len = len;

            // fill the segments zip count array
            segmentAry[segmentIndex].initZipCntAry(1);
            segmentAry[segmentIndex].zipCntAry[0] = len;
            
            // segmentAry[segmentIndex].renewalAL will be filled later
            startIndex += len;
            segmentIndex++;
         }
         // Add the remainder to the last member
         segmentAry[segmentIndex - 1].len += rem;
         // fill the segments zip count array
         segmentAry[segmentIndex - 1].zipCntAry[0] = segmentAry[segmentIndex - 1].len;
         return segmentIndex;
      }

      // Else we do not need to worry about breaking the tray up any further
      // It is a single segment.
      segmentAry[segmentIndex] = classesPack.instantiateSegment();
      segmentAry[segmentIndex].segmentIndex = segmentIndex;
      segmentAry[segmentIndex].trayTag = tray.trayTag;
      segmentAry[segmentIndex].startIndex = tray.startIndex;
      segmentAry[segmentIndex].len = tray.len;

      // fill the segments zip count array
      segmentAry[segmentIndex].zipCntAry = Arrays.copyOf(zipCntAry, zipCntAry.length);
      
      // segmentAry[segmentIndex].renewalAL will be filled later
      segmentIndex++;
      
      return segmentIndex;
   }

   // Count the number of sements divisions
   private int segmentCnt(ArrayList<Tray> trayAL)
   {
      // Count segements
      int segments   = 0;
      for (Tray tray : trayAL)
      {
         ArrayList<Brace> braceAL = tray.bracedAL;
         // A (braceAL.size() > 1) has been properly subdivided into segments
         // However, ( == 1) may need to be further segmented for packaging
         if (braceAL.size() == 1)
         {
            int[] cutsAry = split.numberOfCuts(tray);
            segments += cutsAry[0];
            continue;
         }
         segments += tray.bracedAL.size();
      }
      return segments;
   }


   // find addr entries that expire this month and stores them in each segment
   // in var segment.renewalAry.
   private void findRenewals(Segment segment, Addr[] addrBundleAry)
   {
      // I choose to make segment.renewalAry an Array rather than an ArrayList
      // thus, I must count first... so there are two passes... the first
      // to count and initalize segment.renewalAL and the second to fill the array.

      int len = 0;
      for (int inx = segment.startIndex; inx < (segment.startIndex + segment.len); inx++)
      {
         Addr addr = addrBundleAry[inx];

         // COMP are trapped as an illegal date; but thay
         // are an acceptable exeption... so, don't check them.
         if (addr.renewal.contains("COMP")) continue;

         if (checkRenewal.isExpiring(addr))
         {
            len++;
         }      
      }
      if (len == 0) return;
      
      segment.initRenewalAry(len);

      int index = 0;
      for (int inx = segment.startIndex; inx < (segment.startIndex + segment.len); inx++)
      {
         Addr addr = addrBundleAry[inx];

         // COMP are trapped as an illegal date; but thay
         // are an acceptable exeption... so, don't check them.
         if (addr.renewal.contains("COMP")) continue;

         if (checkRenewal.isExpiring(addr))
         {
            // Store this startIndex into addrBundleAry, in the trayTag trays renewal list
            segment.renewalAry[index++] = inx;
         }      
      }
   }

}


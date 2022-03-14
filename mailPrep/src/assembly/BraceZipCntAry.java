package assembly;

import libPack.Addr;
import libPack.ClassesPack;
import libPack.ClassesPack.Brace;
import libPack.ClassesPack.Tray;
import libPack.Shared;
import libPack.SplitUtils;

import java.util.ArrayList;

// The return is Tray.segmentAL<Tray> where
public class BraceZipCntAry
{
   public BraceZipCntAry(Shared shared, SplitUtils split)
   {
      this.shared = shared;
      this.split = split;
      this.classesPack = shared.getClassesPack();
   }

   private Shared shared = null;
   private SplitUtils split = null;
   private ClassesPack classesPack = null;

   // The return is tray.bracedAL: an array list of segments consisting
   // of Pairs of Start and End indices with tray.zipCntAry.
   public void segmentZipCntAry(ArrayList<Tray> trayAL, Addr[] addrBundleAry)
   {
      for (Tray tray : trayAL)
      {
         if (tray.trayTag.isEmpty()) continue;

         // The return is bracedAL: an array list of segments
         // consisting of an array of contained unit zips (segments
         //of tray.zipCntAry)
         //-------------------------------------------------------
         // See Main.debugBracedAL()
         //internalMsgCtrl.out("---------------- " + tray.trayTag + " ------------------------");
         //-------------------------------------------------------
         
         ArrayList<Brace> bracedAL = splitZipCntAry(tray, addrBundleAry);

         //==========================================================
         // See Main.debugBracedAL()
         // //------------------  DEBUG --------------------------------
         // internalMsgCtrl.out("");
         // for (Brace brace : bracedAL)
         // {
         //    if (brace.startIndex == brace.endIndex)
         //    {
         //       System.out.print(" * ");
         //    }
         //    else
         //    {
         //       for (int inx = brace.startIndex; inx < brace.endIndex; inx++) {
         //          if (inx==brace.startIndex) {
         //             System.out.print(" s ");
         //             continue;
         //          }
         //          System.out.print(".. ");
         //       }
         //       System.out.print(" e ");
         //    }
         // }
         // internalMsgCtrl.out("");
         // for (Brace brace : bracedAL)
         // {
         //    System.out.print("(" + brace.startIndex + "," + brace.endIndex + ")");
         // }
         // System.out.print("\n\n");
         //==========================================================

         // The return is a modified tray (segmentsCntAry)
      }
   }

   // Parses Tray.zipCntAry seeking to divide the array into divisable by "across"
   // into goal slices.
   // The return is bracedAL: an array list of segments consisting of
   // Pairs of Start and End indices with tray.zipCntAry.
   private ArrayList<Brace> splitZipCntAry(Tray tray, Addr[] addrBundleAry)
   {
      if (tray.zipCntAry == null) return null;
      int[] zipCntAry = tray.zipCntAry;
      //--------------------------------
      // See Main.debugBracedAL()
      // int dbCnt = 0;
      // int dbInx = 0;
      // internalMsgCtrl.out("00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17," +
      //                    "18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,32");
      // for (int inx = 0; inx < zipCntAry.length; inx++, dbInx++)
      // {
      //    //System.out.print("(" + inx + "," + zipCntAry[inx] + ")");
      //    String dbStr = String.format("%02d,", zipCntAry[inx]);
      //    System.out.print(dbStr);
      //    //System.out.print(zipCntAry[inx] + ",");
      //    dbCnt += zipCntAry[inx];
      // }
      //internalMsgCtrl.out("\nlabelCnt: " + dbCnt + ", indices: " + dbInx);
      //--------------------------------

      tray.initBracedAL();
      ArrayList<Brace> bracedAL = tray.bracedAL;

      // Set goals for how will be sliced
      int[] cutAry = split.numberOfCuts(tray);
      int cuts = cutAry[0];
      int goal = cutAry[1];

      if (cuts == 1)
      {
         Brace brace = closeSegment(0, (zipCntAry.length-1));
         bracedAL.add(brace);
         return bracedAL;
      }

      // parse through the zipCntAry
      int index = 0;
      int labelCnt = 0;
      for (int inx = 0; inx < zipCntAry.length; inx++)
      {
         int val = zipCntAry[inx];
         if (val >= 9)
         {
            // There may have been some labels that have been counted
            // That will be left behind if not accounted for now; prior
            // to branching off to take care of the unit zip
            if ((inx - index) > 0)
            {
               // The return is via a modified bracedAL
               saveTheMissed(index, inx, labelCnt, bracedAL);
            }

            Brace brace = newSegment(inx, inx);
            bracedAL.add(brace);

            // CloseUnitSegment() may have accounted for the tailing of
            // zipCntAry
            if ((bracedAL.get(bracedAL.size() - 1).endIndex) ==
                (zipCntAry.length - 1)) return bracedAL;

            // else more to process
            labelCnt = 0;
            index = inx + 1;
            continue;
         }

         labelCnt += val;
         if (labelCnt >= goal)
         {
            // startIndex = index ==> starting index within zipCntAry;
            // endIndex = inx ==> ending index within zipCntAry
            Brace brace = closeSegment(index, inx);
            bracedAL.add(brace);
            index = inx + 1;
            labelCnt = 0;
         }
      }
      
      // Take care of the reminder of zipCntAry if the entire array was not processed.
      // endFlg == true... this is the end of zipCntAry... tie up loose ends.
      // Return is a modified content of bracedAL
      saveTheTailings(zipCntAry, bracedAL);
      // cleanUp(startIndex, labelCnt, zipCntAry, bracedAL);

      return bracedAL;
   }

   // There may have been some labels that have been counted that will be left behind
   // if not accounted for prior to branching off to take care of a unit zip,
   // or left over when the parse of zipCntAry is finished.
   // Return is via a modified bracedAL
   private void saveTheMissed(int start, int end, int labelCnt,
                            ArrayList<Brace> bracedAL)
   {
      if (bracedAL.size() == 0)
      {
         // No segment exist that may be extended.  Thus must create a new segment.
         Brace brace = newSegment(start, (end-1));
         bracedAL.add(brace);
         return;
      }

      if (labelCnt >= 6)
      {
         // Create a new segment rather than enlarge another by such a size.
         // (This the same code as the condition above; but I choose to make
         // this an independent condition for clarity.)
         Brace brace = newSegment(start, (end-1));
         bracedAL.add(brace);
         return;
      }

      // Else  add these lost to the previous segment.
      bracedAL.get(bracedAL.size() - 1).endIndex += (end-start);
   }

   private void saveTheTailings(int[] zipCntAry, ArrayList<Brace> bracedAL)
   {
      int endIndex = zipCntAry.length - 1;

      if (bracedAL.size() == 0)
      {
         // This can not happen... but play it save
         
         // No segment exist that may be extended.  Thus must create a new segment.
         Brace brace = newSegment(0, endIndex);
         bracedAL.add(brace);
         return;
      }

      int startIndex = bracedAL.get(bracedAL.size() - 1).endIndex + 1;
      int tailings = (endIndex - startIndex) + 1;
      
      if (tailings >= 6)
      {
         // Create a new segment rather than enlarge another by such a size.
         // (This the same code as the condition above; but I choose to make
         // this an independent condition for clarity.)
         Brace brace = newSegment(startIndex, endIndex);
         bracedAL.add(brace);
         return;
      }

      // Else  add these lost to the previous segment.
      bracedAL.get(bracedAL.size() - 1).endIndex += tailings;
   }

   // Return a Brace (to be appended to bracedAL)
   private Brace closeSegment(int start, int end)
   {
      Brace brace = newSegment(start, end);
      return brace;
   }

   // The return is a modified bracedAL
   private Brace newSegment(int inx, int len)
   {
      return (classesPack.instantiateBrace(inx, len));
      // return (new Brace(inx, len));
   }
}

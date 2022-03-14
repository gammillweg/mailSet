package genLabelsPack;

import libPack.Addr;
import libPack.ClassesPack;
import libPack.Shared;

import java.util.ArrayList;

public class GenCuttingGuide
{
   public GenCuttingGuide(Shared shared)
   {
      this.shared = shared;
   }
   Shared shared = null;


   public ArrayList<String> GenGuttingGuide_Main(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry)
   {
      // FIXIT  --  Need to write the code
      System.out.println("CuttingGuide: Need to write the code.");
      ArrayList<String> cuttingGuildAL = new ArrayList<>();
      int labelsAcross = shared.getLabelsAcross();
      String cuttingGuildStr = "";

      int debugCnt = 0;

      cuttingGuildStr = "Seq Rows First Last  Tray Tot Cur Rew  Renewal List";
      cuttingGuildAL.add(cuttingGuildStr);
      for (int index = 0; index < segmentAry.length; index++) {
         ClassesPack.Segment segment = segmentAry[index];
         int labelsCnt = segment.len;
         int rows = labelsCnt / labelsAcross;
         if (rows == 0) rows = 1;
         String first = addrBundleAry[segment.startIndex].zip;
         String last = addrBundleAry[segment.startIndex + (segment.len -1)].zip;
         String tray = segment.trayTag;
         int renewals = 0;
         if (segment.renewalAry != null) renewals = segment.renewalAry.length;
         int current = labelsCnt - renewals;

         cuttingGuildStr = String.format("(%02d)   ", index);

         cuttingGuildStr += rows + " " + first + " " + last + " " + tray;
         cuttingGuildStr += String.format("  %02d  %02d  %02d", labelsCnt, current, renewals);

         if (segment.renewalAry != null) {
            String space = "   ";
            for (int inx = 0; inx < segment.renewalAry.length; inx++) {
               String zip = addrBundleAry[segment.renewalAry[inx]].zip;
               cuttingGuildStr += space + zip;
               space = " ";
            }
         }
         cuttingGuildAL.add(cuttingGuildStr);


         //debugCnt++;
         //if (debugCnt == 4) break;
      }
      for (String str : cuttingGuildAL) System.out.println(str);


      return cuttingGuildAL;
      //return (new ArrayList<String>());
   }
}

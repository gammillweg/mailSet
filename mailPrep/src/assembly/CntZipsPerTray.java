package assembly;

import libPack.Addr;
import libPack.ClassesPack.Tray;
import libPack.Shared;

import java.util.ArrayList;


// Count the zips per trayTag Tray
public class CntZipsPerTray
{
   public CntZipsPerTray(Shared shared)
   {
      this.shared = shared;
   }

   private Shared shared = null;

   // Splits the trays into slices suitable to package in label envelopes.
   // The term used:  into segments
   // Return is filled tray (containing a trayTag value) zipCntAry
   public void cntZipEntry(ArrayList<Tray> trayAL, Addr[] addrBundleAry)
   {
      // trayAL is sorted in configured processing order (bundle order)
      // Currently:  D5, MXD, OMX, SCF, 800, 801, 802, 804
      for (Tray tray : trayAL)
      {
         if (tray.trayTag.isEmpty()) continue;
         countZipSets(tray, addrBundleAry);
      }
   }

   // Counts the number of matching zips in the trayTag.
   // Return modified tray with filled tray.zipCntAry
   private void countZipSets(Tray tray, Addr[] addrBundleAry)
   {
      // Two passes... the first to find out the size to allocate
      // the second to fill the allocated array

      int cnt = 0;
      int startInx = tray.startIndex;
      String currZip = addrBundleAry[tray.startIndex].zip;

      // This pass to determine the size of the array
      for (int inx = tray.startIndex; inx < (tray.startIndex + tray.len); inx++)
      {
         // Step through all of this zip
         if (currZip.equals(addrBundleAry[inx].zip)) continue;

         // All of the currZip have been counted
         cnt++;

         startInx = inx;
         currZip = addrBundleAry[inx].zip;
      }
      cnt++;
      int[] zipCntAry = new int[cnt];
      tray.initZipCntAry(cnt);

      // This pass fills the allocated array
      int index = 0;
      startInx = tray.startIndex;
      currZip = addrBundleAry[tray.startIndex].zip;
      
      for (int inx = tray.startIndex; inx < (tray.startIndex + tray.len); inx++)
      {
         // Step through all of this zip
         if (currZip.equals(addrBundleAry[inx].zip)) continue;

         // All of the currZip have been counted
         tray.zipCntAry[index++] = (inx - startInx);

         startInx = inx;
         currZip = addrBundleAry[inx].zip;
      }
      tray.zipCntAry[index] = (tray.startIndex + tray.len) - startInx;
   }
}


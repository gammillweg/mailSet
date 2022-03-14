package assembly;

import configPack.XMLData;
import libPack.Addr;
import libPack.ClassesPack;
import libPack.ClassesPack.Tray;
import libPack.Shared;

import java.util.ArrayList;

// Bundles are the main trayTag division as in 5D, 3D, SCF, ADC, OMX, MXD
// Addr contains bundle classification.
// The next division is trayTag.  Some bundles form a single trayTag, others,
// notably 3D are sub divided (as in 800, 801...)
public class SplitByBundle
{
   public SplitByBundle(Shared shared)
   {
      this.shared = shared;
      xmlData = shared.getXMLData();
      classesPack = shared.getClassesPack();
   }
   Shared shared = null;
   XMLData xmlData = null;
   ClassesPack classesPack = null;

   // Returns an ArrayList of Tray
   public ArrayList<Tray> splitBundle(Addr[] addrBundleAry)
   {
      ArrayList<Tray> majorTrayAL = new ArrayList<Tray>();
         
      // Find the bounds... startIndex and endIndex of each bundle
      // trayAL is sorted in configured processing order (bundle order)
      // Note that each bundles is in zip order.
      String[] orderAry = xmlData.get_order();
      for (String bundle : orderAry)
      {
         Tray majorTray = margeMajorBundles(addrBundleAry, bundle);
         majorTrayAL.add(majorTray);
      }

      // SplitUtils bundles by trayTag
      ArrayList<Tray> trayAL = splitToTrays(orderAry, majorTrayAL, addrBundleAry);
      
      return trayAL;
   }

   // Splits any tray into sub divisions as necessary.
   // (Specifically:  Bundle 3D, contains multiple sub divisions (800, 801, 802, 804).
   // But the code is general subdivided on trayTag, so 5D would also sub divide if needed.
   private ArrayList<Tray> splitToTrays(String[] orderAry,
                                        ArrayList<Tray> majorTrayAL, Addr[] addrBundleAry)
   {
      ArrayList<Tray> trayAL = new ArrayList<Tray>();
      Tray tray = null;

      for (String bundle : orderAry)
      {
         // Find the Tray for bundle (Finding the major tray located the
         // starting startIndex and endIndex of the bundle.)
         Tray majorTray = findMajorTray(bundle, majorTrayAL);
         if (majorTray== null)
            continue;

         // A bundle may not contain any items (an empty tray)... do not create it.
         if (majorTray.startIndex== -1)
            continue;

         //-----------------------------------------------------------
         // This creation of Tray is for bundles that are NOT sub divided
         boolean startFlg = false;
         String trayTag = "";

         trayTag = addrBundleAry[majorTray.startIndex].trayTag;
         // The string (D3 trayTag name to match
         // -1 as we stepped one past the startIndex when ending the run through
         tray = classesPack.instantiateTray();
         //tray = new Tray();
         tray.trayTag = trayTag;
         tray.bundle = bundle;
         tray.startIndex = majorTray.startIndex;
         //-----------------------------------------------------------
         
         // Find any additional trays
         int inx = 0;
         for (inx = majorTray.startIndex; inx < (majorTray.startIndex + majorTray.len); inx++)
         {
            // This condition for sub divisions
            if (startFlg)
            {
               trayTag = addrBundleAry[inx-1].trayTag;
               // The string (D3 trayTag name to match
               // -1 as we stepped one past the startIndex when ending the run through
               tray = classesPack.instantiateTray();
               //tray = new Tray();
               tray.trayTag = trayTag;
               tray.bundle = bundle;
               tray.startIndex = inx-1;

               startFlg = false;
               continue;
            }
            // Skip through all of this trayTag
            if (trayTag.equals(addrBundleAry[inx].trayTag)) continue;

            // If here we have moved onto the next trayTag to be marked
            // Close the open tray and add to the return array list
            tray.len = inx - tray.startIndex;
            trayAL.add(tray);

            // And we startIndex another trayTag
            startFlg = true;
         }

         // Close the open tray and add to the return array list
         tray.len = (inx - tray.startIndex);
         trayAL.add(tray);
      }

      return trayAL;
   }

   // Find a majorTray by bundle  (Major trays are the initialization of tray.
   // finding the startIndex int addrBundleAry and the lenght of the bundle.)
   private Tray findMajorTray(String bundle, ArrayList<Tray> majorTrayAL)
   {
      for (Tray majorTray : majorTrayAL)
      {
         //if (majorTray.majorFlg)
         //{
            if (majorTray.bundle.equals(bundle))
               return majorTray;
            //}
      }
      return null;
   }

   // Find the startIndex and endIndex of a bundle.  Know that addrBundAry is sorted
   // in bundle order (and zip's are still in order under each bundle).
   // The MajorBundle is an initial capture; by bundle of only the startIndex startIndex (in
   // addrBundleAry) and the endIndex of the bundle.
   private Tray margeMajorBundles(Addr[] addrBundleAry, String bundle)
   {
      Tray tray = classesPack.instantiateTray();
      //Tray tray = new Tray();
      tray.bundle = bundle;
      
      boolean startFlg = false;
      for (int inx = 0; inx < addrBundleAry.length; inx++)
      {
         if (startFlg)
         {
            // The first was found.
            // on to hunting for the len

            if (bundle.equals(addrBundleAry[inx].bundle) == false)
            {
               tray.len = (inx - tray.startIndex);
               return tray;
            }
            continue;
         }
         if (bundle.equals(addrBundleAry[inx].bundle))
         {
            tray.startIndex = inx;
            //tray.majorFlg = true;
            startFlg = true;
            continue;
         }
      }

      if (startFlg)
      {
         // reached the len of the array with the last bundle in the array
         tray.len = addrBundleAry.length - tray.startIndex;
      }
      
      return tray;
   }
}

package buildAddr;

import configPack.XMLData;
import libPack.Addr;
import libPack.Shared;

public class ZonePerZip
{
   public ZonePerZip(Shared shared)
   {
      this.shared = shared;
   }
   Shared shared = null;

   // Fills the Zone member of each Addr member in the addrAL
   // Return is modified input list.
   public void zonePerZip_Main(Addr[] addrZipAry)
   {
      XMLData xmlData = shared.getXMLData();

      // An array (one for each zone) of 3D strings:
      // {"800", "801", "802", "803", "804", "805", "806", "807", "808", "809",
      //  "810", "811", "812"}

      String[] zoneAry = xmlData.get_ZONE1();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE1");
      //debugZoneAssignments(addrZipAry);
      
      zoneAry = xmlData.get_ZONE2();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE2");
      //debugZoneAssignments(addrZipAry);

      zoneAry = xmlData.get_ZONE3();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE3");
      //debugZoneAssignments(addrZipAry);

      zoneAry = xmlData.get_ZONE4();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE4");
      //debugZoneAssignments(addrZipAry);
      
      zoneAry = xmlData.get_ZONE5();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE5");
      //debugZoneAssignments(addrZipAry);
      
      zoneAry = xmlData.get_ZONE6();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE6");
      //debugZoneAssignments(addrZipAry);
      
      zoneAry = xmlData.get_ZONE7();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE7");
      //debugZoneAssignments(addrZipAry);
      
      zoneAry = xmlData.get_ZONE8();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE8");
      //debugZoneAssignments(addrZipAry);

      zoneAry = xmlData.get_ZONE9();
      assignZonePerZip(addrZipAry, zoneAry, "ZONE9");

      // internalMsgCtrl.out("post size:      " + addrAL.size());
      // int cnt = 1;
      // for (Addr addr : addrAL)
      // {
      //    internalMsgCtrl.out(cnt + ")  " + addr.zone);
      //    internalMsgCtrl.out(cnt + ")  " + addr.csv);
      //    cnt++;
      // }
   }

//   private void debugZoneAssignments(Addr[] addrZipAry)
//   {
//      int cnt = 0;
//      for (Addr addr : addrZipAry)
//      {
//         System.out.println("debug zones:  " + cnt + ")  " + addr.zip + ", " + addr.zone);
//         cnt++;
//      }
//   }

   // Find the zone each zip code belongs to and fill the Addr.zone member
   // Return is modified addrAL
   private void assignZonePerZip(Addr[] addrZipAry, String[] zoneAry, String zoneID)
  {

      for (Addr addr : addrZipAry)
      {
         if (addr.zone.isEmpty())
         {
            String d3 = addr.zip.substring(0, 3);
            for (String zip3d : zoneAry)
            {
               if (d3.equals(zip3d))
               {
                  addr.zone = zoneID;
               }
            }
         }
      }
   }
}

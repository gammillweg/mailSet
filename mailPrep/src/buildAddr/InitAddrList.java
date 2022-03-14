package buildAddr;

import configPack.XMLData;
import libPack.Addr;
import libPack.CheckRenewal;
import libPack.InternalFatalError;
import libPack.Shared;

import java.util.ArrayList;


//190113 -- Following... for the moment is straight cs code...NOT java
// straight from BulletinSlice... needs much effort to compile and work.

public class InitAddrList
{
   public InitAddrList(Shared shared)
   {
      this.shared = shared;
   }
   Shared shared = null;
   XMLData xmlData = null;
   //-------------------------------------------------------------------------
   // The Addr class hold the Master Excel Address List data
   // Plus the zip's Tray and Zone (see TrayTagPerZip and ZonePerZip below)
   public Addr[] buildAddr(ArrayList<String> addressListAL) throws InternalFatalError
   {
      try
      {
         // From a content list convert to a list of Class Addr
         Addr[] addrZipAry = toAddr(addressListAL);
         if (addrZipAry==null) return null;

         // FIXIT -- rename existing genConfigFile.java to genConfigJava.java
         //  and make new app genConfigFile app -- take the following code out of here
         //  and in the new genConfigJava app

         // Check issue expiration dates: this is a fatal error
         CheckRenewal checkRenewal = new CheckRenewal(shared);
         if (checkRenewal.containsExpired(addrZipAry)) return null;

         // Returns are a modified addrZipAry
         // Fills the zone member of each Addr member in the addrList
         ZonePerZip zonePerZip = new ZonePerZip(shared);
         zonePerZip.zonePerZip_Main(addrZipAry);

         // Fills the trayTag member of each Addr member in the addrList
         // Return is modified addrList
         TrayTagPerZip trayPerZip = new TrayTagPerZip(shared);
         trayPerZip.trayTagPerZip_Main(addrZipAry);

         return addrZipAry;
      }
      catch (InternalFatalError excp)
      {
         throw new InternalFatalError();
      }
   }


   private Addr[] toAddr(ArrayList<String> addressListAL) throws InternalFatalError
   {
      if ((addressListAL == null) || (addressListAL.size() == 0))
      {
         return null;
      }
      
      // Save the Excel header String
      String header = addressListAL.get(0);

      // Separate the AddressList lines into a list of Addr classes
      // The list is returned Sorted by Zip
      // Fills members renewal and zip
      try
      {
         InitAddr initAddr = new InitAddr(shared);
         Addr[] addrZipAry = initAddr.initAddr_Main(addressListAL);

      // The error has been reported via System.err.println()
      // return an empty list
      return addrZipAry;
      }
      catch (InternalFatalError exc)
      {
         throw new InternalFatalError();
      }
   }


}

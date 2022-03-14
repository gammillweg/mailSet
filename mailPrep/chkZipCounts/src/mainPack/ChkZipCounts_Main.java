package mainPack;

import buildAddr.InitAddrList;
import buildAddr.Verify_Addr;
import chkZipCountsPack.Report_ZipCounts;
import chkZipCountsPack.Validate_ZipCounts;
import chkZipCountsPack.Verify_ZipCounts;
import configPack.XMLData;
import fileWork.FileWork;
import libPack.*;
import libPack.InternalMsgCtrl.errKey;

import java.io.IOException;
import java.util.ArrayList;

public class ChkZipCounts_Main
{
   public static void main(String[] args) throws InternalFatalError
   {
      // There is ONE and ONLY ONE instance of Shared
      Shared shared = new Shared();
      //------------------------------------------

      shared.setAppName("chkZipCounts");

      // Setup centralized user messages handler.
      // All System.out System.err messages should be passed through errorMegCtrl
      InternalMsgCtrl internalMsgCtrl = shared.getInternalMsgCtrl();

      /*********************************************/
      //-- 1) Read user arguments

      try
      {
         // Argument processing is done with jar args4j and
         // the results are stored in Shared
         CLIOptions cliOptions = shared.getCLIOptions();
         if (cliOptions!=null)
         {
            if ((cliOptions.cliOptionsMain(args))==false)
               System.exit(0);
         }
      }
      catch (IOException ioExcp)
      {
         //System.err.println(ioExcp.toString());
         internalMsgCtrl.err(InternalMsgCtrl.errKey.ExceptionMsg, false, ioExcp.toString());
         System.exit(-1);
      }

      /*********************************************/
      // Keep as an example
      //Locale locale = new Locale("en", "EN");
      //ResourceBundle tstRB = ResourceBundle.getBundle("resourceBundles.ErrorMsgs");
      //String jkj = tstRB.getString("test1");
      //jkj = tstRB.getString("test2");

      /*********************************************/
      //-- 2) Read the configuration file

      // Read the BulletinAssembly configuration file "$Home/.BulletinAssembly/config.xml"
      try
      {
         shared.parseConfigData();
         XMLData xmlData = shared.getXMLData();
         if (xmlData==null)
         {
            internalMsgCtrl.err(errKey.FatalError, false,
                  "Unable to read the required configuration file.");
            internalMsgCtrl.err(errKey.FatalError, false, "Or, an error while reading " + shared.getConfigFileName());
            internalMsgCtrl.err(errKey.Message, false, "-------------------------------------------------------");
            internalMsgCtrl.err(errKey.Message, false, "genConfigFile may be used to generate a new " + shared.getConfigFileName());
            internalMsgCtrl.err(errKey.Message, false, "-------------------------------------------------------");

            String appName = shared.getAppName();
            internalMsgCtrl.out("Obtain the configuration file, edit as necessary, and rerun " + appName + ".");
            System.exit(-1);
         }
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(errKey.FatalError, false, "Failed to read the configuration file.");
         //System.err.println(excp.toString());
         //internalMsgCtrl.err(errKey.ExceptionMsg, false, excp.toString());
         System.exit(-1);
      }

      /*********************************************/
      //-- 3 Get and check for existence the data path

      FileWork fileWork = shared.getFileWork();

      String dataFolderPathName = shared.getDataFolderPathName();
      if (fileWork.isFolder(dataFolderPathName)==false)
      {
         String errMsg = "Is not a directory:  [" + dataFolderPathName + "]";
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }
      if (fileWork.fileExists(dataFolderPathName)==false)
      {
         String errMsg = "Unable to find data directory:  " + dataFolderPathName;
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }

      /*********************************************/
      //-- 4 Read Bulletin Assemblies Excel AddressList worksheet pre-saved as a CSV file

      // FIXIT -- it seems to me Shared should have build this
      //           addressListFilePathName... should be built here

      String addressListFilename = shared.getAddressListFileName();
      String folderBreak = shared.getFolderBreak();
      String addrListFilePath = dataFolderPathName + folderBreak + addressListFilename;

      if (fileWork.fileExists(addrListFilePath)==false)
      {
         String errMsg = "Unable to find csv file:  " + addrListFilePath;
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }

      ArrayList<String> addressListAL = null;
      try
      {
         addressListAL = fileWork.readFile(addrListFilePath);

         Verify_Addr verifyAddrCSV = new Verify_Addr(shared);
         if (verifyAddrCSV.verifyAddrCSV_Main(addressListAL)==false)
         {
            String errMsg = "invalid address CSV file:  " + addrListFilePath;
            internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         }
      }
      catch (InternalFatalError exc)
      {
         System.exit(-1);
      }

      /*********************************************/
      //-- 5) Process the AddressList CSV file and store in an array of class Addr
      //      sorted by zip code
      //      Three lower classes are used:  CheckRenewal, ZonePerZip and TrayTagPerZip
      //      These fill out all the fields in Class Addr (zip, zone, trayTag and bundle
      Addr[] addrAry = null;
      try
      {
         InitAddrList initAddrList = new InitAddrList(shared);
         addrAry = initAddrList.buildAddr(addressListAL);
         if (addrAry==null)
         {
            internalMsgCtrl.err(errKey.FatalError, false, "Unable to build the address list.");
            System.exit(-1);
         }
      }
      catch (InternalFatalError exc)
      {
         System.exit(-1);
      }

      /*********************************************/
      //-- 6) Read and verify the zipCounts.csv file
      String zipCountsFilename = shared.getZipCountsFileName();
      String zipCountsFilePath = dataFolderPathName + folderBreak + zipCountsFilename;

      if (fileWork.fileExists(zipCountsFilePath)==false)
      {
         String errMsg = "Unable to find csv file:  " + zipCountsFilePath;
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }

      XMLData xmlData = shared.getXMLData();
      ExcelUtils excelUtils = shared.getExcelUtils();
      String zipColumn = "";
      ArrayList<String> zipColAL = null;
      ArrayList<String> zipCountsContentAL = null;
      ArrayList zonesAL = null;
      String[] ZC_zonesAry = null;
      String[] icAry = xmlData.get_ic();

      try
      {
         zipCountsContentAL = fileWork.readFile(zipCountsFilePath);
         zonesAL = xmlData.get_zones();
         ZC_zonesAry = xmlData.get_ZC_zones();

         Verify_ZipCounts verifyCountsCSV = new Verify_ZipCounts(shared);
         if (verifyCountsCSV.verifyZipCountCSV_Main(ZC_zonesAry, zipCountsContentAL)==false)
         {
            // Error messages were printed out prior to the false return;
            System.exit(-1);
         }
      }
      catch (InternalFatalError exc)
      {
         internalMsgCtrl.err(errKey.FatalError, true, "Failed to read file:  " + zipCountsFilePath);
         System.exit(-1);
      }

      /*********************************************/
      //-- 7) Find any zip codes per zone (ic, den_spf, or dadc) that are in the
      //      Address list but not in the zipCodes list.  Also find any zip coded
      //      in a zone (ic, den_spf, or dadc) which should NOT be in that column.

      Validate_ZipCounts findMissingCountZips = new Validate_ZipCounts(shared);
      ArrayList<String> errStrAL = findMissingCountZips.valid_ZipCounts_Main(addrAry, ZC_zonesAry, zipCountsContentAL);

      /*********************************************/
      // Report

      Report_ZipCounts report_zipCounts = new Report_ZipCounts(shared);
      ArrayList<String> errorAL = report_zipCounts.report_ZipCounts_Main(errStrAL);

      //errorAL.forEach(str -> System.out.println(str));
      for (String str : errorAL)
      {
         internalMsgCtrl.out(str);
      }
   }

}

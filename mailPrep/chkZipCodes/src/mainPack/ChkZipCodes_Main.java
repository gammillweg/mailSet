package mainPack;

import buildAddr.InitAddrList;
import buildAddr.Verify_Addr;
import chkZipCodesPack.Report_ZipCodes;
import chkZipCodesPack.Validate_ZipCodes;
import chkZipCodesPack.Verify_ZipCodes;
import configPack.XMLData;
import fileWork.FileWork;
import libPack.*;
import libPack.InternalMsgCtrl.errKey;

import java.io.IOException;
import java.util.ArrayList;

public class ChkZipCodes_Main
{
   public static void main(String[] args) throws InternalFatalError
   {
      // There is ONE and ONLY ONE instance of Shared
      Shared shared = new Shared();
      //------------------------------------------

      // Setup centralized user messages handler.
      // All System.out System.err messages should be passed through errorMegCtrl
      InternalMsgCtrl internalMsgCtrl = shared.getInternalMsgCtrl();

      shared.setAppName("chkZipCodes");

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
         internalMsgCtrl.err(errKey.ExceptionMsg, false, ioExcp.toString());
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
      if (fileWork.isFolder(dataFolderPathName) == false)
      {
         String errMsg = "Is not a directory:  [" + dataFolderPathName + "]";
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }
      if (fileWork.fileExists(dataFolderPathName) == false)
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
      //-- 6) Read and verify the zipCodes.csv file

      String zipCodesFilename = shared.getZipCodesFileName();
      String zipCodesFilePath = dataFolderPathName + folderBreak + zipCodesFilename;

      if (fileWork.fileExists(zipCodesFilePath)==false)
      {
         String errMsg = "Unable to find csv file:  " + zipCodesFilePath;
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         System.exit(-1);
      }

      XMLData xmlData = shared.getXMLData();
      ExcelUtils excelUtils = shared.getExcelUtils();
      String zipColumn = "";
      ArrayList<String> zipColAL = null;
      ArrayList<String> zipCodesAL = null;

      try
      {
         zipCodesAL = fileWork.readFile(zipCodesFilePath);
         zipColumn = xmlData.get_Zs_ZIP();
         zipColAL = excelUtils.CSVToColumn(zipColumn, zipCodesAL);

         Verify_ZipCodes verifyCSV = new Verify_ZipCodes(shared);
         if (verifyCSV.verifyZipCodesCSV_Main(zipColAL)==false)
         {
            String msg0 = "invalid data in CSV file:  " + zipCodesFilePath + "\n";
            String msg1 = "All but the heading in column:  " + zipColumn + ", must be 5-digit zip codes or empty";
            String errMsg = msg0 + msg1;

            internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         }
      }
      catch (InternalFatalError exc)
      {
         internalMsgCtrl.err(errKey.FatalError, true, "Faile to read file:  " + zipCodesFilePath);
         System.exit(-1);
      }

      /*********************************************/
      //-- 6) Find any zip codes that in the Address list but not in the zipCodes list

      Validate_ZipCodes findMissingZips = new Validate_ZipCodes(shared);
      ArrayList<Integer> missingIntAL = findMissingZips.findMissingCodeZips_Main(addrAry, zipColAL);

      internalMsgCtrl.out("--------------------------------------------------");
      internalMsgCtrl.out("The file:  " + zipCodesFilePath);
      internalMsgCtrl.out("has been checked.");
      internalMsgCtrl.out("-----------------");

      /*********************************************/
      //-- 7) Report the findings

      if (missingIntAL.size() == 0)
      {
         internalMsgCtrl.out("No problems were found.");
         internalMsgCtrl.out("--------------------------------------------------");
         System.exit(0);
      }

      Report_ZipCodes zipCodeReport = new Report_ZipCodes(shared);
      String reportStr = zipCodeReport.buildReportStr(addrAry, missingIntAL);
      internalMsgCtrl.out(reportStr);
      internalMsgCtrl.out("--------------------------------------------------");
   }
}



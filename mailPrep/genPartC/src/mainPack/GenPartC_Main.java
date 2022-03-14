package mainPack;

import buildAddr.InitAddrList;
import buildAddr.Verify_Addr;
import configPack.XMLData;
import fileWork.FileWork;
import genPartCPack.OutCountyPartC;
import libPack.*;

import java.io.IOException;
import java.util.ArrayList;

public class GenPartC_Main
{
   public static void main(String[] args) throws InternalFatalError
   {
      // There is ONE and ONLY ONE instance of Shared
      Shared shared = new Shared();

      // Setup centralized user messages handler.
      // All System.out System.err messages should be passed through errorMegCtrl
      InternalMsgCtrl internalMsgCtrl = shared.getInternalMsgCtrl();

      shared.setAppName("genPartC");

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
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false,
                  "Unable to read the required configuration file.");
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Or, an error while reading " + shared.getConfigFileName());
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "-------------------------------------------------------");
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "genConfigFile may be used to generate a new " + shared.getConfigFileName());
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "-------------------------------------------------------");

            String appName = shared.getAppName();
            internalMsgCtrl.out("Obtain the configuration file, edit as necessary, and rerun " + appName + ".");
            System.exit(-1);
         }
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Failed to read the configuration file.");
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
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, errMsg);
         System.exit(-1);
      }
      if (fileWork.fileExists(dataFolderPathName) == false)
      {
         String errMsg = "Unable to find data directory:  " + dataFolderPathName;
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, errMsg);
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
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, errMsg);
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
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, errMsg);
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
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Unable to build the address list.");
            System.exit(-1);
         }
      }
      catch (InternalFatalError exc)
      {
         System.exit(-1);
      }

      /*********************************************/
      //-- 6) Generate PartC
      OutCountyPartC outCountyPartC = new OutCountyPartC(shared);
      ArrayList<String> csvAL =  outCountyPartC.outCountyPartC_Main(addrAry);

      // TODO -- need a CLIOptions for the name of the PartC file.

      final String partCFileName = dataFolderPathName + folderBreak + "partC.csv";
      fileWork.writeFile(partCFileName, csvAL);

      String zipCountsFilePathName = shared.getZipCountsFilePathName();

      internalMsgCtrl.out("--------------------------------------------------");
      internalMsgCtrl.out("Read from file:  " +  addrListFilePath);
      internalMsgCtrl.out( "Read from file:  " + zipCountsFilePathName);
      internalMsgCtrl.out("Written to file: " + partCFileName);
      internalMsgCtrl.out("--------------------");
      for (String str : csvAL) internalMsgCtrl.out(str);
      internalMsgCtrl.out("--------------------------------------------------");
   }
}

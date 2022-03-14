package mainPack;

import fileWork.FileWork;
import genConfigOMXPack.ParseOMXInData;
import libPack.CLIOptions;
import libPack.InternalFatalError;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.io.IOException;
import java.util.ArrayList;

public class GenConfigOMX_Main
{
   /**************************************************************************************
    * 2201010 creation
    * (FileWork, InternalFatalError InternalMsgCtrl Shared, CLIOptions and this copied
    * form genLabels the genConfigFile to genConfigZones to here -- with necessary changes.
    * genConfigOMX reads a text file of an expected format, created via a copy/paste
    * of a USPS website: (as of this date) https://fast.usps.com/fast/fastApp/resources/labelListFiles.action
    * Then select a date (the most recent) submit and look for L201 Periodicals about 2/3 of the way down
    * and click "view".  Now search for DENVER (your city), and copy the block in the center column
    * 800-812 (Denver CO).
    * There are 3 columns of data.  Copy only the center column
    * Paste to a file.  The pasted data will be one LONG line.
    * The input file is an ascii text file in the copied format
    * The output file is an ascii xml formatted file.  The content of the output
    * file is formatted to be copied/paste into the config.xml file <OMX></OMX>
    *
    * The default input file is [Home]/.BulletinAssembly/zones.txt
    * Use option -i (--input), an absolute path to change the name of the input file name.
    * The in file must exist
    *
    * The default output file is [Home]/.BulletinAssembly/zones.xml
    * Use option -o (--output), an absolute path to change the name of the output file name.
    * The out file will be created if it does not exist
    *
    * Will expand a leading ~ as Home in Linux
    * Will treat ./ or \. as the current working directory (CWD)
    * Example:  output file in CWD, use "-o ./filename.xml"
    *
    * Note that the copied html data is expected to be a long single line.
    * See folder genConfigOMX/Notes/OMX_Data.txt within the Intellij IDE project data for the input format
    * see folder genConfigOMX/Notes/Zones_Formatted.xml with the Intellij IDE project data for the output format
    **************************************************************************************/
   public static void main(String[] args) throws InternalFatalError
   {
      final String appName = "genConfigOMX";
      final String default_InFileName =  "omx.txt";
      final String default_OutFileName = "omx.xml";

      // There is ONE and ONLY ONE instance of Shared
      Shared shared = new Shared();
      shared.setAppName(appName);
      shared.setDefault_InFileName(default_InFileName);
      shared.setDefault_ouTFileName(default_OutFileName);


      FileWork fileWork = shared.getFileWork();
      ParseOMXInData parseOMXInData = shared.getParseOMXInData();

      //------------------------------------------

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
         cliOptions.setGenConfigOMXFlg();
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

      /*******************************************/
      //--2 Read input file (see Notes/zonesEx.txt for an example file)

      String inPathName = shared.getInPathName();
      ArrayList<String> inDataAL = null;
      try
      {
         inDataAL = fileWork.readFile(inPathName);
         if (inDataAL.isEmpty())
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, true, "The data file was empty.");
            System.exit(-1);
         }
         if (parseOMXInData.verifyOMXInData(inDataAL)==false)
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Invalid input data file:  [" + inPathName + "]");
            System.exit(-1);
         }
      }
      catch (InternalFatalError err)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, true, "Invalid inptu data file.");
         System.exit(-1);
      }

      /*******************************************/
      //--3 process input data (ArrayList) (see Notes/zonesEx.txt for an example file)

      String parsedStr = "";
      try
      {
         parsedStr = parseOMXInData.parseInDataAL(inDataAL);
         if (parsedStr.isEmpty())
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Empty output string.");
            System.exit(-1);
         }
      }
      catch (InternalFatalError err)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, true, "Fatal Error while parsing the input data.");
         System.exit(-1);
      }

      /*******************************************/
      //--4 Write the processes zone data  (see Bulletin Assembly config.xml <ZONES>
      //                                    for an example of the format)

      String outPathName = shared.getOutPathName();
      try
      {
         if (fileWork.isAbsolutPath(outPathName))
         {
            // the check to see if the file exists
            if (fileWork.fileExists(outPathName)==false)
            {
               try
               {
                  fileWork.createPath(outPathName, true);
               }
               catch (InternalFatalError err)
               {
                  internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Problems in creating file: [" + outPathName + "]");
                  System.exit(-0);
               }
            }
            ArrayList strAL = new ArrayList(1);
            strAL.add(parsedStr);
            fileWork.writeFile(outPathName, strAL);
         }
      }
      catch (InternalFatalError err)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, true, "Fatal Error while writing to the output file.");
      }

      /*******************************************/
      // --5 Report to the user the filePathNames read and the filePathName written to
      // Use InternalMsgCtrl.message()
      internalMsgCtrl.out("---------------------------------------------------------");
      internalMsgCtrl.out("Using USPS web data from:  ");
      internalMsgCtrl.out("      [" + inPathName + "]");
      internalMsgCtrl.out("An xml formatted file:  ");
      internalMsgCtrl.out("      [" + outPathName + "]");
      internalMsgCtrl.out("Has been created.  Copy the content of this file to ");
      internalMsgCtrl.out("your config.xml file.");
      internalMsgCtrl.out("---------------------------------------------------------");
   }
}

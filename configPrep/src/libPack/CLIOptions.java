package libPack;

import fileWork.FileWork;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;

//import static java.lang.System.out;

// args4j is used to parse command line option (CLI)
// args4j jars are stored in /usr/local/args4j
// Arguments are parsed here and the results are stored in Share
public class CLIOptions
{
   public CLIOptions(Shared shared)
   {
      this.shared = shared;

      //-------------------------------------------------------------------------------
      // These must be set here.  Originally I called shared to set them  on their code
      // lines below.  BUT... run time java processes definitions PRIOR to the constructor.
      // Thus, share had not yet been properly defined yet.

      // Read comment at len of Shared on how default file names are defined in Shared.
      inPathName = shared.getInPathName();
      outPathName = shared.getOutPathName();

      internalMsgCtrl = shared.getInternalMsgCtrl();
      //-------------------------------------------------------------------------------
   }
   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;

   private String inPathName = "";
   private String outPathName = "";


   // CLIOptions may be called by any one of the three: genConfigFile, genConfigZones or genConfigOMX
   boolean genConfigFileFlg = false;
   public void setGenConfigFileFlg() { genConfigFileFlg = true;}
   boolean genConfigZonesFlg = false;
   public void setGenConfigZonesFlg() { genConfigZonesFlg = true;}
   boolean genConfigOMXFlg = false;
   public void setGenConfigOMXFlg() { genConfigOMXFlg = true;}
   /*=================================================================================*/
   // Define argv4j valid options

   // argv4j's print option list call will print the following in alphbetical order from -?
   // It would be nice if it would print them in the order below; but is OK.

   //----------------------------------------------------------------------------
   //-- Help messages
   //
   @Option(name="-h", aliases="--listOptions", usage="Lists valid options.", required=false)
   private  boolean listOptions = false;

   @Option(name="-help", aliases="--help", usage="Show a brief help message.", required=false)
   private  boolean help = false;

   //--------------------------------------------------------------------------
   @Option(name="-gx", aliases="--genConfigXML", usage="Generate config.xml file.")
   private boolean genConfigXML = false;

   @Option(name="-gj", aliases="--genConfigJAVA", usage="Generate ConfigText.java file.")
   private boolean genConfigJAVA = false;

   @Option(name="-ld", aliases="--listDefaults", usage="list the coded default folder and file names. (ld:[List Defaults])", required=false)
   private  boolean listDefaults = false;

   @Option(name="-i", aliases="--input", usage="an absolute path to the input file. (use -ld to see defaults)", required=false)
   private String opInPathName = this.inPathName;

   @Option(name="-o", aliases="--output", usage="an absolute path to the output file.  (use -ld to see defaults)", required=false)
   private  String opOutPathName = this.outPathName;
   /*=================================================================================*/

   /**
    * @param arguments Command-line arguments to be processed with Args4j.
    */
   public boolean cliOptionsMain(final String[] arguments) throws IOException, InternalFatalError
   {
      final CmdLineParser parser = new CmdLineParser(this);
      try
      {
         // This is where Args4j fills vars defined above with options
         // provided by the user.
         // (Example:  configFilename is defined above
         //            configFilename is defined in shared as "config.xml"
         //            Above (constructor) local configFilename is received from shared
         //            User option -c "BulletinSlicerConfig.xml" is used
         //            parser.parseArgument() replaces local configFilenmame with user option name
         //            Below shared.setConfigFilename() is called to replace the default with the users name
         parser.parseArgument(arguments);

         // Return
         //    true:  will allow process to run
         //    false: will cause process to exit
         //

         // I check for the existence of folders and files
         FileWork fileWork = shared.getFileWork();
         String folderBreak = shared.getFolderBreak();

         // --------------------------------------------------

         if (listOptions)  // -h
         {
            internalMsgCtrl.out("-----------------------------------------------");
            parser.printUsage(System.out);
            internalMsgCtrl.out("-----------------------------------------------");
            return false;
         }

         if (help)
         {
            if (genConfigFileFlg) genConfigFileHelp();
            if (genConfigOMXFlg) genConfigOMXHelp();
            if (genConfigZonesFlg) genConfigZonesHelp();
            return false;
         }

         // --------------------------------------------------

         if (listDefaults)
         {
            String appName = shared.getAppName();
            String default_AppFolderPathName = shared.getAppFolderPathName();
            String default_InPathName = shared.getInPathName();
            String default_OutPathName = shared.getOutPathName();

            internalMsgCtrl.out("--------------------------------------------------");
            internalMsgCtrl.out("Default assignments");
            internalMsgCtrl.out("");
            internalMsgCtrl.out("appName = " + appName);
            internalMsgCtrl.out("default_AppFolderPathName = " + default_AppFolderPathName);
            internalMsgCtrl.out("default_InPathName = " + default_InPathName);
            internalMsgCtrl.out("default_OutPathName = " + default_OutPathName);
            internalMsgCtrl.out("--------------------------------------------------");
            return false;
         }

         // Paths ------------------------------------------------------------------------------------

         if (opInPathName != "")
         {
            if (opInPathName.startsWith("~"))
            {
               opInPathName = shared.expandTilde(opInPathName);
            }

            // Find if user provided a file name in the CWD
            // test for cwd
            String cwdStr = shared.getCWDStr();
            if (opInPathName.startsWith(cwdStr))
            {
               opInPathName = shared.expandCWD(opInPathName);
            }

            if (!(fileWork.isAbsolutPath(opInPathName)))
            {
               internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "[" + opInPathName + "] must be an absolute path.");
               throw new InternalFatalError();
            }

            if (fileWork.fileExists(opInPathName) == false)
            {
               internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, true, "File: [" + opInPathName + "] does not exist.");
               throw new InternalFatalError();
            }

            inPathName = opInPathName;
            shared.setInPathName(inPathName);
         }

         if (opOutPathName != "")
         {
            if (opOutPathName.startsWith("~"))
            {
               opOutPathName = shared.expandTilde(opOutPathName);
            }
            // test for cwd
            String cwdStr = shared.getCWDStr();
            if (opOutPathName.startsWith(cwdStr))
            {
               opOutPathName = shared.expandCWD(opOutPathName);
            }

            if (!(fileWork.isAbsolutPath(opOutPathName)))
            {
               internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "[" + opOutPathName + "] must be a path.");
               throw new InternalFatalError();
            }
            outPathName = opOutPathName;
            shared.setOutPathName(outPathName);
         }

         if (genConfigJAVA) { shared.setGenConfigFileJAVAFlg();  }

         if (genConfigXML) { shared.setGenConfigFileXMLFlg(); }


      }
      //catch (CmdLineException clEx)
      catch (Exception exc)
      {
         String errMsg = exc.toString();
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Message, false, "Invalid option.  Use \"-?\" or --help or --optionList to see valid options.");
         // TODO -- work on --verbose... if verbose were true, I really should show this message
         // the exception message will be from args4j, which I don't want the user to see
         // internalMsgCtrl.err(errKey.ExceptionMsg, false, errMsg);
         return false;
      }
      return true;
   }

   private void genConfigZonesHelp()
   {
      internalMsgCtrl.out("Needs to be written.");
   }

   private void genConfigFileHelp()
   {
      // TODO -- low priority -- the literal name genConfigFile and ConfigText.java
      // could be vars from shared (or passed in when genConfigFileFlg was set.
      String configName = shared.getConfigFileName();
      String configTextName = shared.getConfigTextName();
      String configTextSourcePath = shared.getConfigPrepSource();
      internalMsgCtrl.out("-----------------------------------------------");
      internalMsgCtrl.out("genConfigFile can be used two ways:");
      internalMsgCtrl.out("  -gx generate "+ configName + " (via java " + configTextName + ".)");

      internalMsgCtrl.out("  -gj generate " + configTextName + " (from an existing " + configName + ".)");
      internalMsgCtrl.out("(One or the other must be specified.)");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("-i an absolute path to the " + configName + " used to generate " + configTextName + ".");
      internalMsgCtrl.out("-o an absolute path to the " + configName + " file generated");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("Copy the " + configTextName + " file created to the source code location.");
      internalMsgCtrl.out("  " + configTextSourcePath);
      internalMsgCtrl.out("Then rebuild configPrep.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("Options -i and -o:");
      internalMsgCtrl.out("Will expand a leading \"~/\" as Home in Linux");
      internalMsgCtrl.out("Will treat \"./\" or \".\\\" as the current working directory (CWD)");
      internalMsgCtrl.out("Example:  output file in CWD, use \"-o ./filename.xml\"");
      internalMsgCtrl.out("-----------------------------------------------");
   }

   private void genConfigOMXHelp()
   {
      internalMsgCtrl.out("-----------------------------------------------");
      internalMsgCtrl.out("genConfigOMX reads a text file of an expected format, created via a copy/paste");
      internalMsgCtrl.out("of the USPS website: https://postcalc.usps.com/DomesticZoneChart (Denver is 802)");
      internalMsgCtrl.out("There are 4 columns of data with heading \"Zip Code/Zone\"");
      internalMsgCtrl.out("Copy the heading line down to the bottom line (containing zone 999)");
      internalMsgCtrl.out("Do not copy the table ZIP Code/Zone/Specific To (zones 09000---09999 etc)");
      internalMsgCtrl.out("Paste to a file");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("-i (--input) [an absolutPath] to over ride the default");
      internalMsgCtrl.out("    Default input file: [Home]/.BulletinAssembly/omx.txt");
      internalMsgCtrl.out("    Path and file must exist.");
      internalMsgCtrl.out("-o (--output) [an absolutPath] to over ride the default");
      internalMsgCtrl.out("    Default out file: [Home]/.BulletinAssembly/omx.xml");
      internalMsgCtrl.out("    Path and file will be created if does not exist.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("Will expand a leading \"~/\" as Home in Linux");
      internalMsgCtrl.out("Will treat \"./\" or \".\\\" as the current working directory (CWD)");
      internalMsgCtrl.out("Example:  output file in CWD, use \"-o ./filename.xml\"");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("The output xml formatted file is meant to replace <ZONES> in");
      internalMsgCtrl.out("(default name) [home]/.BulletinAssembly/.config.xml");
      internalMsgCtrl.out("-----------------------------------------------");
   }

}

package mainPack;

//191024: Creation

import fileWork.FileWork;
import genConfigFilePack.BuildConfigText_JavaFile;
import libPack.*;

import java.io.IOException;

// Builds a class file ConfigText.java in support of Intellij's BulletinAssembly Project.
// This is a tricky piece of code!  Its not totally QAD; but isn't fancy.
// The ArrayList contentsAL (the contents of the config files) is run through
// and wrapped with the string configStrAL.add (configStrAL is the ArrayList
// of class ConfigText.java file); into the ArrayList classAL (the return from this
// createClass() routine.  the ArrayList classAL is what is run through and written
// out to the file ConfigText.java.
// REMEMBER... this is a tricky routine... careful when you work on it!
// Created week off 191016


public class GenConfigFile_Main
{
   public static void main (String[] args)
   {
      Shared shared = null;
      FileWork fileWork = null;
      InternalMsgCtrl internalMsgCtrl = null;

      String outPathName = "";
      String inPathName = "";
      String configName = "";
      String configTextName = "";

      final String appName = "genConfigFiles";
       try
      {
         // There is ONE and ONLY ONE instance of Shared
         shared = new Shared();
         shared.setAppName(appName);

         configName = shared.getConfigFileName();
         configTextName = shared.getConfigTextName();
         final String default_InFileName =  configName;
         final String default_OutFileName = configTextName;
         shared.setDefault_InFileName(default_InFileName);
         shared.setDefault_ouTFileName(default_OutFileName);

         internalMsgCtrl = shared.getInternalMsgCtrl();
         outPathName = shared.getOutPathName();
         inPathName = shared.getInPathName();
      }
      catch (InternalFatalError err)
      {
         System.out.println("Unable to obtain file names from Shared.");
         System.exit(-1);
      }

       //------------------------------------------

      /*********************************************/
      //-- 1) Read user arguments

      try
      {
         // Argument processing is done with jar args4j and
         // the results are stored in Shared
         CLIOptions cliOptions = shared.getCLIOptions();
         cliOptions.setGenConfigFileFlg();
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
      catch (InternalFatalError exc)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.ExceptionMsg, false, "The reading of argument failed.");
         System.exit(-1);
      }

      // One of these two flags must be set.
      boolean genConfigFileJAVAFlg = shared.getGenConfigFileJAVAFlg();
      boolean genConfigFileXMLFlg = shared.getGenConfigFileXMLFlg();
      if ((genConfigFileJAVAFlg == false) && (genConfigFileXMLFlg == false))
      {
         System.out.println("------------------------------------------------");
         System.out.println("Neither options:");
         System.out.println("   -gj generate " + configTextName);
         System.out.println("   -gx generate " + configName);
         System.out.println("One of the two must be used.");
         System.out.println("------------------------------------------------");
         System.exit(-1);
      }

      /*********************************************/
      // Find the config folder

      if (genConfigFileJAVAFlg)
      {
         // Then we are going to create ConfigText.java
         BuildConfigText_JavaFile buildConfigText_JavaFile = new BuildConfigText_JavaFile(shared);
         try
         {
            buildConfigText_JavaFile.buildJavaFile(outPathName, inPathName);
         }
         catch (InternalFatalError err)
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Unable to write to data file: " + outPathName);
         }
      }
      if (shared.getGenConfigFileXMLFlg())
      {
         String configPath = "";
         try
         {
            ConfigText configText = new ConfigText(shared);
            configPath = configText.writeConfigText();
         }
         catch (InternalFatalError err)
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Unable to write the config file.");
            System.exit(-1);
        }
         internalMsgCtrl.out("A default config file has been written to disk.");
         internalMsgCtrl.out("See:  " + configPath);
         internalMsgCtrl.out("Edit as needed. Then rerun " + appName + " with the -gj option.");
      }
   }
}

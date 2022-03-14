package libPack;

import fileWork.FileWork;
import genConfigOMXPack.ParseOMXInData;
import genConfigZonesPack.ParseZoneInData;
import libPack.InternalMsgCtrl.errKey;
//import mainPack.CLIOptions;
//import mainPack.ParseInData;

public class Shared
{
   public Shared() throws InternalFatalError
   {
      //--------------------------------------------------------
      // Determine the OS
      // Only interested in Linux, Windows or Mac X OS (or newer)
      // Older versions of Mac line break is \r, and is not supported here
      os = determineOS();
      internalMsgCtrl = new InternalMsgCtrl(this);

      // These are messages meant to bin determineOS(); but there is a conflict as to who is need first
      // determineOS() need internalMsgCtrl()  and internalMsgCtrl() need determineOS().
      // So I moved these error messages here (out of determineOS().

      // I thought to use the var lineBreak as set; but can not... need to use a literal, so can be seen.
      if (os.contains("win"))
      {
         internalMsgCtrl.verbose("OS: " + os + "; lineBreak: \"\\r\\n\"; folderBreak:  \"" + folderBreak + "\"");
      } else
      {
         internalMsgCtrl.verbose("OS: " + os + "; lineBreak: \"\\n\"; folderBreak:  \"" + folderBreak + "\"");
      }
      //--------------------------------------------------------

      fileWork = new FileWork(this);
      setDefaultAppFolderPathName();
      appFolderPathName = getAppFolderPathName();
      cliOptions = new CLIOptions(this);
      parseZoneInData = new ParseZoneInData(this);
      parseOMXInData = new ParseOMXInData((this));
   }
   // ------------------------------------------------------

   // These three are a set in GenConfigXXX_Main()
   // with calls to shared.setXXX() below
   private String appName = "";
   private String default_InFileName = "";
   private String default_OutFileName = "";

   public void setAppName(String appName) { this.appName = appName; }
   public void setDefault_InFileName(String default_inFileName)
   {
      this.default_InFileName = default_inFileName;
      setDefaultInPathName();
   }
   public void setDefault_ouTFileName(String default_outFileName)
   {
      this.default_OutFileName = default_outFileName;
      setDefaultOutPathName();
   }

   public String getAppName() {return appName;}
   // ------------------------------------------------------

   private final String default_appFolderName = ".mailPrep";
   private final String default_configFileName = "config.xml";
   private final String default_ConfigTextName = "ConfigText.java";
   private final String configFileName = "";
   private String default_appFolderPathName = "";
   private String default_InPathName = "";  // see setDefaultInPathName()
   private String default_OutPathName = ""; // see setDefaultOutPathName()
   private final String default_configPrepSource = "/work/Intellij/mailSet/configPrep/src/libPack/ConfigText.java";
   //-----------------------------------
   private String appFolderPathName = "";
   private String appFolderName = "";
   private String inFilePathName = "";
   private String outFilePathName = "";

   private String folderBreak = "";
   private String lineBreak = "";
   private String cwdStr = "";
   private String os = "";

   private CLIOptions cliOptions = null;
   private FileWork fileWork = null;
   private InternalMsgCtrl internalMsgCtrl = null;
   private ParseZoneInData parseZoneInData = null;
   private ParseOMXInData parseOMXInData = null;


   public CLIOptions getCLIOptions() {return cliOptions;}

   public FileWork getFileWork() {return fileWork;}

   public InternalMsgCtrl getInternalMsgCtrl() {return internalMsgCtrl;}

   public ParseZoneInData getParseZoneInData() {return parseZoneInData;}
   public ParseOMXInData getParseOMXInData() {return parseOMXInData;}

   // ------------------------------------------------------
   public String getOS() {return this.os;}

   public String determineOS()
   {
      this.os = System.getProperty("os.name").toLowerCase();

      this.lineBreak = System.getProperty("line.separator");
      this.folderBreak = System.getProperty("file.separator");
      this.cwdStr = "." + folderBreak;
      return os;
   }

   public String getFolderBreak() {return folderBreak;}

   public String getLineBreak() {return lineBreak;}

   public String getCWDStr() {return cwdStr;}

   public String getCWD() {return System.getProperty("user.dir");}

   public String getHome() {return System.getProperty("user.home");}

   public String expandTilde(String path)
   {
      int len = path.length();
      String head = path.substring(1, len);
      String expansion = this.getHome() + head;
      return expansion;
   }

   public String expandCWD(String path)
   {
      if (path.startsWith(this.cwdStr))
      {
         path = path.substring(2);
      }

      try
      {
         String cwdPath = new java.io.File(".").getCanonicalPath();
         String tmpPath = cwdPath + folderBreak + path;
         return tmpPath;
      }
      catch (Exception excp)
      {
         return path;
      }
   }

   public String getConfigFileName() { return default_configFileName; }
   public String getConfigTextName() { return default_ConfigTextName; }

   private void setDefaultAppFolderPathName()
   {
      default_appFolderPathName = this.getHome() + folderBreak + default_appFolderName;
      setAppFolderName(default_appFolderName);
   }

   private void setDefaultInPathName()
   {
      default_InPathName = default_appFolderPathName + folderBreak + default_InFileName;
      setInPathName(default_InPathName);
   }

   private void setDefaultOutPathName()
   {
      default_OutPathName = default_appFolderPathName + folderBreak + default_OutFileName;
      setOutPathName(default_OutPathName);
   }

   public String getAppFolderName() {return appFolderName;}

   // The application folder (appFolder) is forced to live in the uses home folder
   public void setAppFolderName(String appFolderName)
   {
      if (appFolderName==null || appFolderName=="")
      {
         internalMsgCtrl.err(errKey.FatalError, true, "Invalid app folder provided.");
      } else if (appFolderName.endsWith(folderBreak))
      {
         // User may have terminated appFolderName with a "/"... if so remove it
         int len = appFolderName.length();
         this.appFolderName = appFolderName.substring(0, len - 1);
      } else
      {
         this.appFolderName = appFolderName;
      }

      // The application folder (appFolder) is forced to live in the uses home folder
      appFolderPathName = this.getHome() + folderBreak + appFolderName;

      internalMsgCtrl.verbose("App folder is set to:  \"" + this.appFolderName + "\"");
      internalMsgCtrl.verbose("App folder path is set to:  \"" + this.appFolderPathName + "\"");
   }

   public String getAppFolderPathName() {return appFolderPathName;}

   public String getInPathName() {return inFilePathName;}

   public void setInPathName(String path) {inFilePathName = path;}

   public String getOutPathName() {return outFilePathName;}

   public void setOutPathName(String path) {outFilePathName = path;}

   public String getConfigFilePathName()
   {
      String configFilePathName = appFolderPathName + folderBreak + default_configFileName;
      return configFilePathName;
   }

   //---------------------------------------------------------------------------
   boolean genConfigFileJAVAFlg = false;
   public void setGenConfigFileJAVAFlg() {genConfigFileJAVAFlg = true;  }
   public boolean getGenConfigFileJAVAFlg() { return genConfigFileJAVAFlg; }

   boolean genConfigFileXMLFlg = false;
   public void setGenConfigFileXMLFlg() { genConfigFileXMLFlg = true;  }
   public boolean getGenConfigFileXMLFlg() { return genConfigFileXMLFlg; }

   public String getConfigPrepSource() { return default_configPrepSource; }
//---------------------------------------------------------------------------
}

package libPack;

/**********************************************
 * ErrorMsgCtrl uses resourcesBundles.ErrorMsgs.properties
 * via ResourceBundle.getBundle() and contained keys
 * CORRECTION:  Originally I did you resourcesBundles.ErrorMsgs.properties;
 * but changed to using the enum.  See the commented out code below.
 *
 * I had System.err.println() and internalMsgCtrl.out() scattered
 * thought the project.  The intent here is to consolidate all
 * of those messages.  Perhaps to one day expand to a log file
 * or some trace debug message.  BUT, at the very least, to
 * get some little control over my scattered messaged.
 * ResoureBundele, at the very least, has made the "Fatal Error"
 * message consistently formatted.
 *
 * I wrote code such that if an errkey.FatalError was active, then this
 * code would throw an InternalFatalError exception.  The chain reaction
 * of that was that every instance of the use of InternalMsgCtrl had to
 * be bracketed by a try/catch and then throw an InternalFatalError exception.
 * That was a LOT of try/catch blocks.  I didn't like it.  It worked; but
 * I didn't like it.  I got rid of that code and changed it.  Now, and
 * call to InternalMsgCtrl that passes an errKey.FatalError, is RESPONSIBLE
 * to throwing a InternalFatalError exception.  That works too.  Makes
 * for easier code, in that every call to InternMsgCtrl does not require
 * try/catch; but is not as protective.  The caller is responsible.  It
 * might have been better to bracket all InternalMsgCtrl calls.  But...
 * Well... this is my code... one person.  I am responsible.
 **********************************************/

// TODO option t/f to append to sb to collect and pass on --verbose option
   //FIXIT -- do the above

public class InternalMsgCtrl
{
   public InternalMsgCtrl(Shared shared)
   {
      this.shared = shared;
      this.lineBreak = shared.getLineBreak();

      // See comment below... not useing resourceBundle
      //resourceBundle = ResourceBundle.getBundle("resourceBundles.ErrorMsgs");
      verboseSB = new StringBuilder();
   }

   private Shared shared = null;
   private String lineBreak;
   //ResourceBundle resourceBundle;

   //----------------------------------------------------
   //https://howtodoinjava.com/java/enum/java-enum-string-example/
   static final String MESSAGE = "Message";

   public enum errKey
   {
      FatalError("Fatal Error"),
      Error("internal Error"),
      ExceptionMsg("Program Exception Message"),
      Message(MESSAGE),
      LineBreak("---------------------------------------");

      private final String msg;

      errKey(String key)
      {
         this.msg = key;
      }

      public String getMsg()
      {
         return msg;
      }
   }
   //----------------------------------------------------

   private StringBuilder verboseSB = null;
   private boolean verboseHandledFlg = false;
   private boolean verboseFlg = false;

   public void setVerboseFlg(boolean verboseFlg)
   {
      this.verboseFlg = verboseFlg;
   }

   public void verbose(String str)
   {
      verboseSB.append(str + lineBreak);
   }

   // Replacement for System.err() to centralize error messages
   // Uses:  internalMsgCtrl.err(errKey.FatalError, msg);
   // Import in caller libPack.ErrorMsgCtrl.errKey
   // obtain eerorMsgCtrl via:  internalMsgCtrl = shared.getErrorMsgCtrl();
   // Make up keys in enum errKey and assign a string to print
   public void err(errKey key, boolean verboseFlg, String str)
   {
      // Verbose flag is a specialized flag, used only at startup.
      // The problem it solves is:  Shared, and OptionsSupport are initialized
      // prior to CLIOptions.  There are a number of messages I want to put out
      // under the option --verbose; but not until verbose is set true.  Thus,
      // I collect them in a stringBuilder until ErrorMsgCtrl.verboseFlg is set.

      if (key == errKey.Message && verboseHandledFlg == false && verboseFlg == true)
      {
         verboseSB.append(str + lineBreak);
         return;
      }

      try
      {
         String errorMsg = key.getMsg();
         System.err.print(errorMsg);
         if ((str != null) || !str.isEmpty())
         {
            System.err.print(":  " + str);
            System.err.println();
         }
      }
      catch (Exception exc)
      {
         System.err.println("Invalid error key:  " + key);
         System.err.println("Unable to print error message.");
      }
   }

   /**********************************************************
    * I choose not to use resourceBundle.
    * resourceBundle is a fancy I don't need.  It is designed for language
    * translation and this is an English only application.
    * The implementation I came up with for resourceBundle showed me how
    * to do the job without using resourceBundle.
   public void err(String key, String str)
   {
      try
      {
         String errorMsg = resourceBundle.getString(key);
         System.err.println(errorMsg);
         if ((str != null) || !str.isEmpty())
         {
            System.err.println("   " + str);
         }
      }
      catch (Exception exc)
      {
         System.err.println("Invalid error key:  " + key);
         internalMsgCtrl.out("   Unable to print error message.");
      }
   }
   ***********************************************************/
   // println
   public void out(String str)
   {
      System.out.println(str);
   }

   // print rather than println
   public void out0(String str)
   {
      System.out.print(str);
   }

   public StringBuilder getVerboseMessages()
   {
      verboseHandledFlg = true;
      return verboseSB;
   }
}

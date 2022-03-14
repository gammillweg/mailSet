package chkZipCountsPack;

import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;


public class Report_ZipCounts
{
   public Report_ZipCounts(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;

   // Read of writing one's state machine with an enum object.
   // I followed this site's example (among many others).
   // https://www.alanfoster.me/posts/implementing-finite-state-machines-using-java-enum/

   private static StringBuilder sb = new StringBuilder();
   private static int index = 0;
   private static int breakCnt = 0;
   private static int errorCnt = 0;
   private static final String delineate = "~~~";
   private static final String doneFlg = "###";
   private static String heading = "";
   private static String errorHeading = "";
   private static final String invalid =   "Invalid:   ";
   private static final String missing =   "Missing:   ";
   private static final String duplicates = "Duplicates: ";
   private static final int zipLen = 5;

   protected enum State
   {
      START {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL)
         {
            heading = errStrAL.get(index);

            // DONE and ERROR are handled outside the enum, to avoid Static restrictions.
            // Check to see if it is indeed a heading string
            // The first 3 characters of the heading are expected to be "---"
            String chk = heading.substring(0, 3);
            // doneFlg is not lead by "---", but is a "heading", so check done BEFORE error in the heading string
            if (heading.equals(doneFlg))
               return DONE;
            if (chk.equals("---") == false)
               return ERROR;

            // The order that we check is:  invalid, missing, and duplicates

            // TODO --- I will have to work our the index flow
            return INVALID;
         }
      },
      INVALID {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {
            index++;
            String tmpStr = errStrAL.get(index);
            if (tmpStr.length() == zipLen)
            {
               errorHeading = invalid;
               return PEEK;
            }
            return MISSING;
         }
      },
      MISSING {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {
            index++;
            String tmpStr = errStrAL.get(index);
            if (tmpStr.length() == zipLen)
            {
               errorHeading = missing;
               return PEEK;
            }
            return DUPLICATES;
         }
      },
      DUPLICATES {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {
            index++;
            String tmpStr = errStrAL.get(index);
            if (tmpStr.length() == zipLen)
            {
               errorHeading = duplicates;
               return PEEK;
            }
            // Start from the top
            index++;
            return START;
         }
      },
      // we are at the
      PEEK {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {
            // peek ahead at the errors
            errorCnt = this.peeker(errStrAL, index);
             // We have to index of the first error and how many errors found plus the header
            return STORE;
         }
      },
      STORE {
         @Override
         protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {
            errorAL.add(heading);
            sb.append(errorHeading);

            int token = index;
            for (int inx = 0; inx < errorCnt; inx++)
            {
               token += inx;
               sb.append(errStrAL.get(token) + ", ");
            }
            String errStr = sb.toString();
            errStr = errStr.substring(0, (errStr.length()-2));
            errorAL.add(errStr);
            index = token + 1;

            sb.delete(0,sb.length());
            switch (errorHeading)
            {
               case invalid:
                  return MISSING;
               case missing:
                  return DUPLICATES;
               case duplicates:
                  index++;
                  return START;
               default: break;
            }

            return START;
         }
      },
      DONE  {
         // We are done collecting.  The state DONE is handled in handleDONE()
         @Override protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) { return HALT;  }
      },
      ERROR {
         // The order sequencing is incorrect, as the heading is not a heading.
         // The state ERROR is handled in handleERROR()
         @Override protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) { return HALT;  }
      },
      HALT{ @Override protected State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL) {return null;} };

      //------------------------------------------------------------
      // The return is: The number of error strings found
      protected int peeker(ArrayList<String> errStrAL, int index)
      {
         String tmp = errStrAL.get(index);
         int counter = 1;
         while (tmp.length() == zipLen)
         {
            tmp = errStrAL.get(index + counter++);
         }
         counter--; // counter stepped one to many
         return counter;
      }

      //------------------------------------------------------------
      // All cases above must implement this abstract
      // Input:  errStrAL is the data process, passed in
      // OutPut: errorAL is the data built during processing, passed out by reference
      protected abstract State execute(ArrayList<String> errStrAL, ArrayList<String> errorAL);
   }

   public ArrayList<String> report_ZipCounts_Main(ArrayList<String> errStrAL)
   {
      ArrayList<String> errorAL = new ArrayList<>();
      ArrayList<String> rtnAL = null;

      State currentState = State.START;
      while(currentState != State.HALT)
      {
         // We catch the state of DONE  and ERROR here, so we can do some work
         // outside the of Static restrictions.
         if (currentState == State.DONE)
         { rtnAL = handleDONE(errorAL); }
         if (currentState == State.ERROR)
         { handleERROR(); }

         currentState = currentState.execute(errStrAL, errorAL);
         //System.out.println(currentState);
      }
      return rtnAL;
   }

   // Outside the enum State simply so I can call none static instance of Shared.
   private ArrayList<String> handleDONE(ArrayList<String> errorAL) {
      String path = shared.getZipCountsFilePathName();
      ArrayList rtnAL = new ArrayList();
      rtnAL.add(0, "-------------------------------------------");
      rtnAL.add("The file:  " + path);
      rtnAL.add("has been checked.");
      rtnAL.add("-----------------");

      if (errorAL.size() == 0) {
         rtnAL.add("No problem were found");
      }
      else {
         rtnAL.add("The following correction(s)l need to be made:  ");
      }
      rtnAL.addAll(errorAL);
      rtnAL.add("-------------------------------------------");
      return rtnAL;
   }

   // Outside the enum State simply so I can call none static instance of Shared.
   private void handleERROR()
   {
       internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false,
            "The generation of an error report failed.");
      internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false,
            "Likely a problem in Validate_ZipCodes.java");
    }
}

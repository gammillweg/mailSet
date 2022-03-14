package genLabelsPack;

import libPack.Addr;
import libPack.ClassesPack;
import libPack.MailPrepUtils;
import libPack.Shared;

import java.util.ArrayList;

public class GenCheckList {

    public GenCheckList(Shared shared) {
        this.shared = shared;
    }
   Shared shared = null;

    public ArrayList<String> genCheckList_Main(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
        // The return is via checkListAL a static class field
        checkListStateMachine(segmentAry, addrBundleAry);
        return checkListAL;
    }

    // The return is via checkListAL a static class field
    private void checkListStateMachine(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
        //Heading
        MailPrepUtils mailPrepUtils = new MailPrepUtils(shared);
        String fourthTueStr = mailPrepUtils.getForthTueStr();

        checkListAL = new ArrayList<>();
        checkListAL.add(fourthTueStr + ",,,,");
        checkListAL.add("CHK,ZIP,CNT,TRAY,");

        State state = State.SEGMENT;
        while (state != State.HALT) {
            state = state.execute(segmentAry, addrBundleAry);
        }
    }

   static ArrayList<String> checkListAL = null;
   static String checkListStr = "";
   static ClassesPack.Segment segment = null;
   static int segmentIndex = -1;
   static int[] zipCntAry = null;
   static int zipCntIndex = 0;
   static int zipCnt = 0;
   static int[] renewalAry = null;
   static boolean renewalFlg = false;
   static int renewalIndex = 0;
   static int addrIndex = 0;
   static int zipCntRenewal = 0;
   static int spinCnt = 0;
   static String activeZip = "";
   static String activeTag = "";

   protected enum State {
        SEGMENT { // a new segment
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                segmentIndex++;
                if (segmentIndex == segmentAry.length) return HALT;
                segment = segmentAry[segmentIndex];
                return INIT_SEGMENT;
            }
        },
       INIT_SEGMENT { // initialize for this segment
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                zipCntAry = segment.zipCntAry;
                renewalAry = segment.renewalAry;
                renewalFlg = (renewalAry == null) ? false : true;
                zipCntIndex = 0;
                renewalIndex = 0;
                addrIndex = segment.startIndex;
                return ZIPCNTS;
            }
        },
        ZIPCNTS { // collect the zipCnt from the zipCntAry
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                if (zipCntIndex == zipCntAry.length) return SEGMENT;
                zipCnt = zipCntAry[zipCntIndex];
                activeTag = segment.trayTag;
                if (zipCnt > 1) {
                    spinCnt = zipCnt;
                    zipCntRenewal = 0;
                    activeZip = addrBundleAry[addrIndex].zip;
                    return SPIN;
                }
                else {
                    activeZip = addrBundleAry[addrIndex].zip;
                    zipCntIndex++;
                    return CURRENT_STR;
                }
            }
        },
        SPIN { // spin through the renewal
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                while (spinCnt > 0) {
                    if (renewalFlg) {
                        if (renewalIndex == renewalAry.length) {
                            addrIndex++;
                            spinCnt--;
                            continue;
                        }
                        if (addrIndex == renewalAry[renewalIndex]) {
                            renewalIndex++;
                            zipCntRenewal++;
                        }
                    }
                    addrIndex++;
                    spinCnt--;
                }
                zipCntIndex++;
                return RENEWAL_STR;
            }
        },
        RENEWAL_STR { // Build the return string (added to the return checkListAL)
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                if (zipCntRenewal > 0) { // build a complex string to show:  total(current|renewals)
                    int diff = zipCnt - zipCntRenewal;
                    checkListStr = "," + activeZip;
                    checkListStr += "," + zipCnt + "(" + diff + "|" + zipCntRenewal + ")";
                    checkListStr += "," + activeTag;
                    checkListAL.add(checkListStr);
                    return ZIPCNTS;
                }
                else { // renewal count is zero, just print the current count
                    return CURRENT_STR;
                }
            }
        },
        CURRENT_STR { // print just the current count
            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                checkListStr = "," + activeZip;
                checkListStr += "," + zipCnt;
                checkListStr += "," + activeTag;
                checkListAL.add(checkListStr);
                return ZIPCNTS;
            }
        },
        HALT { // DONE and out of here

            @Override
            protected State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry) {
                return null;
            }
        };
       //------------------------------------------------------------
       // All cases above must implement this abstract
       protected abstract State execute(ClassesPack.Segment[] segmentAry, Addr[] addrBundleAry);
    }
}


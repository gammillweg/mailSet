package libPack;

public class Addr
{
   public String zip = "";
   public String zone = "";
   public String trayTag = "";
   public String bundle = "";
   public String renewal = "";
   public String csv = "";

   public Addr clone()
   {
      // these are all type String
      Addr addr = new Addr();
      addr.csv = this.csv;
      addr.renewal = this.renewal;
      addr.zip = this.zip;
      addr.zone = this.zone;
      addr.trayTag = this.trayTag;
      addr.bundle = this.bundle;
      return addr;
   }

   public void tr()
   {
      System.out.println("zip:  " + zip + ", zone:  " + zone +
                         ", trayTag:  " + trayTag +
                         ", bundle:  " + bundle +
                         ", renewal: " + renewal);
   }

   public void debugPrintlnLong()
   {
      System.out.println("zip:  " + zip + ", zone:  " + zone +
                        ", trayTag:  " + trayTag +
                        ", bundle:  " + bundle +
                        ", renewal: " + renewal);
     System.out.print("   ");
      System.out.println(csv);
   }
}

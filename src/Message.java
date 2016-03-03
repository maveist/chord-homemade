
public enum Message {
	   IN("in"),
       YOUR_SUCCESSOR("ys"),
	   INSERT_NET("NiceToMeetYou"),
	   CHANGE_PREC("changeprec"),
	   ANS_INSERT_NET("MeToo"),
	   TELL_FINGER("finger");
	   
	   
	   private String msg;
	   
	   private Message(String msg) {
	      this.msg = msg;
	   }
	   public String toString() {
	      return msg;
	   }
	   
}

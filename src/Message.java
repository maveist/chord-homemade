
public enum Message {
	   IN("in"),
       YOUR_SUCCESSOR("ys"),
	   INSERT_NET("NiceToMeetYou"),
	   ANS_INSERT_NET("MeToo"),
	   TELL_FINGER("finger"),
	   ANS_FINGER("imyourfinger"),
	   SIZE_NET("size");
	   
	   
	   private String msg;
	   
	   private Message(String msg) {
	      this.msg = msg;
	   }
	   public String toString() {
	      return msg;
	   }
	   
}

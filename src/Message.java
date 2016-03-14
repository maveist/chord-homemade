
public enum Message {
	   IN("in"),
       YOUR_SUCCESSOR("ys"),
	   INSERT_NET_PRED("NiceToMeetYouPred"),
	   INSERT_NET_SUCC("NiceToMeetYouSucc"),
	   ANS_INSERT_NET("MeToo"),
	   TELL_FINGER("finger"),
	   ANS_FINGER("imyourfinger"),
	   SIZE_NET("size"),
	   WHO_SUCC("ws"),
	   WHO_PRED("wp"),
	   BAD_DISCONNECT("bd"),
	   MESSAGE("msg");
	   
	   
	   private String msg;
	   
	   private Message(String msg) {
	      this.msg = msg;
	   }
	   public String toString() {
	      return msg;
	   }
	   
}

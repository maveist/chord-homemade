
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
	   IAM_SUCC("ims"),
	   WHO_PRED("wp"),
	   IAM_PRED("imp"),
	   BAD_DISCONNECT("bd"),
	   DISCONNECT_PRED("iqPrec"),
	   DISCONNECT_SUCC("iqSucc"),
	   DISCONNECT_TO_WELCOME("a+"),
	   QUIT_NETWORK("quit"),
	   MESSAGE("msg");
	   
	   
	   private String msg;
	   
	   private Message(String msg) {
	      this.msg = msg;
	   }
	   public String toString() {
	      return msg;
	   }
	   
}

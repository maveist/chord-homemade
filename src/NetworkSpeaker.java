import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkSpeaker implements Runnable{
	private String message;
	private String ip;

	
	
	public NetworkSpeaker(String msg, String ip){
		this.message = msg;
		this.ip =ip;

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket sock = new Socket(this.ip, NetworkManager.PEER_PORT);
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			pw.println(this.message);
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}

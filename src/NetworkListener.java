import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class NetworkListener implements Runnable{
	private NetworkManager netManager;
	
	public NetworkListener(NetworkManager net){
		this.netManager = net;
	}
	
	
	//TODO Finir cette méthode.
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(NetworkManager.PEER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Socket sock = servSock.accept();
			InputStream input = sock.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(input));
			String[] msg = read.readLine().split(":");
			switch(msg[0]){
				case "in":
					in(msg);
					break;
				case "NiceToMeetYou":
					niceToMeetYou(msg);
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void in(String[] msg){
		
	}
	
	public void niceToMeetYou(String[] msg){
		this.netManager.searchPlace(Integer.parseInt(msg[1]), msg[2]);
	}

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class MonitorListener implements Runnable{
	private Peer pair;
	
	
	public MonitorListener(Peer pair){
		this.pair = pair;
	}
	
	@Override
	public void run() {
		
			ServerSocket servSock = null;
			try {
				servSock = new ServerSocket(NetworkManager.MONITOR_PORT);
				Socket sock = servSock.accept();
				while(true){
					BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					String message = reader.readLine();
					String[] msg = message.split(":");
					if(msg[0].equals("rt?")){
						ArrayList<String> rt = this.pair.getRouteTable();
						NetworkManager.sendMessage(rt, NetworkManager.MONITOR_IP , sock);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
	}

}

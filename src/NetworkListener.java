import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class NetworkListener implements Runnable{
	private Peer pair;
	
	public NetworkListener(Peer p){
		this.pair = p;
	}
	
	
	//TODO Finir cette méthode.
	@Override
	
	public void run() {
		while(true){
			ServerSocket servSock = null;
			try {
				servSock = new ServerSocket(NetworkManager.PEER_PORT);
			} catch (IOException e) {
	
				e.printStackTrace();
			}
			try {
				Socket sock = servSock.accept();
				InputStream input = sock.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(input));
				String[] msg = read.readLine().split(":");
				int hashCible = Integer.parseInt(msg[1]);
				if(hashCible == this.pair.getHash()){
					switch(msg[0]){
						case "in":
							in(msg);
							break;
						case "NiceToMeetYou":
							niceToMeetYou(msg);
							break;
					}
				}else{
					forwardMessage(msg);
				}
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	
	public void forwardMessage(String[] msg){
		String str = msg[0]; 
		for(int i = 1 ; i < msg.length; i++){
			str = str+":"+msg[i];
		}
		NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
	}
	
	public void in(String[] msg){
		String str ="";
		int hash = Integer.parseInt(msg[1]);
		int hashSucc = this.netManager.getNextHash();
		if(hash < hashSucc){
			try {
				str = Message.INSERT_NET.toString()+":"+Integer.toString(this.pair.getHashSuccesseur())+":"+this.netManager.getIpNext();
				Socket sock = this.netManager.getSockNext();
				PrintWriter pw = new PrintWriter(sock.getOutputStream());
				pw.println(str);
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.netManager.changeSucc(msg[2], Integer.parseInt(msg[1]));
	}
	
	public void niceToMeetYou(String[] msg){
		this.netManager.changeSucc(msg[1], Integer.parseInt(msg[2]));
	}

}

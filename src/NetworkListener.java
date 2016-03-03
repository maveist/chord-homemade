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
				String bufmsg = read.readLine();
				System.out.println(bufmsg);
				String[] msg = bufmsg.split(":");
			
				if(msg[0].equals(Message.SIZE_NET.toString())){
					forwardSize(msg);
				}
				int hashCible = Integer.parseInt(msg[1]);
				if(hashCible == this.pair.getHash()){
					switch(msg[0]){
						case "in":
							in(msg);
							break;
						case "NiceToMeetYou":
							niceToMeetYou(msg);
							break;
						case "yaf":
							this.pair.changeSuccesseur(this.pair.getIp(), this.pair.getHash());
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
	
	public void forwardSize(String[] msg){
		int currentSize = Integer.parseInt(msg[2]);
		String str = msg[0]+":"+msg[1]+":"+Integer.toString(currentSize);
		NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
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
		int hashSucc = this.pair.getHashSuccesseur();
		if(hash < hashSucc){
			try {
				str = Message.INSERT_NET.toString()+":"+Integer.toString(this.pair.getHashSuccesseur())+":"+this.pair.getIpSuccesseur();
				Socket sock = new Socket(this.pair.getIpSuccesseur(), NetworkManager.PEER_PORT);
				PrintWriter pw = new PrintWriter(sock.getOutputStream());
				pw.println(str);
				pw.close();
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.pair.changeSuccesseur(msg[2], Integer.parseInt(msg[1]));
	}
	
	public void niceToMeetYou(String[] msg){
		this.pair.changeSuccesseur(msg[1], Integer.parseInt(msg[2]));
	}

}

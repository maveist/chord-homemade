import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkSpeaker implements Runnable{
	private String message;
	private String ip;
	private ArrayList<String> messages;
	private boolean severalMessage;
	private Socket socket;
	
	public NetworkSpeaker(String msg, String ip){
		this.message = msg;
		this.ip =ip;
		this.severalMessage = false;
	}
	
	public NetworkSpeaker(ArrayList<String> msg, String ip){
		this.severalMessage = true;
		this.messages = msg;
		this.ip = ip;
	}
	
	public NetworkSpeaker(ArrayList<String> msg, String ip, Socket sock){
		this(msg, ip);
		this.socket = sock;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.severalMessage){
			this.sendSeveralMessage();
		}else{
			this.sendOneMessage();
		}
		
	}
	
	
	public void sendOneMessage(){
		try {
			PrintWriter pw;
			Socket sock;
			if(this.socket == null){ //vérifie si on a spécifié un socket à utiliser
				sock = new Socket(this.ip, NetworkManager.PEER_PORT);
				pw = new PrintWriter(sock.getOutputStream(), true);
			}else{
				sock = this.socket;
				pw = new PrintWriter(sock.getOutputStream(), true);
			}
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
	
	public void sendSeveralMessage(){
		try {
			PrintWriter pw;
			Socket sock;
			if(this.socket == null){//vérifie si on a spécifié un socket à utiliser
				sock = new Socket(this.ip, NetworkManager.PEER_PORT);
				pw = new PrintWriter(sock.getOutputStream(), true);
			}else{
				sock = this.socket;
				pw = new PrintWriter(sock.getOutputStream(), true);
			}
			for(String msg : this.messages){
				pw.println(msg);
			}
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

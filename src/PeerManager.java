

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PeerManager {
	private String ipPrec;
	private String ipNext;
	private int port;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private String myip;
	
	public PeerManager(String ip, int port, String ipSucc){
		this.myip = ip;
		this.port = port;
		this.ipNext = ipSucc;
		try{
			this.socket = new Socket(ipSucc, port);
			this.input = this.socket.getInputStream();
			this.output = this.socket.getOutputStream();
			//this.connexion();
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		this.getInNetwork();
	}
	
	public void getInNetwork(){
		//String str = "in:"+Integer.toString(hash)+":"+this.myip;
		PrintWriter sortie = new PrintWriter(this.output , true ) ;
		//sortie.println(msg);
	}
	
	public String getIpPrec() {
		return ipPrec;
	}
	public void setIpPrec(String ipPrec) {
		this.ipPrec = ipPrec;
	}
	public String getIpNext() {
		return ipNext;
	}
	public void setIpNext(String ipNext) {
		this.ipNext = ipNext;
	}
	
	
	
}

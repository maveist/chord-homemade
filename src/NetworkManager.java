

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkManager {
	public static int PEER_PORT = 8000;
	
	private int hashNext;
	private String ipNext;
	private Socket sockNext;
	

	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	private String myip;
	private int myHash;
	
	private Thread netListener;
	
	
	
	/*TODO Refaire l'archi.
		un socket écouteur (successeur)
		un socket parleur (predecesseur)
		HashMap de socket (fingers)
	*/
	/*public NetworkManager(String ip, String ipSucc, int hash){
		this.myHash = hash;
		this.myip = ip;
		this.ipNext = ipSucc;
		try{
			this.socket = new Socket(ipSucc, PEER_PORT);
			this.input = this.socket.getInputStream();
			this.output = this.socket.getOutputStream();
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		// Ne pas l'appeler depuis le constructeur car on a besoin de l'ip du WelcomeServer et port
		// On va probablement l'appeler depuis le main.
		//this.getInNetwork();
	}*/
	

	
	public static void getInNetwork(String ipWelcome, int portWelcome){
		//Communication avec le WelcomeServeur
				Socket sock;
				String ipToContact = "";
				String strIn = "";
				PrintWriter sortie;
				OutputStream output;
				try {
					sock = new Socket(ipWelcome, portWelcome);
					OutputStream outToWelcome = sock.getOutputStream();
					InputStream inWelcome = sock.getInputStream();
					PrintWriter toWelcome = new PrintWriter(outToWelcome, true);
					toWelcome.print("yo:"+Integer.toString(this.myHash)+":"+this.myip);
					
					BufferedReader readWelcome = new BufferedReader(new InputStreamReader(inWelcome));
					strIn = readWelcome.readLine();
					ipToContact = strIn;
					sock.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// Communication avec l'ip d'entrée donnée par WelcomeServer
				try {
					sock = new Socket(ipToContact, PEER_PORT);
					output = sock.getOutputStream();
					String str = "in:"+Integer.toString(this.myHash)+":"+this.myip;
					sortie = new PrintWriter(output , true ) ;
					sortie.println(str);
					sortie.close();
					output.close();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//La méthode se termine, on créée un thread pour écouter les messages 
				//et ainsi avoir des réponses pour avoir le successeur.
				NetworkListener nl = new NetworkListener(this);
				this.netListener = new Thread(nl);
				this.netListener.start();
				
	}
	
	
	//Cette méthode envoie un msg à son successeur lorsqu'il y a insertion de machine
	public static changeSucc(String ip, int hash){
		try {
			String str = Message.CHANGE_PREC.toString() + ":" + hash +":"+ ip;
			PrintWriter pw = new PrintWriter(this.sockNext.getOutputStream());
			pw.close();
			this.sockNext.close();
			this.sockNext = new Socket(ip, PEER_PORT);
			this.hashNext = hash;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
		
	public static void sendMessage(String msg, String ip){
		try {
			OutputStream output = this.sockNext.getOutputStream();
			PrintWriter pw = new PrintWriter(output);
			pw.println(msg);
			pw.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

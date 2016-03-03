

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkManager {
	// Hash server infos
	private static String hashServerIp;
	private static int hashServerPort;
	
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
	public NetworkManager(String ip, String ipSucc, int hash){		
		this.myHash = hash;
		this.myip = ip;
		this.ipNext = ipSucc;
		try{
			this.socket = new Socket(ipSucc, PEER_PORT);
			this.input = this.socket.getInputStream();
			this.output = this.socket.getOutputStream();
			//this.connexion();
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		// Ne pas l'appeler depuis le constructeur car on a besoin de l'ip du WelcomeServer et port
		// On va probablement l'appeler depuis le main.
		//this.getInNetwork();
	}
	
	/*public void getInNetwork(String ipWelcome, int portWelcome){
		//Communication avec le WelcomeServeur
		Socket sockWelcome;
		String ipToContact = "";
		String strIn = "";
		PrintWriter sortie;
		OutputStream output;
		try {
			sockWelcome = new Socket(ipWelcome, portWelcome);
			OutputStream outToWelcome = sockWelcome.getOutputStream();
			InputStream inWelcome = sockWelcome.getInputStream();
			PrintWriter toWelcome = new PrintWriter(outToWelcome, true);
			toWelcome.print("yo:"+Integer.toString(this.myHash)+":"+this.myip);
			
			BufferedReader readWelcome = new BufferedReader(new InputStreamReader(inWelcome));
			strIn = readWelcome.readLine();
			ipToContact = strIn;
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		// Communication avec l'ip d'entrée donnée par WelcomeServer
		Socket sock;
		try {
			sock = new Socket(ipToContact, PEER_PORT);
			output = sock.getOutputStream();
			String str = "in:"+Integer.toString(this.myHash)+":"+this.myip;
			sortie = new PrintWriter(output , true ) ;
			sortie.println(str);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		
		
		//Reception de l'identité du predecesseur
		BufferedReader entree = new BufferedReader(new InputStreamReader(this.input));
		String in;
		
		try {
			in = entree.readLine();
			String[] msg = in.split(":");
			if(msg[0].equals("go")){
				
				 
				 String msgEnter = "NiceToMeetYou";		
				 this.hashNext = Integer.parseInt(msg[1]);
				 this.ipNext = msg[2];
				 sock = new Socket(this.ipNext, PEER_PORT);
				 OutputStream out = sock.getOutputStream();
				 sortie = new PrintWriter(out, true);
				 sortie.print(msgEnter);
				 
				 //Ecoute du prédecesseur pour savoir le successeur
				 InputStream precInput = sock.getInputStream();
				 BufferedReader precToMe = new BufferedReader(new InputStreamReader(precInput));
				 in=precToMe.readLine();
				 String[] dataSucc = in.split(":");
				 if(dataSucc[0].equals("ys")){ // ys == Your Successor. Regex => ys:hashSucc:IpSucc
					 this.hashNext = Integer.parseInt(dataSucc[1]);
					 this.ipNext = dataSucc[2];
				 }
				 
				 
				 sock.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	
	// ---------------------------
	// GESTION DU HASH DU PAIR
	// ---------------------------
	
	public static void setHashServerIp(String ip){
		NetworkManager.hashServerIp = ip;
	}
	
	public static void setHashServerPort(int port){
		NetworkManager.hashServerPort = port;
	}
	
	public static int getHashFromServer(String peerIp){
		try {
			// Création du canal de communication
			Socket hashSock;
			hashSock = new Socket(NetworkManager.hashServerIp, NetworkManager.hashServerPort);
			InputStream fluxEntree = hashSock.getInputStream();
			OutputStream fluxSortie = hashSock.getOutputStream();
			BufferedReader entree = new BufferedReader(new InputStreamReader(fluxEntree));
			PrintWriter sortie = new PrintWriter(fluxSortie, true);
			
			// Récupération du hash via le canal et renvoi
			sortie.println(peerIp);
			String hash = entree.readLine();
			return Integer.parseInt(hash);
		} catch (IOException e) {
			return -1;
		}
	}
	
	public void getInNetwork(String ipWelcome, int portWelcome){
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
					sortie = new PrintWriter(output , true );
					sortie.println(str);
					sortie.close();
					output.close();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				NetworkListener nl = new NetworkListener(this);
				this.netListener = new Thread(nl);
				this.netListener.start();
				
	}
	
	//TODO Modifier cette méthode en ajoutant la gestion des fingers
	public void searchPlace(int hash, String ip){
		if(hash > this.hashNext){
			try {
				String str = Message.INSERT_NET.toString()+Integer.toString(hash)+":"+ip;
				OutputStream output = this.sockNext.getOutputStream();
				PrintWriter pw = new PrintWriter(output, true);
				pw.println(str);
				output.close();
				pw.close();
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
		}else{
			try {
				Socket sock = new Socket(ip, PEER_PORT);
				String str = Message.ANS_INSERT_NET.toString()+Integer.toString(this.myHash)+":"+this.myip;
				OutputStream output = sock.getOutputStream();
				PrintWriter pw = new PrintWriter(output, true);
				pw.println(str);
				pw.close();
				output.close();
				sock.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	//Cette méthode envoie un msg à son successeur lorsqu'il y a insertion de machine
	public void changeSucc(String ip, int hash){
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
		
	public void sendMessage(String msg){
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
	
	public String getIpNext() {
		return ipNext;
	}
	public void setIpNext(String ipNext) {
		this.ipNext = ipNext;
	}
	
	public int getNextHash(){
		return this.hashNext;
	}
	
	public int getHash(){
		return this.myHash;
	}
	public Socket getSockNext(){
		return this.sockNext;
	}
}

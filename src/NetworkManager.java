

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
	
	
	private Thread netListener;
	
	
	
	/*TODO Refaire l'archi.
		un socket écouteur (successeur)
		un socket parleur (predecesseur)
		HashMap de socket (fingers)
	*/
/*
	public NetworkManager(String ip, String ipSucc, int hash){		

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
	

	
	// ---------------------------
	// GESTION DU HASH DU PAIR
	// ---------------------------
	
	public static void setPeerPort(int port){
		PEER_PORT = port;
	}
	
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
	
	public void getInNetwork(String ipWelcome, int portWelcome, Peer pair){
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
					toWelcome.print("yo:"+Integer.toString(pair.getHash())+":"+pair.getIp);
					
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
					String str = "in:"+Integer.toString(pair.getHash())+":"+pair.getIp();
					sortie = new PrintWriter(output , true );
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
	
	
	
		
	public static void sendMessage(String msg, String ip){
		try {
			Socket sock = new Socket(ip, PEER_PORT);
			OutputStream output = sock.getOutputStream();
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



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkManager {
	// Hash server infos
	private static String hashServerIp;
	private static int hashServerPort;
	public static String MONITOR_IP;	
	public static String WELCOME_IP;
	
	public static int PEER_PORT = 8005;
	public static int MONITOR_PORT = 8002;
	public static int WELCOME_PORT = 8000;
	
	
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
	
	public static void setMonitor(String ip, int port){
		MONITOR_IP = ip;
		MONITOR_PORT = port;
	}
	
	public static void setWelcome(String ip, int port){
		WELCOME_IP = ip;
		WELCOME_PORT = port;
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
	
	public static void getInNetwork(String ipWelcome, int portWelcome, Peer pair){
		//Communication avec le WelcomeServeur
				Socket sock;
				String ipToContact = "";
				String strIn = "";
				PrintWriter sortie;
				OutputStream output;
				try {
					System.out.println("Connexion au WelcomeServer...");
					sock = new Socket(ipWelcome, portWelcome);
					OutputStream outToWelcome = sock.getOutputStream();
					//InputStream inWelcome = sock.getInputStream();
					PrintWriter toWelcome = new PrintWriter(outToWelcome, true);
					System.out.println("yo:"+Integer.toString(pair.getHash())+":"+pair.getIp());
					toWelcome.println("yo:"+Integer.toString(pair.getHash())+":"+pair.getIp()+"\n");
					System.out.println("Attente de réponse du serveur");
					BufferedReader readWelcome = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					strIn = readWelcome.readLine();
					System.out.println(strIn);
					System.out.println("Réponse ok.");
					ipToContact = strIn;
					sock.close();
					
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(!ipToContact.equals("yaf")){
				// Communication avec l'ip d'entrée donnée par WelcomeServer
					try {
						sock = new Socket(ipToContact, PEER_PORT);
						output = sock.getOutputStream();
						String str = "in:"+Integer.toString(pair.getHash())+":"+pair.getIp();
						sortie = new PrintWriter(output , true);
						sortie.println(str);
						sortie.close();
						output.close();
						sock.close();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else{
					pair.setIpSuccesseur(pair.getIp());
					pair.setHashSuccesseur(pair.getHashSuccesseur());
				}
				
				//La méthode se termine, on créée un thread pour écouter les messages sur le réseau
				//et ainsi avoir des réponses pour avoir le successeur.
				System.out.println("Connexion ok.");
				
				NetworkListener nl = new NetworkListener(pair);
				Thread netListener = new Thread(nl);
				netListener.start();
				Thread thMonitor = new Thread(new MonitorListener(pair));
				thMonitor.run();
				//Thread entreeTh = new Thread(new EntreeThread(pair));
				//entreeTh.run();
				//pair.setFinger();
				
	}

	
		
	public static void sendMessage(String msg, String ip) throws IOException{
		/*Thread th = new Thread(new NetworkSpeaker(msg, ip));
		th.run();*/
		System.out.println("Envoi du message: "+msg+" à :"+ip);
		try {
			Socket sock = new Socket(ip, PEER_PORT);
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			pw.println(msg);
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void sendMessage(ArrayList<String> msgs, String ip){
		/*read th = new Thread(new NetworkSpeaker(msgs, ip));
		th.run();*/
		System.out.println("Envoi de messages: "+msgs);
		try {
			Socket sock = new Socket(ip, PEER_PORT);
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			for(String msg : msgs){
				pw.println(msg);
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
	
	public static void sendMessage(ArrayList<String> msgs, String ip, Socket sock){
		/*Thread th = new Thread(new NetworkSpeaker(msgs,ip,sock));
		th.run();*/
		System.out.println("Envoi de messages +socket: "+msgs);
		try {
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			for(String msg : msgs){
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
	
	public static void sendMessage(String msg, Socket sock){
		try {
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			pw.println(msg);
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static int sizeOfNetwork(){
		int size = -1;
		try {
			Socket sock = new Socket(WELCOME_IP, WELCOME_PORT);
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			pw.println("len?");
			String msg = br.readLine();
			String[] lenMsg = msg.split(":");
			size = Integer.parseInt(lenMsg[1]);
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}
	
	public static void disconnect(Peer pair){
		
		 
		try {
			String str = Message.DISCONNECT_TO_WELCOME.toString()+":"+pair.getHash();
			Socket sock = new Socket(WELCOME_IP, WELCOME_PORT);
			String strToPrec = Message.DISCONNECT_PRED.toString()+":"+pair.getHashSuccesseur()+":"+pair.getIpSuccesseur();
			String strToSucc = Message.DISCONNECT_SUCC.toString()+":"+pair.getHashPredecesseur()+":"+pair.getIpPredecesseur();
			NetworkManager.sendMessage(str, sock);
			NetworkManager.sendMessage(strToPrec, pair.getIpPredecesseur());
			NetworkManager.sendMessage(strToSucc, pair.getIpSuccesseur());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

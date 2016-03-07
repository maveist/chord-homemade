

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkManager {
	// Hash server infos
	private static String hashServerIp;
	private static int hashServerPort;
		
	public static int PEER_PORT = 8005;
	
	
	
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
				
	}
	
	
	
		
	public static void sendMessage(String msg, String ip){
		Thread th = new Thread(new NetworkSpeaker(msg, ip));
		th.run();	
	}
	
	
	//TODO à tester si le NetworkListener ne prend pas le dessus lors de la réception du message.
	public static int sizeOfNetwork(int myHash, String ipSuccesseur){
		int size = -1;
		try {
			Socket sock = new Socket(ipSuccesseur, PEER_PORT);
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			String str = Message.SIZE_NET.toString()+":"+ Integer.toString(myHash);
			pw.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try{
			ServerSocket sockListen = new ServerSocket(NetworkManager.PEER_PORT);
			Socket sock = sockListen.accept();
			InputStream input = sock.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(input));
			String[] received = read.readLine().split(":");
			size = Integer.parseInt(received[2]); //received[2] => endroit où se trouve le nb de jump du message.
		}catch(IOException e){
			e.printStackTrace();
		}
		return size;
	}
}

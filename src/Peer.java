import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



public class Peer {
	private static String hashServerIp;
	private static int hashServerPort;
	private String Ip;
	private String hash;
	
	public Peer(){
		try {
			// Récupération de l'IP puis du Hash
			this.Ip = InetAddress.getLocalHost().getHostAddress();
			
			// Temporaire - Récupération du hash
			Socket hashSock = new Socket(Peer.hashServerIp, Peer.hashServerPort);
			InputStream fluxEntree = hashSock.getInputStream();
			OutputStream fluxSortie = hashSock.getOutputStream();
			BufferedReader entree = new BufferedReader(new InputStreamReader(fluxEntree));
			PrintWriter sortie = new PrintWriter(fluxSortie, true);
			
			sortie.println(this.Ip);
			this.hash = entree.readLine();
			System.out.println(this.hash);
		} catch (UnknownHostException e) {
			System.out.println("Erreur de récupération de l'adresse IP.");
		} catch (IOException e) {
			System.out.println("Erreur de communication en entrée/sortie.");
		}
	}
	
	public static void setHashServerIp(String Ip){
		Peer.hashServerIp = Ip;
	}
	
	public static void setHashServerPort(int port){
		Peer.hashServerPort = port;
	}
}

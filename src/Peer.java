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
	private String ip;
	private int hash;
	private int hashSuccesseur;
	private String ipSuccesseur;
	
	
	
	public Peer(){
		// Récupération de l'IP puis du Hash
		try {
			this.ip = InetAddress.getLocalHost().getHostAddress();
			this.hash = NetworkManager.getHashFromServer(this.ip);
			
			
			System.out.print(this.ip + " => " + this.hash);
		} catch (UnknownHostException e) {
			System.out.println("Erreur dans la récupération de votre IP.");
		}
	}
	
	public void changeSuccesseur(String ip, int hash){
		this.ipSuccesseur = ip;
		this.hashSuccesseur = hash;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getHashSuccesseur() {
		return hashSuccesseur;
	}

	public void setHashSuccesseur(int hashSuccesseur) {
		this.hashSuccesseur = hashSuccesseur;
	}

	public String getIpSuccesseur() {
		return ipSuccesseur;
	}

	public void setIpSuccesseur(String ipSuccesseur) {
		this.ipSuccesseur = ipSuccesseur;
	}
	
	

	
}

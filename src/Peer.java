import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;


public class Peer {
	private String ip;
	private int hash;
	private int hashSuccesseur;
	private String ipSuccesseur;
	private HashMap<Integer, String> fingers;
	
	
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
	
	public void addFinger(int hash, String ip){
		this.fingers.put(hash, ip);
	}
	
	public Set<Integer> getFingersHashes(){
		return this.fingers.keySet();
	}
	
	public String getFingerIp(int hash){
		return this.fingers.get(hash);
	}
	
	public int getFingerHash(String ip){
		int hash = -1;
		
		for(int hashCourant : this.fingers.keySet()){
			if(this.fingers.get(hashCourant) == ip)
				hash = hashCourant;
		}
		
		return hash;
	}
	
	public int getClosestFinger(int seekedHash){
		int networkSize = NetworkManager.sizeOfNetwork(this.hash, this.ipSuccesseur);
		int finalHash = -1;
		seekedHash = this.hashModulo(this.hash, seekedHash, networkSize);
		
		// On parcours les finger (on prend le soins de réajuster les hash avec le modulo).
		for(int fingerHash : this.fingers.keySet()){
			fingerHash = this.hashModulo(this.hash, fingerHash, networkSize);
			
			if(fingerHash < seekedHash)
				finalHash = fingerHash;
			else
				break;
		}
				
		return this.hashModuloInverse(this.hash, finalHash, networkSize);
	}

	private int hashModulo(int hashDepart, int hashCourant, int networkSize){
		if(hashCourant < hashDepart)
			return (networkSize % hashDepart) + (hashCourant%hashDepart);
		else
			return (hashCourant%hashDepart);
	}
	
	private int hashModuloInverse(int hashDepart, int hashModulo, int networkSize){
		if(hashModulo > (networkSize % hashDepart))
			return hashModulo - (networkSize % hashDepart);
		else
			return hashDepart + hashModulo;
	}
}

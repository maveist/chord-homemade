import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Peer {
	private String ip;
	private int hash;
	
	private int hashSuccesseur;
	private String ipSuccesseur;
	
	private int hashPredecesseur;
	private String ipPredecesseur; 
	
	private HashMap<Integer, String> fingers;
	
	
	public Peer(){
		// Récupération de l'IP puis du Hash
		try {
			this.ip = InetAddress.getLocalHost().getHostAddress();
			this.hash = NetworkManager.getHashFromServer(this.ip);
			
			
			System.out.println(this.ip + " => " + this.hash);
		} catch (UnknownHostException e) {
			System.out.println("Erreur dans la récupération de votre IP.");
		}
	}
	
	public Peer(String ip){
		this.ip = ip;
		this.hash = NetworkManager.getHashFromServer(this.ip);
		
		
		System.out.println(this.ip + " => " + this.hash);
	}
	
	public boolean haveSuccesseur(){
		return this.ip != this.ipSuccesseur;
	}
	
	public boolean havePredecesseur(){
		return this.ip != this.ipPredecesseur;
	}
	
	public void changeSuccesseur(String ip, int hash){
		this.ipSuccesseur = ip;
		this.hashSuccesseur = hash;
	}
	
	public void changePredecesseur(String ip, int hash){
		this.ipPredecesseur = ip;
		this.hashPredecesseur = hash;
	}
	
	
	
	public int getHashPredecesseur() {
		return hashPredecesseur;
	}

	public void setHashPredecesseur(int hashPredecesseur) {
		this.hashPredecesseur = hashPredecesseur;
	}

	public String getIpPredecesseur() {
		return ipPredecesseur;
	}

	public void setIpPredecesseur(String ipPredecesseur) {
		this.ipPredecesseur = ipPredecesseur;
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
	
	public ArrayList<String> getRouteTable(){
		ArrayList<String> rt = new ArrayList<>();
		String myHashString =Integer.toString(this.hash);
		String tmp = myHashString +":"+Integer.toString(this.hashSuccesseur)+":"+this.ipSuccesseur;
		rt.add(tmp);
		if(this.fingers != null && !this.fingers.isEmpty()){
			for(int hashS : this.fingers.keySet()){
				String ipS = this.fingers.get(hashS);
				tmp = myHashString+":"+Integer.toString(hashS)+":"+ipS;
				rt.add(tmp);
			}
			
		}	
		rt.add("end");
		return rt;
	}
	
	
	public void setFinger(){
		int size = NetworkManager.sizeOfNetwork();
		System.out.println(size);
	}
	
	public void sendMessage(int targetedHash, String msg){
		//TODO à peaufiner
		if(targetedHash < this.hash){
			NetworkManager.sendMessage(msg, this.ipPredecesseur);
		}else{
			NetworkManager.sendMessage(msg, this.ipSuccesseur);
		}
	}
	
	public int getClosestFinger(int seekedHash){
		int networkSize = NetworkManager.sizeOfNetwork();
		int finalHash = -1;
		seekedHash = Peer.hashModulo(this.hash, seekedHash, networkSize);
		
		// On parcours les finger (on prend le soins de réajuster les hash avec le modulo).
		for(int fingerHash : this.fingers.keySet()){
			fingerHash = Peer.hashModulo(this.hash, fingerHash, networkSize);

			if(fingerHash < seekedHash)
				finalHash = fingerHash;
			else
				break;
		}
				
		return Peer.hashModuloInverse(this.hash, finalHash, networkSize);
	}

	public static int hashModulo(int hashDepart, int hashCourant, int networkSize){
		if(hashCourant < hashDepart)
			return (networkSize % hashDepart) + (hashCourant%hashDepart);
		else
			return (hashCourant%hashDepart);
	}
	
	public static int hashModuloInverse(int hashDepart, int hashModulo, int networkSize){
		if(hashModulo > (networkSize % hashDepart))
			return hashModulo - (networkSize % hashDepart);
		else
			return hashDepart + hashModulo;
	}
	
}

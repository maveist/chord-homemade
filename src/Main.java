import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String ipServ ="";
		int portWelcome = 8000;
		System.out.println("Adresse ip de serveur welcome/hash/moniteur");
		try {
			ipServ = bufferRead.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NetworkManager.setHashServerIp(ipServ);
		NetworkManager.setHashServerPort(8001);
		NetworkManager.setMonitor(ipServ, 8002);
		NetworkManager.setWelcome(ipServ, 8000);
		Peer monPair = new Peer("192.168.1.36");
		System.out.println("IP: "+ monPair.getIp()+" HASH: "+monPair.getHash());
		NetworkManager.getInNetwork(ipServ, portWelcome, monPair);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//TODO vérifier si le destinataire du message existe bien.
		//TODO vérifier si le dest du waiting message existe encore.
		while(true){
			try {
				System.out.println("Ecrire un message:");
				String msg = br.readLine();
				if(msg.equals("quit")){
					System.out.println("déconnection.");
					return;
				}else{
					System.out.println("Envoyer à qui? (valeur de hash):");
					String dest = br.readLine();
					try {
						monPair.sendMessage(Integer.parseInt(dest), msg);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

}

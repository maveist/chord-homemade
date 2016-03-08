import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//CETTE CLASSE EST UTILE SEULEMENT SI ON UTILISE UN PORT DEDIÉ À LA COMMUNICATION DE MESSAGE SIMPLE
public class MessageListener implements Runnable{
	private Peer pair;
	
	public MessageListener(Peer pair){
		this.pair = pair;
	}
	
	//TODO à revoir si on fait utilise un port dédié pour le transfert de message et un port dédié pour
	// le transfert de message pour la gestion du réseau
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			ServerSocket servSock = null;
			try {
				servSock = new ServerSocket(NetworkManager.PEER_PORT); //DEFINIR MESSAGE PORT
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				Socket sock = servSock.accept();
				InputStream input = sock.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(input));
				String bufmsg = read.readLine();
				sock.close();
				servSock.close();
				System.out.println(bufmsg);
				String[] msg = bufmsg.split(":");
				int hashDest = Integer.parseInt(msg[1]);
				if(hashDest == this.pair.getHash()){
					System.out.println(msg[2]); //msg[2] => texte contenu dans le message
												//TODO faire une méthode propre
				}else{
					forwardMessage(msg);
				}
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	
	public void forwardMessage(String[] msg){
		String str = msg[0]; 
		for(int i = 1 ; i < msg.length; i++){
			str = str+":"+msg[i];
		}
		NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
	}

}

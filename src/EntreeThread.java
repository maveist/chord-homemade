import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class EntreeThread implements Runnable{
	private Peer pair;
	
	public EntreeThread(Peer peer){
		this.pair = peer;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Entrez le message à envoyer:");
				String msg = reader.readLine();
				if(!msg.equals("quit")){
					System.out.println("Envoyer à qui? (spécifiez le numéro de hash):");
					int hash = Integer.parseInt(reader.readLine());
					try {
						this.pair.sendMessage(hash, msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					NetworkManager.disconnect(this.pair);
					System.out.println("déconnection.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}

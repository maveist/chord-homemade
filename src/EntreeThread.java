import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class EntreeThread implements Runnable{
	private Peer pair;
	
	public EntreeThread(/*String ipServ, int port,*/ Peer peer){
		this.pair = peer;
		//NetworkManager.getInNetwork(ipServ, port, peer);
	}
	
	@Override
	
	public  void run() {
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
						String toSend = Message.MESSAGE.toString()+":"+Integer.toString(hash)+":"+msg;
						this.pair.sendMessage(hash, toSend);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					NetworkManager.disconnect(this.pair);
					System.out.println("déconnection.");
					System.exit(0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class NetworkListener implements Runnable{
	private Peer pair;

	public NetworkListener(Peer p){
		this.pair = p;
	}
	
	
	@Override
	public void run() {
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(NetworkManager.PEER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			
			try {
				Socket sock = servSock.accept();
				InputStream input = sock.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(input));
				String bufmsg = read.readLine();
				sock.close();
				System.out.println("message reçu: "+bufmsg);
				if(bufmsg != null){
					String[] msg = bufmsg.split(":");
				
					
					int hashCible = Integer.parseInt(msg[1]);
					
						switch(msg[0]){
							case "in":
								in(msg);
								break;
							case "NiceToMeetYou":
								niceToMeetYou(msg);
								break;
							case "yaf":
								this.pair.changeSuccesseur(this.pair.getIp(), this.pair.getHash());
								break;
						}
				}
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}

	}
	
	public void forwardSize(String[] msg){
		int currentSize = Integer.parseInt(msg[2]);
		String str = msg[0]+":"+msg[1]+":"+Integer.toString(currentSize);
		NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
	}
	
	public void forwardMessage(String[] msg){
		String str = msg[0]; 
		for(int i = 1 ; i < msg.length; i++){
			str = str+":"+msg[i];
		}
		System.out.println("ip succ:"+this.pair.getIpSuccesseur());
		NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
	}
	
	public void in(String[] msg){
		String str ="";
		int hash = Integer.parseInt(msg[1]);
		int hashSucc = this.pair.getHashSuccesseur();
		//Pour savoir si le pair n'a que lui comme successeur soit qu'il est tt seul.
		if(this.pair.getIp().equals(this.pair.getIpSuccesseur())){
			System.out.println("passage dans la condition 1");
			this.pair.changeSuccesseur(msg[2], hash);
			str = Message.INSERT_NET.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
			NetworkManager.sendMessage(str, msg[2]);
		}else{
			System.out.println("passage dans la condition 2");
			int hashTmp = Peer.hashModulo(this.pair.getHash(), hash, 100);
			int hashSuccTmp = Peer.hashModulo(this.pair.getHash(), hashSucc, 100);
			if(hashTmp < hashSuccTmp){
				
				str = Message.INSERT_NET.toString()+":"+Integer.toString(this.pair.getHashSuccesseur())+":"+this.pair.getIpSuccesseur();
				System.out.println("Message pour le nv successeur : "+str);
				this.pair.changeSuccesseur(msg[2], hash);
				NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
			}else{
				System.out.println("passage dans la condition 2.2");
				System.out.println("Je forward le message : " + msg.toString());
				forwardMessage(msg);
			}
		}
		
	}
	
	public void niceToMeetYou(String[] msg){
		System.out.println("passe méthode nicetomeetyou");
		this.pair.changeSuccesseur(msg[2], Integer.parseInt(msg[1]));
	}

}

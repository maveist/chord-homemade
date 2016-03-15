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
						switch(msg[0]){
							case "in":
								in(msg);
								break;
							case "NiceToMeetYouSucc":
								niceToMeetYouSucc(msg);
								break;
							case "NiceToMeetYouPred":
								niceToMeetYouPred(msg);
								break;
							case "yaf":
								this.pair.changeSuccesseur(this.pair.getIp(), this.pair.getHash());
								break;
							case "ws":
								this.whoSuccessor(msg);
								break;
							case "wp":
								this.whoPredecessor(msg);
								break;
							case "bd":
								this.badDisconnect(msg);
								break;
							case "ims":
								//réponse iam successor
								int hashNewSucc = Integer.parseInt(msg[1]);
								String ipNewSucc = msg[2];
								this.pair.changeSuccesseur(ipNewSucc, hashNewSucc);
								break;
							case "ips":
								//reponse iam predecessor
								int hashNewPred = Integer.parseInt(msg[1]);
								String ipNewPred = msg[2];
								this.pair.changePredecesseur(ipNewPred, hashNewPred);
								break;
							
						}
				}
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}

	}
	
	public void forwardSize(String[] msg, boolean toSucc){
		int currentSize = Integer.parseInt(msg[2]);
		String ipDest ="";
		int hashDest = -1;
		if(toSucc){
			ipDest = this.pair.getIpSuccesseur();
			hashDest = this.pair.getHashSuccesseur();
		}else{
			ipDest = this.pair.getIpPredecesseur();
			hashDest = this.pair.getHashPredecesseur();
		}
		String str = msg[0]+":"+msg[1]+":"+Integer.toString(currentSize);
		try{
			NetworkManager.sendMessage(str, ipDest);
		}catch(IOException e){
			this.pair.signalLeaver(hashDest);
		}
	}
	
	public void forwardMessage(String[] msg, boolean toSucc){
		String str = msg[0]; 
		for(int i = 1 ; i < msg.length; i++){
			str = str+":"+msg[i];
		}
		String ipDest ="";
		int hashDest = -1;
		if(toSucc){
			ipDest = this.pair.getIpSuccesseur();
			hashDest = this.pair.getHashSuccesseur();
		}else{
			ipDest = this.pair.getIpPredecesseur();
			hashDest = this.pair.getHashPredecesseur();
		}
		try{
			NetworkManager.sendMessage(str, ipDest);
		}catch(IOException e){
			this.pair.signalLeaver(hashDest);
		}
	}
	
	public void in(String[] msg){
		int hash = Integer.parseInt(msg[1]);
		int hashSucc = this.pair.getHashSuccesseur();
		//Pour savoir si le pair n'a que lui comme successeur soit qu'il est tt seul.
		if(!this.pair.haveSuccesseur()){
			this.pair.changeSuccesseur(msg[2], hash);
			String str = Message.INSERT_NET_SUCC.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
			try{
				NetworkManager.sendMessage(str, msg[2]);
			}catch(IOException e){
				this.pair.signalLeaver(this.pair.getHashSuccesseur());
			}
		}
		if(!this.pair.havePredecesseur()){
			this.pair.changePredecesseur(msg[2], hash);
			String str = Message.INSERT_NET_PRED.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
			try{
				NetworkManager.sendMessage(str, msg[2]);
			}catch(IOException e){
				this.pair.signalLeaver(hash);
			}
		}
		if(!this.pair.havePredecesseur() || !this.pair.haveSuccesseur()){
			int hashTmp = Peer.hashModulo(this.pair.getHash(), hash, 100);
			int hashSuccTmp = Peer.hashModulo(this.pair.getHash(), hashSucc, 100);
			if(hashTmp < hashSuccTmp){
				String strToSucc = Message.INSERT_NET_PRED.toString()+":"+msg[1]+":"+msg[2];
				try{
					NetworkManager.sendMessage(strToSucc, this.pair.getIpSuccesseur());
				}catch(IOException e){
					this.pair.signalLeaver(this.pair.getHashSuccesseur());
				}
				
				String strToNewPeer = Message.INSERT_NET_SUCC.toString()+":"+Integer.toString(this.pair.getHashSuccesseur())+":"+this.pair.getIpSuccesseur();
				this.pair.changeSuccesseur(msg[2], hash);
				try{
					NetworkManager.sendMessage(strToNewPeer, this.pair.getIpSuccesseur());
				}catch(IOException e){
					this.pair.signalLeaver(this.pair.getHashSuccesseur());
				}
				 
			}else{
				forwardMessage(msg, true);
			}
		}
	}
	
	public void niceToMeetYouPred(String[] msg){
		this.pair.changePredecesseur(msg[2], Integer.parseInt(msg[1]));
	}
	
	public void niceToMeetYouSucc(String[] msg){
		this.pair.changeSuccesseur(msg[2], Integer.parseInt(msg[1]));
	}
	
	public void whoSuccessor(String[] msg){
		int oldHash = Integer.parseInt(msg[1]);
		int hashClaimer = Integer.parseInt(msg[2]);
		String ipClaimer = msg[3];
		if(this.pair.getHashPredecesseur() == oldHash){
			try{
				String str = Message.IAM_SUCC.toString()+":"+this.pair.getHash()+":"+this.pair.getIp();
				NetworkManager.sendMessage(str, ipClaimer);
			}catch(IOException e){
				this.pair.signalLeaver(hashClaimer);
			}
		}else{
			this.forwardMessage(msg, false);
		}
	}
	
	public void whoPredecessor(String[] msg){
		int oldHash = Integer.parseInt(msg[1]);
		int hashClaimer = Integer.parseInt(msg[2]);
		String ipClaimer = msg[3];
		if(this.pair.getHashSuccesseur() == oldHash){
			try{
				String str = Message.IAM_PRED.toString()+":"+this.pair.getHash()+":"+this.pair.getIp();
				NetworkManager.sendMessage(str, ipClaimer);
			}catch(IOException e){
				this.pair.signalLeaver(hashClaimer);
			}
		}else{
			this.forwardMessage(msg, true);
		}
	}

	public void badDisconnect(String[] msg){
		int oldHash = Integer.parseInt(msg[1]);
		if(oldHash == this.pair.getHashPredecesseur()){
			String str = Message.WHO_PRED.toString()+":"+msg[1];
			try{
				NetworkManager.sendMessage(str, this.pair.getIpSuccesseur());
			}catch(IOException e){
				this.pair.signalLeaver(this.pair.getHashSuccesseur());
			}
		}else if(oldHash == this.pair.getHashSuccesseur()){
			String str = Message.WHO_SUCC.toString()+":"+msg[1];
			try{
				NetworkManager.sendMessage(str, this.pair.getIpPredecesseur());
			}catch(IOException e){
				this.pair.signalLeaver(this.pair.getHashPredecesseur());
			}
		}
	}
}

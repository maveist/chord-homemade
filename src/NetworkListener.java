import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


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
							case "msg":
								int hashDest = Integer.parseInt(msg[1]);
								if(this.pair.getHash() == hashDest){
									System.out.println("Message texte reçu: "+msg[2]);
								}else{
									this.pair.sendMessage(hashDest, msg);
								}
								break;
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
								this.pair.changePredecesseur(this.pair.getIp(), this.pair.getHash());
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
							case "iqPrecc":
								this.pair.changePredecesseur(msg[2], Integer.parseInt(msg[1]));
								break;
							case "ipSucc":
								this.pair.changePredecesseur(msg[2], Integer.parseInt(msg[1]));
								break;
							case "ims":
								//réponse iam successor
								int hashNewSucc = Integer.parseInt(msg[1]);
								String ipNewSucc = msg[2];
								this.pair.changeSuccesseur(ipNewSucc, hashNewSucc);
								if(this.pair.getWaitingMessage() != null){
									try {
										this.pair.sendMessage(this.pair.getHashSuccesseur(), this.pair.getWaitingMessage());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									this.pair.setWaitingMessage(null);
								}
								break;
							case "ips":
								//reponse iam predecessor
								int hashNewPred = Integer.parseInt(msg[1]);
								String ipNewPred = msg[2];
								this.pair.changePredecesseur(ipNewPred, hashNewPred);
								if(this.pair.getWaitingMessage() != null){
									try {
										this.pair.sendMessage(this.pair.getHashPredecesseur(), this.pair.getWaitingMessage());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									this.pair.setWaitingMessage(null);
								}
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
			this.pair.signalLeaver(str, hashDest);
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
			this.pair.signalLeaver(str, hashDest);
		}
	}
	
	public void in(String[] msg){
		int hash = Integer.parseInt(msg[1]);
		int hashSucc = this.pair.getHashSuccesseur();
		
		//Pour savoir si le pair n'a que lui comme successeur soit qu'il est tt seul.
		if(this.pair.havePredecesseur() && this.pair.haveSuccesseur()){
			/*int hashTmp = Peer.hashModulo(this.pair.getHash(), hash, 100);
			int hashSuccTmp = Peer.hashModulo(this.pair.getHash(), hashSucc, 100);*/
			
			int myHash = this.pair.getHash();
			int myhashSu = this.pair.getHashSuccesseur();
			//if(hashTmp < hashSuccTmp){
			System.out.println("mon hash: "+Integer.toString(myHash)+" le hash de mon successeur: "+Integer.toString(myhashSu));
			if((myHash > myhashSu && hashSucc > myHash) || hashSucc > myHash){
				String strToSucc = Message.INSERT_NET_PRED.toString()+":"+msg[1]+":"+msg[2];
				try{
					NetworkManager.sendMessage(strToSucc, this.pair.getIpSuccesseur());
				}catch(IOException e){
					this.sendImportantMessage(strToSucc, this.pair.getIpSuccesseur());
					System.out.println("erreur dans l'envoi de message");
					e.printStackTrace();
					System.out.println("ok");
				}
				ArrayList<String> msgs = new ArrayList();
				String strToNewPeer = Message.INSERT_NET_SUCC.toString()+":"+Integer.toString(this.pair.getHashSuccesseur())+":"+this.pair.getIpSuccesseur();
				String strToNewPeer1 = Message.INSERT_NET_PRED.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
				msgs.add(strToNewPeer);
				msgs.add(strToNewPeer1);
				this.pair.changeSuccesseur(msg[2], hash);
				sendImportantMessage(msgs, this.pair.getIpSuccesseur());
	
				 
			}else{
				System.out.println("forward le message");
				forwardMessage(msg, true);
			}
		}else{
			if(!this.pair.haveSuccesseur()){
				this.pair.changeSuccesseur(msg[2], hash);
				String str = Message.INSERT_NET_SUCC.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
				
				try{
					NetworkManager.sendMessage(str, msg[2]);
				}catch(IOException e){
					this.sendImportantMessage(str, msg[2]);
					System.out.println("erreur dans l'envoi de message");
					e.printStackTrace();
					//this.pair.signalLeaver(str, this.pair.getHashSuccesseur());
					System.out.println("ok");
				}
			}
			if(!this.pair.havePredecesseur()){
				this.pair.changePredecesseur(msg[2], hash);
				String str = Message.INSERT_NET_PRED.toString()+":"+Integer.toString(this.pair.getHash())+":"+this.pair.getIp();
				
				try{
					NetworkManager.sendMessage(str, msg[2]);
				}catch(IOException e){
					this.sendImportantMessage(str, msg[2]);
					System.out.println("erreur dans l'envoi de message");
					e.printStackTrace();
					//this.pair.signalLeaver(str, hash);
					System.out.println("ok");
				}
			}
		}
		
	}
	
	public void sendImportantMessage(String msg, String ip){
		try {
			NetworkManager.sendMessage(msg, ip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendImportantMessage(msg, ip);
			System.out.println("ok");
		}
	}
	
	public void sendImportantMessage(ArrayList<String> msgs, String ip){
		for(String msg : msgs){
			try{
				NetworkManager.sendMessage(msg, ip);
			}catch(IOException e){
				sendImportantMessage(msgs, ip);
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
		String str ="";
		int oldHash = Integer.parseInt(msg[1]);
		int hashClaimer = Integer.parseInt(msg[2]);
		String ipClaimer = msg[3];
		if(this.pair.getHashPredecesseur() == oldHash){
			try{
				str = Message.IAM_SUCC.toString()+":"+this.pair.getHash()+":"+this.pair.getIp();
				NetworkManager.sendMessage(str, ipClaimer);
			}catch(IOException e){
				System.out.println("erreur dans l'envoi de message");
				e.printStackTrace();
				this.pair.signalLeaver(str, hashClaimer);
			}
		}else{
			this.forwardMessage(msg, false);
		}
	}
	
	public void whoPredecessor(String[] msg){
		String str ="";
		int oldHash = Integer.parseInt(msg[1]);
		int hashClaimer = Integer.parseInt(msg[2]);
		String ipClaimer = msg[3];
		if(this.pair.getHashSuccesseur() == oldHash){
			try{
				str = Message.IAM_PRED.toString()+":"+this.pair.getHash()+":"+this.pair.getIp();
				NetworkManager.sendMessage(str, ipClaimer);
			}catch(IOException e){
				System.out.println("erreur dans l'envoi de message");
				e.printStackTrace();
				this.pair.signalLeaver(str, hashClaimer);
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
				System.out.println("erreur dans l'envoi de message");
				e.printStackTrace();
				this.pair.signalLeaver(str, this.pair.getHashSuccesseur());
			}
		}else if(oldHash == this.pair.getHashSuccesseur()){
			String str = Message.WHO_SUCC.toString()+":"+msg[1];
			try{
				NetworkManager.sendMessage(str, this.pair.getIpPredecesseur());
			}catch(IOException e){
				System.out.println("erreur dans l'envoi de message");
				e.printStackTrace();
				this.pair.signalLeaver(str, this.pair.getHashPredecesseur());
			}
		}else{
			forwardMessage(msg, true);
		}
	}
}

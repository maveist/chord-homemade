

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkManager {
	
	private int hashPrec;
	private String ipPrec;
	
	private int hashNext;
	private String ipNext;
	
	private int port;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	private String myip;
	private int myHash;
	
	public NetworkManager(String ip, int port, String ipSucc, int hash){
		this.myHash = hash;
		this.myip = ip;
		this.port = port;
		this.ipNext = ipSucc;
		try{
			this.socket = new Socket(ipSucc, port);
			this.input = this.socket.getInputStream();
			this.output = this.socket.getOutputStream();
			//this.connexion();
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		// Ne pas l'appeler depuis le constructeur car on a besoin de l'ip du WelcomeServer et port
		// On va probablement l'appeler depuis le main.
		//this.getInNetwork();
	}
	
	public void getInNetwork(String ipWelcome, int portWelcome){
		//Communication avec le WelcomeServeur
		Socket sockWelcome;
		String ipToContact;
		try {
			sockWelcome = new Socket(ipWelcome, portWelcome);
			OutputStream outToWelcome = sockWelcome.getOutputStream();
			InputStream inWelcome = sockWelcome.getInputStream();
			PrintWriter toWelcome = new PrintWriter(outToWelcome, true);
			toWelcome.print("yo:"+Integer.toString(this.myHash)+":"+this.myip);
			
			BufferedReader readWelcome = new BufferedReader(new InputStreamReader(inWelcome));
			String strIn = readWelcome.readLine();
			ipToContact = strIn;
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		// Communication avec l'ip d'entrée donnée par WelcomeServer
		String str = "in:"+Integer.toString(this.myHash)+":"+this.myip;
		PrintWriter sortie = new PrintWriter(this.output , true ) ;
		sortie.println(str);
		
		//Reception de l'identité du predecesseur
		BufferedReader entree = new BufferedReader(new InputStreamReader(this.input));
		String in;
		
		try {
			in = entree.readLine();
			String[] msg = in.split(":");
			if(msg[0].equals("go")){
				
				 //Communication avec le predecesseur pour s'insérer dans le réseau.
				 String msgEnter = "NiceToMeetYou";
				 this.hashPrec = Integer.parseInt(msg[1]);
				 this.ipPrec = msg[2];
				 Socket sock = new Socket(this.ipPrec, this.port);
				 OutputStream out = sock.getOutputStream();
				 sortie = new PrintWriter(out, true);
				 sortie.print(msgEnter);
				 
				 //Ecoute du prédecesseur pour savoir le successeur
				 InputStream precInput = sock.getInputStream();
				 BufferedReader precToMe = new BufferedReader(new InputStreamReader(precInput));
				 in=precToMe.readLine();
				 String[] dataSucc = in.split(":");
				 if(dataSucc[0].equals("ys")){ // ys == Your Successor. Regex => ys:hashSucc:IpSucc
					 this.hashNext = Integer.parseInt(dataSucc[1]);
					 this.ipNext = dataSucc[2];
				 }
				 
				 
				 sock.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getIpPrec() {
		return ipPrec;
	}
	public void setIpPrec(String ipPrec) {
		this.ipPrec = ipPrec;
	}
	public String getIpNext() {
		return ipNext;
	}
	public void setIpNext(String ipNext) {
		this.ipNext = ipNext;
	}
}

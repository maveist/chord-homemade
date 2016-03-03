import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String ipWelcome ="localhost";
		int portWelcome = 8000;
		/*System.out.println("Adresse ip de serveur welcome");
		try {
			ipWelcome = bufferRead.readLine();
			System.out.println("Port du serveur welcome");
			portWelcome = Integer.parseInt(bufferRead.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		NetworkManager.setHashServerIp("localhost");
		NetworkManager.setHashServerPort(8001);
		Peer monPair = new Peer();
		NetworkManager.getInNetwork(ipWelcome, portWelcome, monPair);
	}

}

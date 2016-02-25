import java.net.InetAddress;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InetAddress IP;
		try {
			IP = InetAddress.getLocalHost();
			System.out.println("IP of my system is := "+IP.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

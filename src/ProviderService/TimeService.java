package ProviderService;

import java.net.ServerSocket;
import java.net.Socket;

public class TimeService implements ProviderService{

	private final Socket socket;
	private final ServerSocket serverSocket;
	
	 public TimeService(Socket s, ServerSocket ss) {
		// TODO Auto-generated constructor stub
		 this.socket=s;
		 this.serverSocket=ss;
		 
		}
	@Override
	public void provideService() {
		// TODO Auto-generated method stub
		
	}

	
}

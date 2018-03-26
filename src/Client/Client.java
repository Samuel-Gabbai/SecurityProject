package Client;

import java.net.Socket;

public class Client {
	private Socket s;
	private String name;
	private String addr;
	private int port;
	private ClientModel clientModel;
	
	public Client(String name, String addr, int port) {
		this.name = name;
		this.addr = addr;
		this.port = port;
		this.clientModel = new ClientModel(name);
	}
	
	public void connect(){
		try {
			Thread t = new Thread(new ClientAuthentification(clientModel,name));
			t.start();
			t.join();
			System.out.println("connexion au server FTP");
			s = new Socket(addr, port);
			System.out.println("deconnexion");
			s.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

	
	@Override
	public void sendCertificat(String name, byte[] encoded) {
		os.sendCertificat(name, clientModel.sendMyCertif());
	}

	@Override
	public void receiveCertificat(String name, byte[] encoded) {
		System.out.println(name);
		System.out.println(clientModel.verifyCertificate(name, encoded));
		
	}
	@Override
	public void getSessionKey(byte[] sessionKey, byte[] signature) {
		clientModel.getSessionKey(sessionKey, signature);
	}
	

	
}

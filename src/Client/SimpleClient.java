package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SimpleClient {
	SocketChannel sc; // Fait les communications dans les 2 sens, il peut lire est ecrire (Pour le client)
	boolean isConnected=false;

	
	public SimpleClient(String addr , int port) throws IOException {
		//inetAdress = addresse internevt ipV4 ou ipV6
		//inetSocketAdrr equivalent de la structure en C;
		
		InetSocketAddress isa = new InetSocketAddress(addr, port);
		sc = SocketChannel.open();
		sc.configureBlocking(true); // Valuer par defaut pas oblige de mettre cette ligne
		sc.connect(isa);
		this.isConnected=true;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public void setIsConnected(boolean b){
		this.isConnected=b;
	}
	
	
	void start() throws InterruptedException {
		Thread rp = new Thread(new RepeatNetwork(this.sc, this));
		Thread rk = new Thread(new RepeatKeyboard(this.sc, this));
		
		rk.start();
		rp.start();
		
		rk.join();
		rp.join();
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		SimpleClient client = new SimpleClient("127.0.0.1",8091);
		client.start();
	}
	
 
 
}
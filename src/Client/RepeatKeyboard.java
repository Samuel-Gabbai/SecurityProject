package Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class RepeatKeyboard implements Runnable{
	private SocketChannel sc;
	private ByteBuffer bb;
	private SimpleClient client;
	private ReadableByteChannel rbc;
	final private Charset charset = Charset.forName("UTF-8");

	
	public RepeatKeyboard(SocketChannel sc, SimpleClient client) {
		this.sc = sc;
		this.client = client;
		this.rbc = Channels.newChannel(System.in);
		this.bb=ByteBuffer.allocate(512);
	}


	@Override
	public void run() {
		
			while(client.isConnected()) {
				try {
					rbc.read(bb);
					bb.flip();
					sc.write(bb);
					bb.clear();
	
						
				} catch (IOException e) {
				System.out.println("Deconnection");
				client.setIsConnected(false);		}
				
			}
	}
	
}

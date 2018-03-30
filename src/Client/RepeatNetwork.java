package Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class RepeatNetwork implements Runnable{
	private SocketChannel sc;
	private ByteBuffer bb;
	private SimpleClient client;
	final private Charset charset = Charset.forName("UTF-8");

	public RepeatNetwork(SocketChannel sc, SimpleClient cli) {
		this.sc= sc;
		this.client=cli;
		this.bb= ByteBuffer.allocate(512);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(client.isConnected()) {
				sc.read(bb);
				bb.flip();
				CharBuffer cb = charset.decode(bb);
				System.out.println(cb.toString());
				bb.clear();
			}
		} catch (IOException e) {
			System.out.println("Deconnection");
			client.setIsConnected(false);
		}
	}
	
}
package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Map;

import Client.SimpleClient;



/*
 *Pour la communication cleint server le client envoie le nom de la requette et le server créer la requette et le met dans uyn fichier crypter avec la clé publique du client et envoie au second client l'adresse du fichier  
 * 
 * 
 * */


public class Server {
	ServerSocketChannel ssc;
	Selector selector;
	ByteBuffer bb;
	Map<String,SimpleClient> allClient;
	
	Server(int p) throws IOException{
		ssc =ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(p));
		selector = Selector.open();
		bb = ByteBuffer.allocateDirect(512);
		ssc.register(selector, SelectionKey.OP_ACCEPT);	
	}
	
	@Override
	public String toString() {
		return "Server :" + ssc; 
	}
	
	void accept() throws IOException {
		SocketChannel sc = ssc.accept();
		if( sc == null) {
			System.out.println("Rien à accepter");
			return;
		}
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		System.out.println("accept:"+sc);
	}
	
	void repeat(SelectionKey sk) throws IOException {
		SocketChannel sc = (SocketChannel) sk.channel();
		bb.clear();
		if(sc.read(bb) == -1) {
			System.out.println("connection" + sc +" closed");
			sk.cancel();
			sc.close();
			return;
		}
		bb.flip();
		
		Charset c= Charset.forName("UTF-8");
		CharBuffer cb = c.decode(bb);
		System.out.println("New Message:"+cb.toString());
		for(SelectionKey sk2 : selector.keys()) {
			if( sk2.channel() != ssc) {
				bb.rewind();
				SocketChannel sc2 = (SocketChannel) sk2.channel();
				sc2.write(bb);
			}
		}
		bb.clear();
		
	};
	
	void run() throws IOException {
		while(true) {
			selector.select();
			for( SelectionKey sk: selector.selectedKeys() ) {
				if(sk.isAcceptable() ) {
					accept();
				}else if( sk.isReadable() ) {
					repeat(sk);
				}
			}
			selector.selectedKeys().clear();	
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("main");
		Server s = new Server(8091);
		System.out.println(s);
		s.run();
		
	}
}
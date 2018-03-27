package Client;

public class SimpleClient {
	public static void main(String[] args) {
		Client c = new Client("Sam","localhost", 8080);
		c.connect();
	}
	
}

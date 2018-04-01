package ServerCertificat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;


public class ServerCertificate {

	private String serverName;
	private BouncyCastleProvider bcp;
	private FileOutputStream fos;
	private FileInputStream fis;
	private ServerSocket ss;
	private Socket socketClient;
	private KeyPair kp;
	private PublicKey pk;
	private PrivateKey prk;
	private PKCS10CertificationRequest pcr;
	private X509Certificate x509Certificate;
	private KeyStore ks;
	
	
	public ServerCertificate(){


		try {

			serverName = "ServerAuthority";
			
			bcp = new BouncyCastleProvider();
			Security.addProvider(bcp);

			
			createCertificateServer();

			ss = new ServerSocket(2324);

			System.out.println("En attente d'une connexion...");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	
	public void launchServer() throws IOException {
		// TODO Auto-generated method stub
		
		while(true){
			
			socketClient = ss.accept();
			new Thread(new ServerCertificateAgent(socketClient, prk, x509Certificate)).start();
			
		}
	}
	
	
	private void createCertificateServer() {
		// TODO Auto-generated method stub
		
		try {
			
			kp = KeyPairGenerator.getInstance("RSA", bcp).generateKeyPair();
			pk = kp.getPublic();
			prk = kp.getPrivate();
			
			X500Name xn = new X500NameBuilder()
					.addRDN(BCStyle.CN, serverName)
					.build();
			
			PKCS10CertificationRequestBuilder pkcsBuilder = new JcaPKCS10CertificationRequestBuilder(xn, pk);
			JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
			ContentSigner signer = jcsb.build(prk);
			
			pcr = pkcsBuilder.build(signer);
			
			//System.out.println((pcr.getEncoded()).toString());
			
			Date notBefore = new Date(System.currentTimeMillis());
			Date notAfter = new Date((System.currentTimeMillis() + 1000000000 * 100));
			
			BigInteger serialNumber = getSerialNumber();
			
			X509v3CertificateBuilder x509builder = new X509v3CertificateBuilder(xn, serialNumber, notBefore, notAfter, pcr.getSubject(), pcr.getSubjectPublicKeyInfo());
			X509CertificateHolder x509holder = x509builder.build(signer);
		

			x509Certificate = (new JcaX509CertificateConverter()).getCertificate(x509holder);
			
			File file = new File("CertificatRepertory");
			
			if(!file.exists())
				file.mkdir();
			
			fos = new FileOutputStream(file.toString()+"/ServerCertif");
			
			fos.write(x509Certificate.getEncoded());
	
			
			createkeyStore();
			
			
			
			//System.out.println((x509Certificate.getPublicKey()).toString()); 
			//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAA");
			//System.out.println(((ks.getCertificate("ServerAuthority").getPublicKey()).toString()));
			
		} catch (NoSuchAlgorithmException | OperatorCreationException | IllegalStateException | IOException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void createkeyStore() {
		// TODO Auto-generated method stub
		try {
			
			File fileStore = new File("CertificatRepertory/KeyStore");
			
			if(!fileStore.exists())
				fileStore.createNewFile();
			
			fos = new FileOutputStream(fileStore);
			
			ks = KeyStore.getInstance("JKS");
			
			ks.load(null, null);
			
			ks.setCertificateEntry(serverName, x509Certificate);
			
			ks.store(fos, "password".toCharArray());
			
		} catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static BigInteger getSerialNumber() {
		// TODO Auto-generated method stub
		return BigInteger.valueOf(new Random().nextLong());
	}


}

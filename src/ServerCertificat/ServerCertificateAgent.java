package ServerCertificat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class ServerCertificateAgent implements Runnable {

	private PKCS10CertificationRequest csrClient;
	private ASN1InputStream ans;
	private String clientName;
	private PrivateKey prkS;
	private X509Certificate certif, certifServ;
	private FileInputStream fis;
	private Socket s;
	private KeyStore ks;
	private String serverName = "ServerAuthority"; 
	
	public ServerCertificateAgent(Socket socketClient, PrivateKey prk, X509Certificate certif) {
		
		// TODO Auto-generated constructor stub
		s = socketClient;
		certifServ = certif;
		prkS = prk;
		
		try {
			
			ks = KeyStore.getInstance("JKS");
			
			try{
				
				fis = new FileInputStream(new File("CertificatRepertory/keyStore"));

				ks.load(fis, "password".toCharArray());

				System.out.println("Chargement du fichier");
				
			}catch(FileNotFoundException e1){
				
				System.out.println("Creation d'un KeyStore");
				ks.load(null, null);
				e1.printStackTrace();
			
			}
			
			
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		

		System.out.println("En attente d'une commande");
		
		try {
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			String command = dis.readUTF();
			
			System.out.println("En attente d'une commande");
			
			switch(command){
			
			case("get"): 
				
				this.clientName = dis.readUTF();
				String pathName = dis.readUTF();
				
				createCertifClient(pathName);
				
				addCertkeyStore();
				
				byte[] certificatCl = certif.getEncoded();
				
				dos.writeInt(certificatCl.length);
				dos.write(certificatCl);
				
				byte[] certificatServer = certifServ.getEncoded();
				
				dos.writeInt(certificatServer.length);
				dos.write(certificatServer);
				
				dos.flush();
				
				System.out.println("Certificats envoy�s");

				break;

			default: System.out.println("La commande tap�e n'existe pas");
			}
			
		} catch (IOException | CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
	}
	
	
	private void createCertifClient(String pathName) {
		// TODO Auto-generated method stub
		
		try {
			
			X500Name xn = new X500NameBuilder()
							.addRDN(BCStyle.CN, this.clientName)
							.build();
			
			ans = new ASN1InputStream(new FileInputStream(new File(pathName)));

			ASN1Primitive anp;
			anp = ans.readObject();

			csrClient = new PKCS10CertificationRequest(anp.getEncoded());
			
			Date notBefore = new Date(System.currentTimeMillis());
			Date notAfter = new Date(System.currentTimeMillis()+ 100000000 * 100);
			
			BigInteger serialNumber = ServerCertificate.getSerialNumber();
			
			JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
			ContentSigner signer = jcsb.build(prkS);
			
			X509v3CertificateBuilder x509builder = new X509v3CertificateBuilder(xn, serialNumber, notBefore, notAfter, csrClient.getSubject(), csrClient.getSubjectPublicKeyInfo());
			X509CertificateHolder x509holder = x509builder.build(signer);
			
			certif = (new JcaX509CertificateConverter()).getCertificate(x509holder);
			
			FileOutputStream fos = new FileOutputStream(new File("CertificatRepertory/"+clientName+"Certif"));
			
			fos.write(certif.getEncoded());
			
			fos.close();
			
		} catch (IOException | OperatorCreationException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void addCertkeyStore() {
		// TODO Auto-generated method stub
		try {
			
			File fileStore = new File("CertificatRepertory/KeyStore");
			
			FileOutputStream fos = new FileOutputStream(fileStore);

			ks.setCertificateEntry(clientName, certif);
			
			ks.store(fos, "password".toCharArray());
			
		} catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}

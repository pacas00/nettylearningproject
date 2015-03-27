package net.petercashel.nettyCore.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.internal.logging.Log4JLoggerFactory;


public class SSLContextProvider {
	
	public static boolean useExternalSSL = false;
	public static String pathToSSLCert = "";
	public static String SSLCertSecret = "secret";
	
	private static byte[] pkcs12Base64 = null; //
    private static SSLContext sslContext = null;
    
    public SSLContextProvider() {
    	SetupSSL();
	}
    
    public SSLContextProvider(String path, String secret) {
    	useExternalSSL = true;
    	pathToSSLCert = path;
    	SSLCertSecret = secret;
    	SetupSSL();
	}
    
    public static void SetupSSL() {
    	
    	if (useExternalSSL) {
    		try {
				pkcs12Base64 = Files.readAllBytes(FileSystems.getDefault().getPath(pathToSSLCert));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	} else {
    		InputStream in = SSLContextProvider.class.getClass().getResourceAsStream("/net/petercashel/nettyCore/ssl/SSLCERT.p12");

    		int buffersize = 0;
    		try {
				buffersize = in.available();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				buffersize = 16384;
			}
    		byte[] buffer = new byte[buffersize];
    		try {
    	        if ( in.read(buffer) == -1 ) {
    	        }        
    	    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally { 
    	        try {
    	             if ( in != null ) 
    	                  in.close();
    	        } catch ( IOException e) {
    	        }
    	        
    	    }
    		pkcs12Base64 = buffer;
    		
    	}
    }
	
	
	public static SSLContext get() {
        if(sslContext==null) {
            synchronized (SSLContextProvider.class) {
                if(sslContext==null) {
                    try {
                        sslContext = SSLContext.getInstance("TLSv1.2");
                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        ks.load(new ByteArrayInputStream((pkcs12Base64)), SSLCertSecret.toCharArray());
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                        kmf.init(ks, SSLCertSecret.toCharArray());
                        
                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
                        trustManagerFactory.init(ks);

                        SecureRandom r = new SecureRandom();
                        byte[] seed = r.generateSeed(r.nextInt());
                        r = new SecureRandom(seed);
                        sslContext.init(kmf.getKeyManagers(), trustManagerFactory.getTrustManagers(), r);
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
        return sslContext;
    }
}
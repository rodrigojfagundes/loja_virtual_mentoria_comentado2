package jdev.mentoria.lojavirtual.service;

import java.io.Serializable;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;



//ignorar certificado ssl, 
//class para os servidores de API não ignorar requisicoes q 
//não tenham certificados SSL
//
public class HostIgnoringCliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String hostName;
	
	
	public HostIgnoringCliente(String hostName) {
		this.hostName = hostName;
	}
	
	//simulando um certificado (criando certificado fantasma)
	//o PROF DISSE Q E CONFUSO MESMO... E Q MEIO Q PD SO COPIAR
	//PQ ELE DISSE Q E BEM DIFICIL
	//
	//meio q vamos desabilitar a parte de SSL e vamos criar um certificado
	//fantasma
	public Client hostIgnoringCliente () throws Exception {
		
		TrustManager[] trustManagers = new TrustManager[] {
				
				//implementando a interface X509TrustManager
				    new X509TrustManager() {
					
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					
					@Override
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
						
					}
					
					@Override
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
						
					}
				}
		  };
		
		
		//instanciando um SSLCONTEXT do tipo TLS
		SSLContext sslContext = SSLContext.getInstance("TLS");
		//ali onde ta NULL iria ser passado um certificado
		//mas como estamos fazendo o codigo para burla isso.... Nos vamos
		//deixar em null
		//
		//o prof disse q e complexo é TIPO RECEITA DE BOLO... COPIA E COLA...
		sslContext.init(null, trustManagers, new SecureRandom());
		
		//em hostname estamos passando a URL do servidor da API
		//q e para ignorar SSL/certificado +ou- isso
		Set<String> hostNameList = new HashSet<String>();
		hostNameList.add(this.hostName);
		HttpsURLConnection.setDefaultHostnameVerifier(new IgnoreHostNameSSL(hostNameList));
		
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		
		DefaultClientConfig config = new DefaultClientConfig();
		Map<String, Object> properties = config.getProperties();
		HTTPSProperties httpsProperties = new HTTPSProperties(new HostnameVerifier() {
			
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		}, sslContext);
		
		properties.put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
		config.getClasses().add(JacksonJsonProvider.class);
		config.getClasses().add(MultiPartWriter.class);
		
		return Client.create(config);
		
	}

}

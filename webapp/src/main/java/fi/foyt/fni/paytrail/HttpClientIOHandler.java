package fi.foyt.fni.paytrail;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import fi.foyt.paytrail.io.IOHandler;
import fi.foyt.paytrail.io.IOHandlerResult;

public class HttpClientIOHandler implements IOHandler {

	@Override
	public IOHandlerResult doPost(String merchantId, String merchantSecret, String url, String data) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
  		HttpPost httpPost = new HttpPost(url);
  		httpPost.setHeader("Content-Type", " application/json");
  		httpPost.setHeader("Accept", " application/json");
  		httpPost.setHeader("X-Verkkomaksut-Api-Version", "1");
  		client.getCredentialsProvider().setCredentials(
        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
        new UsernamePasswordCredentials(merchantId, merchantSecret)
      );
  		
  		httpPost.setEntity(new StringEntity(data, "UTF-8"));
  		
  		HttpResponse response = client.execute(httpPost);
  		
  		HttpEntity entity = response.getEntity();
      try {
        int status = response.getStatusLine().getStatusCode();
        if (status == 204) {
         // No Content
          return new IOHandlerResult(status, null);
        }
  
        return new IOHandlerResult(status, IOUtils.toString(entity.getContent()));
      } finally {
        EntityUtils.consume(entity);
      }
		} finally {
		  client.close();
		}
	}


}

package fi.foyt.fni.paytrail;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.paytrail.json.Marshaller;

public class JacksonMarshaller implements Marshaller {

	public JacksonMarshaller() {
	  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	@Override
	public String objectToString(Object object) throws IOException {
		return mapper.writeValueAsString(object);
	}
	
	@Override
	public <T> T stringToObject(Class<? extends T> clazz, String string) throws IOException {
		return mapper.readValue(string, clazz);
	}

	private ObjectMapper mapper = new ObjectMapper();
}

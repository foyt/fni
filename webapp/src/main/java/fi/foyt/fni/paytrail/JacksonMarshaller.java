package fi.foyt.fni.paytrail;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import fi.foyt.paytrail.json.Marshaller;

public class JacksonMarshaller implements Marshaller {

	public JacksonMarshaller() {
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
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

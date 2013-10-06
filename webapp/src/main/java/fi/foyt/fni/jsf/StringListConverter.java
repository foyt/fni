package fi.foyt.fni.jsf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter (value="fi.foyt.fni.jsf.StringListConverter")
public class StringListConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<String> result = new ArrayList<String>();
		
		if (StringUtils.isNotBlank(value)) {
		  for (String encoded : value.split("&")) {
		  	try {
		  		result.add(URLDecoder.decode(encoded, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new ConverterException(e);
				}
		  }
	  }
		
		return result;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof List) {
			StringBuilder resultBuilder = new StringBuilder();
			
		  @SuppressWarnings("unchecked")
			Iterator<String> list = ((List<String>) value).iterator();
		  while (list.hasNext())  {
		  	String string = list.next();
		  	
				try {
					String encoded = URLEncoder.encode(string, "UTF-8");
					resultBuilder.append(encoded);
					if (list.hasNext()) {
						resultBuilder.append('&');
					}
				} catch (UnsupportedEncodingException e) {
					throw new ConverterException(e);
				}
		  }
		  
		  return resultBuilder.toString();
		}
		
		return null;
	}

}

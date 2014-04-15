package fi.foyt.fni.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

@FacesConverter (value="fi.foyt.fni.jsf.LongListConverter")
public class LongListConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<Long> result = new ArrayList<>();
		
		if (StringUtils.isNotBlank(value)) {
		  for (String longValue : value.split("&")) {
	  		result.add(NumberUtils.createLong(longValue));
		  }
	  }
		
		return result;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof List) {
			StringBuilder resultBuilder = new StringBuilder();
			
		  @SuppressWarnings("unchecked")
			Iterator<Long> list = ((List<Long>) value).iterator();
		  while (list.hasNext())  {
				resultBuilder.append(list.next());
				if (list.hasNext()) {
					resultBuilder.append('&');
				}
		  }
		  
		  return resultBuilder.toString();
		}
		
		return null;
	}

}

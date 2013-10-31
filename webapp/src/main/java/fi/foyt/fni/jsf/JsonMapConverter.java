package fi.foyt.fni.jsf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.codehaus.jackson.map.ObjectMapper;

@FacesConverter(value = "fi.foyt.fni.jsf.JsonMapConverter")
public class JsonMapConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      return objectMapper.readValue(value, Map.class);
    } catch (IOException e) {
      throw new ConverterException(e);
    }
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof Map) {
      ObjectMapper objectMapper = new ObjectMapper();

      StringWriter writer = new StringWriter();
      try {
        try {
          objectMapper.writeValue(writer, value);
        } finally {
          writer.flush();
          writer.close();
        }

        return writer.toString();
      } catch (IOException e) {
        throw new ConverterException(e);
      }

    }
    
    return null;
  }

}
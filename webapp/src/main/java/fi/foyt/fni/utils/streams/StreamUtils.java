package fi.foyt.fni.utils.streams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

  public static String getInputStreamAsString(InputStream inputStream) throws IOException {
    StringBuilder result = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line = null;
    while ((line = bufferedReader.readLine()) != null) {
      result.append(line);
    }
    return result.toString();
  }

  public static byte[] getInputStreamAsBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int l;
    byte[] buf = new byte[1024];
    while ((l = inputStream.read(buf)) > 0) {
      outputStream.write(buf, 0, l);
    }
    return outputStream.toByteArray();
  }
}

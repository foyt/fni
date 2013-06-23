package fi.foyt.fni.utils.encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.commons.codec.binary.Base64;

import fi.foyt.fni.encryption.DesDecrypter;
import fi.foyt.fni.encryption.DesEncrypter;

public class EncryptionUtils {

  public static DesEncrypter createDesEncrypter(String passPhrase, String salt, int iterationCount) {
    return new DesEncrypter(passPhrase, salt, iterationCount);
  }

  public static DesDecrypter createDesDecrypter(String passPhrase, String salt, int iterationCount) {
    return new DesDecrypter(passPhrase, salt, iterationCount);
  }
  
  public static String enryptDes(String passPhrase, String salt, int iterationCount, String data) throws UnsupportedEncodingException, GeneralSecurityException {
    DesEncrypter encrypter = createDesEncrypter(passPhrase, salt, iterationCount);
    return new String(Base64.encodeBase64(encrypter.encrypt(data.getBytes("UTF-8"))), "UTF-8");
  }

  public static String decryptDes(String passPhrase, String salt, int iterationCount, String data) throws GeneralSecurityException, IOException {
    DesDecrypter decrypter = createDesDecrypter(passPhrase, salt, iterationCount);
    return new String(decrypter.decrypt(Base64.decodeBase64(data.getBytes("UTF-8"))), "UTF-8");
  }
}

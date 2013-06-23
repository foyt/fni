package fi.foyt.fni.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class DesDecrypter {

  public DesDecrypter(String passPhrase, String salt, int iterationCount) {
    this.salt = new byte[8];
    for (int i = 0, l = salt.length(); i < l; i++) {
      this.salt[i] = (byte) salt.charAt(i);
    }
    this.iterationCount = iterationCount;
    this.passPhrase = passPhrase;
  }

  public byte[] decrypt(byte[] data) throws IOException, GeneralSecurityException {
    return getDecryptCipher().doFinal(data);
  }

  private Cipher getDecryptCipher() throws GeneralSecurityException {
    if (decryptCipher == null) {
      KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
      decryptCipher = Cipher.getInstance(key.getAlgorithm());
      decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }
    
    return decryptCipher;
  }
  
  private byte[] salt;
  private String passPhrase;
  private int iterationCount;
  private Cipher decryptCipher;
}

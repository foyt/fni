package fi.foyt.fni.encryption;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class DesEncrypter {

  public DesEncrypter(String passPhrase, String salt, int iterationCount) {
    this.salt = new byte[8];
    for (int i = 0, l = salt.length(); i < l; i++) {
      this.salt[i] = (byte) salt.charAt(i);
    }
    this.iterationCount = iterationCount;
    this.passPhrase = passPhrase;
  }

  public byte[] encrypt(byte[] data) throws UnsupportedEncodingException, GeneralSecurityException {
    return getEncryptCipher().doFinal(data);
  }
  
  private Cipher getEncryptCipher() throws GeneralSecurityException {
    if (encryptCipher == null) {
      KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
      encryptCipher = Cipher.getInstance(key.getAlgorithm());
      encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
    }
    
    return encryptCipher;
  }
  
  private byte[] salt;
  private String passPhrase;
  private int iterationCount;
  private Cipher encryptCipher;
}

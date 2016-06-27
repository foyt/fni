package fi.foyt.fni.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class CompressionUtils {

  public static OutputStream uncompressGzipStream(InputStream inputStream) throws IOException {
    GzipCompressorInputStream gzInputStream = new GzipCompressorInputStream(inputStream);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    final byte[] buffer = new byte[1024];
    int n;
    while (-1 != (n = gzInputStream.read(buffer))) {
      outputStream.write(buffer, 0, n);
    }
    
    outputStream.close();
    gzInputStream.close();

    return outputStream;
  }
  
  public static byte[] uncompressGzipArray(byte[] compressed) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(compressed);
    byte[] uncomressed = ((ByteArrayOutputStream) uncompressGzipStream(inputStream)).toByteArray();
    inputStream.close();
    return uncomressed;
  }
  
  public static byte[] uncompressBzip2Array(byte[] compressed) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);
    
    uncompressBzip2Stream(inputStream, outputStream);
    
    inputStream.close();
    outputStream.flush();
    outputStream.close();
    
    return outputStream.toByteArray();
  }

  public static void uncompressBzip2Stream(InputStream inputStream, OutputStream outputStream) throws IOException {
    BZip2CompressorInputStream compressorInputStream = new BZip2CompressorInputStream(inputStream);
    
    final byte[] buffer = new byte[1024];
    int n;
    while (-1 != (n = compressorInputStream.read(buffer))) {
      outputStream.write(buffer, 0, n);
    }
    
    outputStream.close();
    compressorInputStream.close();
  }
  
  public static byte[] compressBzip2Array(byte[] bytes) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    compressBzip2Stream(bytes, outputStream);
    
    outputStream.flush();
    outputStream.close();
    
    return outputStream.toByteArray();
  }

  public static void compressBzip2Stream(byte[] bytes, OutputStream outputStream) throws IOException {
    BZip2CompressorOutputStream compressorOutputStream = new BZip2CompressorOutputStream(outputStream);
    compressorOutputStream.write(bytes);
    compressorOutputStream.flush();
    compressorOutputStream.close();
  }
}

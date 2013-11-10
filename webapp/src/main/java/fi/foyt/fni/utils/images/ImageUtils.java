package fi.foyt.fni.utils.images;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.imageio.ImageIO;

import fi.foyt.fni.utils.data.TypedData;

public class ImageUtils {

  public static TypedData resizeImage(TypedData originalData, Integer maxWidth, Integer maxHeight, ImageObserver imageObserver) throws IOException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalData.getData()));

    int width = maxWidth;
    int height = maxHeight;

    if ((image.getHeight() / maxHeight) > (image.getWidth() / maxWidth))
      width = -1;
    else
      height = -1;

    Image scaledInstance = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    
    if (imageObserver != null) {
    	scaledInstance.getWidth(imageObserver);
    	scaledInstance.getHeight(imageObserver);        
    } else {
    	scaledInstance.getWidth(null);
    	scaledInstance.getHeight(null);
    }
    
    BufferedImage renderedImage = null;
    
    if (scaledInstance instanceof BufferedImage) {
    	renderedImage = (BufferedImage) scaledInstance;
    } else {
    	try {
      	Method getBufferedImageMethod = scaledInstance.getClass().getMethod("getBufferedImage");
      	renderedImage = (BufferedImage) getBufferedImageMethod.invoke(scaledInstance);
    	} catch (Exception e) {
    	}
    }
    	
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    if (ImageIO.write(renderedImage, "png", baos)) {
      baos.flush();
      baos.close();
      return new TypedData(baos.toByteArray(), "image/png", new Date(System.currentTimeMillis()));
    }
    
    return null;
  }


  public static TypedData convertToPng(TypedData originalData) throws IOException {
  	BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalData.getData()));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
  	if (ImageIO.write(image, "png", baos)) {
      baos.flush();
      baos.close();
      return new TypedData(baos.toByteArray(), "image/png", new Date(System.currentTimeMillis()));
    }
    
    return null;
  }
  
  static {
    ImageIO.setUseCache(true);
  }

}

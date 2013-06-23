package fi.foyt.fni.utils.gdocs;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry.MediaType;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DrawingEntry;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.PresentationEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.util.AuthenticationException;

import fi.foyt.fni.persistence.model.materials.GoogleDocumentType;

public class GoogleDocumentsUtils {

  public static GoogleDocumentsClient createClient(String username, String password) throws AuthenticationException {
    return new GoogleDocumentsClient(username, password);
  }

  public static MediaType getMediaTypeByMimeType(String mimeType) {
		for (MediaType mediaType : MediaType.values()) {
			if (mediaType.getMimeType().equals(mimeType))
				return mediaType;
		}
	  
		return null;
  }

  public static MediaType getMediaTypeByFileName(String fileName) {
  	String extension = FilenameUtils.getExtension(fileName);
  	if (StringUtils.isNotBlank(extension)) {
  		extension = extension.toUpperCase();

  		for (MediaType mediaType : MediaType.values()) {
  			if (mediaType.name().equals(extension))
  				return mediaType;
  		}
  	}
  	
		return null;
  }
  
  public static Class<? extends DocumentListEntry> getDocumentListEntryClass(GoogleDocumentType documentType) {
  	switch (documentType) {
  	  case DOCUMENT:
  	    return DocumentEntry.class;
  	  case DRAWING:
  	  	return DrawingEntry.class;
  	  case FOLDER:
  	  	return FolderEntry.class;
  	  case PRESENTATION:
  	  	return PresentationEntry.class;
  	  case SPREADSHEET:
  	  	return SpreadsheetEntry.class;
  	}
  	
  	return null;
  }
  
  public static GoogleDocumentType getTypeByName(String name) {
  	if ("document".equals(name))
  		return GoogleDocumentType.DOCUMENT;
  	if ("presentation".equals(name))
  		return GoogleDocumentType.PRESENTATION;
  	if ("spreadsheet".equals(name))
  		return GoogleDocumentType.SPREADSHEET;
   	if ("folder".equals(name))
  		return GoogleDocumentType.FOLDER;
   	if ("drawing".equals(name))
  		return GoogleDocumentType.DRAWING;
   	
   	return null;
  	
    
  
    /**
    private GoogleDocumentType(String name, Class<? extends DocumentListEntry> entryClass) {
      this.name = name;
      this.entryClass = entryClass;
    }
    
    public String getName() {
      return name;
    }
    
    public static GoogleDocumentType getByName(String name) {
      for (GoogleDocumentType documentType : GoogleDocumentType.values()) {
        if (documentType.getName().equals(name))
          return documentType;
      }
      
      return null;
    }
    
    public Class<? extends DocumentListEntry> getEntryClass() {
      return entryClass;
    }
    
    private String name;
    private Class<? extends DocumentListEntry> entryClass;
    **/
  }
}

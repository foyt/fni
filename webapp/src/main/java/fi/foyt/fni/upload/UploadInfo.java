package fi.foyt.fni.upload;

import java.util.ArrayList;
import java.util.List;

public class UploadInfo {

	public List<UploadInfoFile> getFiles() {
		return files;
	}

	public UploadInfoFile getFileInfo(String fieldName) {
		for (UploadInfoFile file : files) {
			if (file.getFieldName().equals(fieldName))
				return file;
		}
		
		return null;
	}

	private List<UploadInfoFile> files = new ArrayList<UploadInfoFile>();
}

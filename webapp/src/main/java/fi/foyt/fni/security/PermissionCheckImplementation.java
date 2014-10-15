package fi.foyt.fni.security;

import java.io.FileNotFoundException;
import java.util.Map;

public interface PermissionCheckImplementation<T> {

	public boolean checkPermission(T context, Map<String, String> parameters) throws FileNotFoundException;
	
}

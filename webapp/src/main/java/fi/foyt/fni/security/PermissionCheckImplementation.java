package fi.foyt.fni.security;

import java.util.Map;

public interface PermissionCheckImplementation<T> {

	public boolean checkPermission(T context, Map<String, String> parameters);
	
}

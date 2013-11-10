package fi.foyt.fni.security;

public interface PermissionCheckImplementation<T> {

	public boolean checkPermission(T context);
	
}

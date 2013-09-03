package fi.foyt.fni.security;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.session.SessionController;

@Secure
@Interceptor
public class SecureInterceptor implements Serializable {

	private static final long serialVersionUID = 1717214145781666931L;

	@Inject
	private SessionController sessionController;
	
	@Inject
	@Any
	private Instance<PermissionCheckImplementation<?>> permissionChecks;
	
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Secure secure = ic.getMethod().getAnnotation(Secure.class);
		Permission permission = secure.value();
		if (sessionController.hasLoggedUserPermission(permission)) {
			if (invokePermissionChecks(permission, ic.getMethod(), ic.getParameters())) {
  			return ic.proceed();
			}
		} 

		throw new ForbiddenException();
	}

	@SuppressWarnings("serial")
	private boolean invokePermissionChecks(final Permission permission, Method method, Object[] parameters) {
		Object contextParamter = null;
		
		System.out.println(method.toGenericString());
		
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for (int i = 0, l = parameterAnnotations.length; i < l; i++) {
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations) {
				if (annotation instanceof PermissionContext) {
					contextParamter = parameters[i];
					break;
				}
				
				if (contextParamter != null) {
					break;
				}
			}
		};
		
		PermissionCheck permissionCheckAnnotation = new PermissionCheckQualifier() { 
			@Override
			public Permission value() {
				return permission;
			}
		};
		 
		Iterator<PermissionCheckImplementation<?>> permissionCheckIterator = permissionChecks.select(permissionCheckAnnotation).iterator();
		while (permissionCheckIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			PermissionCheckImplementation<Object> permissionCheck = (PermissionCheckImplementation<Object>) permissionCheckIterator.next();
			if (!permissionCheck.checkPermission(contextParamter)) {
				return false;
			}
		}

		return true;
	}
	
	@SuppressWarnings("all")
	private abstract class PermissionCheckQualifier extends AnnotationLiteral<PermissionCheck> implements PermissionCheck { };
	
}

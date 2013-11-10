package fi.foyt.fni.security;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

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
	private boolean invokePermissionChecks(final Permission permission, Method method, Object[] methodParameters) {
		Object contextParameter = null;
		
		SecurityContext securityContext = method.getAnnotation(SecurityContext.class);
		if (securityContext != null) {
			if (StringUtils.isNotBlank(securityContext.context())) {
				contextParameter = evaluateExpression(securityContext.context());
			} else {
				throw new SecurityException("SecurityContext requires a context when used in method body");
			}
		} else {
  		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
  		for (int i = 0, l = parameterAnnotations.length; i < l; i++) {
  			Annotation[] annotations = parameterAnnotations[i];
  			for (Annotation annotation : annotations) {
  				if (annotation instanceof SecurityContext) {
  					contextParameter = methodParameters[i];
  					break;
  				}
  				
  				if (contextParameter != null) {
  					break;
  				}
  			}
  		};
		}
		
		Map<String, Object> parameters = new HashMap<>();
		SecurityParams params = method.getAnnotation(SecurityParams.class);
		if (params != null) {
  		for (SecurityParam param : params.value()) {
  		  Object value = evaluateExpression(param.value());
  		  parameters.put(param.name(), value);
  		}
		}
		
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
			if (!permissionCheck.checkPermission(contextParameter, parameters)) {
				return false;
			}
		}

		return true;
	}
	
	private Object evaluateExpression(String expression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
    ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, expression, Object.class);
    return valueExpression.getValue(elContext);
	}
	
	@SuppressWarnings ("all")
	private abstract class PermissionCheckQualifier extends AnnotationLiteral<PermissionCheck> implements PermissionCheck {
  };
	
}

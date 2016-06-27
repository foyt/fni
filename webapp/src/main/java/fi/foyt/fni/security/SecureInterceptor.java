package fi.foyt.fni.security;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.session.SessionController;

@Secure
@Interceptor
public class SecureInterceptor implements Serializable {

	private static final long serialVersionUID = 1717214145781666931L;
	
	@Inject
	private Logger logger;
	
	@Inject
	private SecurityController securityController;

	@Inject
	private SessionController sessionController;

  @Inject
  private NavigationController navigationController;
	
	private String errorPage = null;
	
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) {
	  if (errorPage == null) {
  	  Secure secure = getAnnotation(ic.getMethod(), ic.getTarget(), Secure.class);
  		
  		if (secure == null) {
  		  throw new SecurityException("Could not find Secure annotation");
  		}
  
  		Permission permission = secure.value();
  		if (sessionController.hasLoggedUserPermission(permission)) {
  			try {
          if (invokePermissionChecks(permission, ic.getTarget(), ic.getMethod(), ic.getParameters())) {
          	return ic.proceed();
          }

          errorPage = navigationController.accessDenied();
        } catch (FileNotFoundException e) {
          logger.log(Level.SEVERE, "Could not invoke secure interceptor", e);
          errorPage = navigationController.notFound();
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Could not invoke secure interceptor", e);
          errorPage = navigationController.internalError();
        }
  		} else {
        errorPage = navigationController.accessDenied();
  		}
    }
	  
    return errorPage;
	}

	private boolean invokePermissionChecks(final Permission permission, Object object, Method method, Object[] methodParameters) throws FileNotFoundException {
		Object contextParameter = null;
		
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
		
    if (contextParameter == null) {
  		SecurityContext securityContext = getAnnotation(method, object, SecurityContext.class);
  		
  		if (securityContext != null) {
  			if (StringUtils.isNotBlank(securityContext.context())) {
  				contextParameter = resolveParameter(object, securityContext.context());
  			} else {
  				throw new SecurityException("SecurityContext requires a context when used in method body");
  			}
  		}
		}

    if (contextParameter == null) {
      logger.warning("Could not resolve @SecurityContext value in method " + method.toGenericString());
    }
    
		Map<String, String> parameters = new HashMap<>();
		SecurityParams params = getAnnotation(method, object, SecurityParams.class);
		if (params != null) {
  		for (SecurityParam param : params.value()) {
  		  String value = (String) resolveParameter(object, param.value());
  		  parameters.put(param.name(), value);
  		}
		}
		
	  return securityController.checkPermission(permission, contextParameter, parameters);
	}
	
	private <T extends Annotation> T getAnnotation(Method method, Object object, Class<T> annotationClass) {
	  T annotation = method.getAnnotation(annotationClass);
    if (annotation == null) {
      annotation = method.getDeclaringClass().getAnnotation(annotationClass);
    }
    
    if (annotation == null) {
      annotation = object.getClass().getAnnotation(annotationClass);
    }
    
    return annotation;
	}
	
  private Object resolveParameter(Object object, String expression) {
    if (StringUtils.startsWith(expression, "@")) {
      return resolveBeanProperty(object, expression);
    }
    
    return evaluateELExpression(object, expression);
  }
  
	private Object resolveBeanProperty(Object object, String expression) {
	  try {
	    String property = StringUtils.stripStart(expression, "@");
	    
      BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        if (property.equals(propertyDescriptor.getName())) {
          try {
            return propertyDescriptor.getReadMethod().invoke(object);
          } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "Could not resolve bean property", e);
            return null;
          }
        }
      }
    } catch (IntrospectionException e) {
      logger.log(Level.SEVERE, "Could not resolve bean info", e);
    }
	  
	  return null;
  }
	
	private Object evaluateELExpression(Object object, String expression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
    ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, expression, Object.class);
    return valueExpression.getValue(elContext);
	}
}

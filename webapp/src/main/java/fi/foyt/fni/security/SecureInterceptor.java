package fi.foyt.fni.security;

import java.io.Serializable;

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

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Secure secure = ic.getMethod().getAnnotation(Secure.class);
		Permission permission = secure.permission();
		if (sessionController.hasLoggedUserPermission(permission)) {
			return ic.proceed();
		} else {
			throw new ForbiddenException();
		}
	}

}

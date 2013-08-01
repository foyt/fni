package fi.foyt.fni.security;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import fi.foyt.fni.session.SessionController;

@LoggedIn
@Interceptor
public class LoggedInInterceptor implements Serializable {

	private static final long serialVersionUID = -4809267710739056756L;

	@Inject
	private SessionController sessionController;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		if (!sessionController.isLoggedIn()) {
			throw new UnauthorizedException();
		} else {
			return ic.proceed();
		}
	}

}

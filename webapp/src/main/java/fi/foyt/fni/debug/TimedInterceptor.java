package fi.foyt.fni.debug;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Timed
public class TimedInterceptor implements Serializable {

  private static final long serialVersionUID = -7215496352281026138L;

  @Inject
  private DebugTimerCollector debugTimerCollector;

  @AroundInvoke
  public Object aroundInvoke(InvocationContext ic) throws Exception {
    long start = System.currentTimeMillis();
    try {
      return ic.proceed();
    } finally {
      long end = System.currentTimeMillis();
      debugTimerCollector.addCall(ic.getMethod(), end - start);
    }
  }

}

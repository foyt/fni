package fi.foyt.fni.debug;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Timed
public class TimedInterceptor {

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

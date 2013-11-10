package fi.foyt.fni.security;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ PARAMETER, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityParams {

	SecurityParam[] value();
	
}

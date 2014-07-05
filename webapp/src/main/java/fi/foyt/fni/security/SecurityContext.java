package fi.foyt.fni.security;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ PARAMETER, METHOD, TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityContext {

	String context();
	
}

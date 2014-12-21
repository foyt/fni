package fi.foyt.fni.rest;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fi.foyt.fni.rest.illusion.OAuthScopes;

@Target({ METHOD })
@Retention(RUNTIME)
public @interface Security {
  
  OAuthScopes[] scopes();
  boolean allowNotLogged() default false;
  boolean allowService() default false;
  
}

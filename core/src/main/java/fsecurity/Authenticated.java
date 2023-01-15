package fsecurity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
  String hasRole() default "";
  String[] hasAnyRole() default {};
  String[] hasAllRoles() default {};
  Class<? extends AuthenticationHandler> handler() default AuthenticationHandler.class;
}

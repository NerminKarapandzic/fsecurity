package fsecurity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Configuration
@ComponentScan(basePackageClasses = Authenticated.class)
public @interface EnableFsecurity {

}

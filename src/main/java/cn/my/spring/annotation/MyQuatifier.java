package cn.my.spring.annotation;


import java.lang.annotation.*;

@Target({ ElementType.FIELD }) // 代表注解的注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyQuatifier {
    String value() default "";
}

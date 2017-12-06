package cn.my.spring.annotation;


import java.lang.annotation.*;

@Target({ ElementType.METHOD }) // 在方法上的注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}

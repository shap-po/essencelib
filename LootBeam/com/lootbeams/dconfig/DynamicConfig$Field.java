package com.lootbeams.dconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
  String category();
  
  String group() default "default";
  
  double min() default 0.0D;
  
  double max() default 1.7976931348623157E308D;
  
  boolean display() default true;
}


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\DynamicConfig$Field.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
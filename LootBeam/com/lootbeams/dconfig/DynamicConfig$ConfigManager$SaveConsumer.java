package com.lootbeams.dconfig;

@FunctionalInterface
public interface SaveConsumer<T, U> {
  T accept(U paramU);
}


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\DynamicConfig$ConfigManager$SaveConsumer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
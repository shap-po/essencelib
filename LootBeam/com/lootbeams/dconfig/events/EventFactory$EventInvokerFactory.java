package com.lootbeams.dconfig.events;

@FunctionalInterface
public interface EventInvokerFactory<T> {
  T create(T[] paramArrayOfT);
}


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\events\EventFactory$EventInvokerFactory.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
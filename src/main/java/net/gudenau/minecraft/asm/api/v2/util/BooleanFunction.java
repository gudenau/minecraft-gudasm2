package net.gudenau.minecraft.asm.api.v2.util;

@FunctionalInterface
public interface BooleanFunction<T>{
    boolean apply(T t);
}

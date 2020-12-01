package net.gudenau.minecraft.asm.hacks;

import java.lang.invoke.MethodHandle;

public class FieldHandle{
    private final MethodHandle setter;
    private final MethodHandle getter;
    
    FieldHandle(MethodHandle setter, MethodHandle getter){
        this.setter = setter;
        this.getter = getter;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Object instance){
        try{
            return (T)getter.invoke(instance);
        }catch(Throwable t){
            throw new RuntimeException("Failed to get field", t);
        }
    }
    
    public void set(Object instance, Object value){
        try{
            setter.invoke(instance, value);
        }catch(Throwable t){
            throw new RuntimeException("Failed to set field", t);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public <T> T get(){
        try{
            return (T)getter.invoke();
        }catch(Throwable t){
            throw new RuntimeException("Failed to get field", t);
        }
    }
    
    public void set(Object value){
        try{
            setter.invoke(value);
        }catch(Throwable t){
            throw new RuntimeException("Failed to set field", t);
        }
    }
}

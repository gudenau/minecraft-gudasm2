package net.gudenau.minecraft.asm.hacks;

import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Objects;

public class MethodReference{
    private final Class<?> owner;
    private final int modifiers;
    private final Class<?> type;
    private final String name;
    private final Class<?>[] args;
    
    private MethodReference(Class<?> owner, int modifiers, Class<?> type, String name, Class<?>... args){
        this.owner = owner;
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.args = args;
    }
    
    public Class<?> getOwner(){
        return owner;
    }
    
    public int getModifiers(){
        return modifiers;
    }
    
    public Class<?> getType(){
        return type;
    }
    
    public String getName(){
        return name;
    }
    
    public Class<?>[] getArgs(){
        return args;
    }
    
    private MethodType methodType = null;
    public MethodType getMethodType(){
        if(methodType == null){
            methodType = MethodType.methodType(type, args);
        }
        return methodType;
    }
    
    public boolean is(Modifier modifier){
        return (modifiers & modifier.getMask()) != 0;
    }
    
    @Override
    public String toString(){
        return "MethodReference{" +
               "owner=" + owner +
               ", modifiers=" + modifiers +
               ", type=" + type +
               ", name='" + name + '\'' +
               ", args=" + Arrays.toString(args) +
               '}';
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        MethodReference that = (MethodReference)o;
        return modifiers == that.modifiers &&
               Objects.equals(owner, that.owner) &&
               Objects.equals(type, that.type) &&
               Objects.equals(name, that.name) &&
               Arrays.equals(args, that.args);
    }
    
    @Override
    public int hashCode(){
        int result = Objects.hash(owner, modifiers, type, name);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
    
    public static MethodReference of(Class<?> owner, int modifiers, Class<?> type, String name, Class<?>... args){
        return new MethodReference(owner, modifiers, type, name, args);
    }
}

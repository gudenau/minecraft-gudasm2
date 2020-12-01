package net.gudenau.minecraft.asm.hacks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import net.gudenau.minecraft.asm.api.v2.util.Pair;
import net.gudenau.minecraft.asm.util.MiscUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.gudenau.minecraft.asm.Asm.MOD_ID;

public class Reflection{
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    private static final MethodHandles.Lookup IMPL_LOOKUP;
    
    static{
        try{
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            IMPL_LOOKUP = (MethodHandles.Lookup)field.get(null);
        }catch(Throwable e){
            throw new RuntimeException("Failed to access IMPL_LOOKUP", e);
        }
    }
    
    @SafeVarargs
    private static MethodHandle findBoundMethod(Pair<MethodReference, Object>... pairs){
        for(Pair<MethodReference, Object> pair : pairs){
            MethodReference reference = pair.getA();
            MethodHandle handle = null;
            if(reference.is(Modifier.STATIC)){
                try{
                    handle = IMPL_LOOKUP.findStatic(reference.getOwner(), reference.getName(), reference.getMethodType());
                }catch(NoSuchMethodException | IllegalAccessException ignored){}
            }else{
                try{
                    handle = IMPL_LOOKUP.findVirtual(reference.getOwner(), reference.getName(), reference.getMethodType()).bindTo(pair.getB());
                }catch(NoSuchMethodException | IllegalAccessException ignored){}
            }
            if(handle != null){
                return handle;
            }
        }
        LOGGER.fatal("Failed to locate method, searched for");
        for(Pair<MethodReference, Object> pair : pairs){
            MethodReference reference = pair.getA();
            LOGGER.fatal(
                "\t{}.{}({}){}",
                reference.getOwner().getName(),
                reference.getName(),
                MiscUtil.toString(", ", (Object[])reference.getArgs()),
                reference.getType()
            );
        }
        throw new RuntimeException("Failed to locate method");
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Class<T> findClass(String name){
        try{
            return (Class<T>)Class.forName(name);
        }catch(ClassNotFoundException e){
            throw new RuntimeException("Failed to find class " + name, e);
        }
    }
    
    public static FieldHandle findFieldHandle(Class<?> owner, int modifiers, Class<?> type, String name){
        MethodHandle setter;
        MethodHandle getter;
        if(Modifier.is(modifiers, Modifier.STATIC)){
            try{
                setter = IMPL_LOOKUP.findStaticSetter(owner, name, type);
                getter = IMPL_LOOKUP.findStaticGetter(owner, name, type);
            }catch(NoSuchFieldException | IllegalAccessException e){
                throw new RuntimeException("Failed to get MethodHandle for " + owner.getName() + "." + name, e);
            }
        }else{
            try{
                setter = IMPL_LOOKUP.findSetter(owner, name, type);
                getter = IMPL_LOOKUP.findGetter(owner, name, type);
            }catch(NoSuchFieldException | IllegalAccessException e){
                throw new RuntimeException("Failed to get MethodHandle for " + owner.getName() + "." + name, e);
            }
        }
        return new FieldHandle(setter, getter);
    }
}

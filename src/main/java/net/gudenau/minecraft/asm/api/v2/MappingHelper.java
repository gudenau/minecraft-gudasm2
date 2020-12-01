package net.gudenau.minecraft.asm.api.v2;

import net.gudenau.minecraft.asm.impl.MappingHelperImpl;
import org.objectweb.asm.Type;

public interface MappingHelper{
    static MappingHelper getInstance(){
        return MappingHelperImpl.INSTANCE;
    }
    
    String mapClass(String name);
    
    default Type getMappedType(String name){
        return Type.getObjectType(mapClass(name));
    }
}

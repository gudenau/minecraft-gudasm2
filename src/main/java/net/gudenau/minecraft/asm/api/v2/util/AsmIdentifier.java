package net.gudenau.minecraft.asm.api.v2.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class AsmIdentifier implements Comparable<AsmIdentifier>{
    private final String modId;
    private final String name;
    
    public AsmIdentifier(String modId, String name){
        this.modId = modId;
        this.name = name;
    }
    
    public String getModId(){
        return modId;
    }
    
    public String getName(){
        return name;
    }
    
    @Override
    public String toString(){
        return modId + ":" + name;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        AsmIdentifier that = (AsmIdentifier)o;
        return Objects.equals(modId, that.modId) &&
               Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(modId, name);
    }
    
    @Override
    public int compareTo(@NotNull AsmIdentifier o){
        int result = modId.compareTo(o.modId);
        return result != 0 ? result : name.compareTo(o.name);
    }
}

package net.gudenau.minecraft.asm.config;

public enum DumpMode implements EnumConfig.NamedEnum{
    NONE,
    MODIFIED,
    ALL;
    
    @Override
    public String getName(){
        return name().toLowerCase();
    }
}

package net.gudenau.minecraft.asm.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.gudenau.minecraft.asm.api.v2.MappingHelper;

public class MappingHelperImpl implements MappingHelper{
    public static final MappingHelper INSTANCE = new MappingHelperImpl();

    private final MappingResolver resolver;
    
    private MappingHelperImpl(){
        resolver = FabricLoader.getInstance().getMappingResolver();
    }
    
    @Override
    public String mapClass(String name){
        return resolver.mapClassName("intermediary", name.replaceAll("\\/", ".")).replaceAll("\\.", "/");
    }
}

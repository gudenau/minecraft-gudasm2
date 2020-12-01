package net.gudenau.minecraft.asm.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetConfig extends Config<Set<String>>{
    public SetConfig(String name){
        super(name, Collections.emptySet());
    }
    
    @Override
    Set<String> parseValue(String value){
        if(value.isEmpty()){
            return null;
        }
        return new HashSet<>(Arrays.asList(value.split(",")));
    }
    
    @Override
    String getStringValue(){
        StringBuilder builder = new StringBuilder();
        getValue().stream().sorted()
            .forEachOrdered((s)->builder.append(s).append(","));
        if(builder.length() != 0){
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }
}

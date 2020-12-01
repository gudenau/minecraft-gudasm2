package net.gudenau.minecraft.asm.config;

public class EnumConfig<T extends Enum<T> & EnumConfig.NamedEnum> extends Config<T>{
    private final T[] values;
    
    public EnumConfig(String name, Class<T> klass, T defaultValue){
        super(name, defaultValue);
        values = klass.getEnumConstants();
    }
    
    @Override
    String getStringValue(){
        return getValue().getName();
    }
    
    @Override
    T parseValue(String value){
        for(T constant : values){
            if(constant.getName().equalsIgnoreCase(value)){
                return constant;
            }
        }
        return null;
    }
    
    interface NamedEnum{
        String getName();
    }
}

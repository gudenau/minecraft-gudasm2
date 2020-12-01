package net.gudenau.minecraft.asm.hacks;

public enum Modifier{
    PUBLIC(0x00000001),
    PRIVATE(0x00000002),
    PROTECTED(0x00000004),
    STATIC(0x00000008),
    FINAL(0x00000010),
    SYNCHRONIZED(0x00000020),
    VOLATILE(0x00000040),
    TRANSIENT(0x00000080),
    NATIVE(0x00000100),
    INTERFACE(0x00000200),
    ABSTRACT(0x00000400),
    STRICT(0x00000800),
    BRIDGE(0x00000040),
    VARARGS(0x00000080),
    SYNTHETIC(0x00001000),
    ANNOTATION(0x00002000),
    ENUM(0x00004000),
    MANDATED(0x00008000);
    
    private final int mask;
    
    Modifier(int mask){
        this.mask = mask;
    }
    
    public int getMask(){
        return mask;
    }
    
    public static int getMask(Modifier... modifiers){
        int value = 0;
        for(Modifier modifier : modifiers){
            value |= modifier.mask;
        }
        return value;
    }
    
    public static boolean is(int modifiers, Modifier modifier){
        return (modifiers & modifier.mask) != 0;
    }
}

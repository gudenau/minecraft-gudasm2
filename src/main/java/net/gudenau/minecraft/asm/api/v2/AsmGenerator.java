package net.gudenau.minecraft.asm.api.v2;

import org.objectweb.asm.tree.ClassNode;

public interface AsmGenerator extends AsmIdentified{
    
    boolean generatesClass(String name);
    ClassNode generateClass(String name);
}

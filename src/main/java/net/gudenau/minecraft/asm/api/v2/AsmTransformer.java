package net.gudenau.minecraft.asm.api.v2;

import org.objectweb.asm.tree.ClassNode;

public interface AsmTransformer extends AsmIdentified{
    boolean transformsClass(String name);
    boolean transformClass(ClassNode classNode);
}

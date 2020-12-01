package net.gudenau.minecraft.asm.api.v2.asm;

import java.util.Optional;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public final class ClassHelpers{
    public static Optional<ClassNode> findClass(ClassNode classNode, Type name){
        if(name.getInternalName().equals(classNode.name)){
            return Optional.of(classNode);
        }else{
            return Optional.empty();
        }
    }
    
    public static Optional<MethodNode> findMethod(ClassNode classNode, int modifiers, String name, Type description){
        for(MethodNode method : classNode.methods){
            if(
                (modifiers == -1 || method.access == modifiers) &&
                (name == null || method.name.equals(name)) &&
                (description == null || description.getDescriptor().equals(method.desc))
            ){
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }
    
    public static void disableClass(ClassNode classNode){
        classNode.superName = "java/lang/Object";
        classNode.fields.clear();
        classNode.methods.forEach(MethodHelpers::disableMethod);
    }
}

package net.gudenau.minecraft.asm.api.v2.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.gudenau.minecraft.asm.api.v2.util.BooleanFunction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class MethodHelpers{
    private static final Type RuntimeException = Type.getType(RuntimeException.class);
    
    public static void disableMethod(MethodNode method){
        if(method.localVariables != null){
            method.localVariables.clear();
        }
        InsnList instructions = method.instructions;
        instructions.clear();
        instructions.add(CodeGen.createException(RuntimeException, "Method \"" + method.name + "\" was disabled", null));
        method.maxStack = 3;
        method.maxLocals = getArgumentsSize(Type.getMethodType(method.desc));
        if(!MiscHelper.checkAccess(method.access, Opcodes.ACC_STATIC)){
            method.maxLocals++;
        }
    }
    
    public static int getArgumentsSize(Type method){
        int size = 0;
        for(Type argumentType : method.getArgumentTypes()){
            size += argumentType.getSize();
        }
        return size;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends AbstractInsnNode> List<T> findNodes(MethodNode method, BooleanFunction<AbstractInsnNode> filter){
        return MiscHelper.stream(method.instructions)
            .filter(filter::apply)
            .map((n)->(T)n)
            .collect(Collectors.toList());
    }
    
    public static List<TypeInsnNode> findTypeNodes(MethodNode method, int opcode, Type type){
        String desc = type.getInternalName();
        return findNodes(method, (node)->
            node.getType() == AbstractInsnNode.TYPE_INSN && node.getOpcode() == opcode && ((TypeInsnNode)node).desc.equals(desc)
        );
    }
    
    public static List<MethodInsnNode> findMethodNodes(MethodNode method, int opcode, Type owner, String name, Type desc){
        String ownerString = owner == null ? null : owner.getInternalName();
        String descString = desc == null ? null : desc.getDescriptor();
        return findNodes(method, (node)->{
            if(node.getType() != AbstractInsnNode.METHOD_INSN){
                return false;
            }
            MethodInsnNode methodNode = (MethodInsnNode)node;
            return (opcode == -1 || opcode == methodNode.getOpcode()) &&
                   (ownerString == null || ownerString.equals(methodNode.owner)) &&
                   (name == null || name.equals(methodNode.name)) &&
                   (descString == null || descString.equals(methodNode.desc));
        });
    }
    
    public static Optional<MethodInsnNode> findInitNode(TypeInsnNode newNode){
        String owner = newNode.desc;
        AbstractInsnNode currentNode = newNode.getNext();
        int depth = 0;
        while(currentNode != null){
            int currentType = currentNode.getType();
            if(currentType == AbstractInsnNode.METHOD_INSN){
                MethodInsnNode methodNode = (MethodInsnNode)currentNode;
                if(methodNode.owner.equals(owner) && methodNode.name.equals("<init>")){
                    depth--;
                    if(depth == -1){
                        return Optional.of(methodNode);
                    }
                }
            }else if(currentType == AbstractInsnNode.TYPE_INSN){
                TypeInsnNode typeNode = (TypeInsnNode)currentNode;
                if(typeNode.desc.equals(owner)){
                    depth++;
                }
            }
            currentNode = currentNode.getNext();
        }
        return Optional.empty();
    }
    
    public static List<AbstractInsnNode> extractNew(TypeInsnNode newNode){
        List<AbstractInsnNode> nodes = new ArrayList<>();
        String owner = newNode.desc;
        nodes.add(newNode);
        AbstractInsnNode currentNode = newNode.getNext();
        int depth = 0;
        while(currentNode != null){
            nodes.add(currentNode);
            int currentType = currentNode.getType();
            if(currentType == AbstractInsnNode.METHOD_INSN){
                MethodInsnNode methodNode = (MethodInsnNode)currentNode;
                if(methodNode.owner.equals(owner) && methodNode.name.equals("<init>")){
                    depth--;
                    if(depth == -1){
                        break;
                    }
                }
            }else if(currentType == AbstractInsnNode.TYPE_INSN){
                TypeInsnNode typeNode = (TypeInsnNode)currentNode;
                if(typeNode.desc.equals(owner)){
                    depth++;
                }
            }
            currentNode = currentNode.getNext();
        }
        return nodes;
    }
}

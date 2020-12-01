package net.gudenau.minecraft.asm.api.v2.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class CodeGen{
    public static InsnList createException(Type type, String message, VarInsnNode cause){
        InsnList instructions = new InsnList();
        instructions.add(new TypeInsnNode(Opcodes.NEW, type.getInternalName()));
        instructions.add(new InsnNode(Opcodes.DUP));
        if(message != null){
            instructions.add(new LdcInsnNode(message));
        }
        if(cause != null){
            instructions.add(new VarInsnNode(Opcodes.ALOAD, cause.var));
        }
        String descriptor;
        if(message != null){
            if(cause != null){
                descriptor = "(Ljava/lang/String;Ljava/lang/Throwable;)V";
            }else{
                descriptor = "(Ljava/lang/String;)V";
            }
        }else{
            if(cause != null){
                descriptor = "(Ljava/lang/Throwable;)V";
            }else{
                descriptor = "()V";
            }
        }
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, type.getInternalName(), "<init>", descriptor, false));
        instructions.add(new InsnNode(Opcodes.ATHROW));
        return instructions;
    }
}

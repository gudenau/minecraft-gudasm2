package net.gudenau.minecraft.asm.api.v2.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public final class MiscUtil{
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <R, O> R ifPresent(Optional<O> optional, Function<O, R> task, Supplier<R> other){
        return optional.isPresent() ? task.apply(optional.get()) : other.get();
    }
    
    public static void dumpInstructions(InsnList instructions){
        for(AbstractInsnNode instruction : instructions){
            dumpInstruction(instruction);
        }
    }
    
    public static void dumpInstruction(AbstractInsnNode instruction){
        StringBuilder builder = new StringBuilder(opcodeName(instruction.getOpcode()));
        switch(instruction.getType()){
            case AbstractInsnNode.INSN:{
            
            } break;
            case AbstractInsnNode.INT_INSN:{
                builder.append(" ").append(((IntInsnNode)instruction).operand);
            } break;
            case AbstractInsnNode.VAR_INSN:{
                builder.append(" ").append(((VarInsnNode)instruction).var);
            } break;
            case AbstractInsnNode.TYPE_INSN:{
                builder.append(" ").append(((TypeInsnNode)instruction).desc);
            } break;
            case AbstractInsnNode.FIELD_INSN:{
                FieldInsnNode node = (FieldInsnNode)instruction;
                builder.append(" ").append(node.owner).append(" ").append(node.desc).append(" ").append(node.name);
            } break;
            case AbstractInsnNode.METHOD_INSN:{
                MethodInsnNode node = (MethodInsnNode)instruction;
                builder.append(" ").append(node.owner).append(" ").append(node.name).append(" ").append(node.desc);
            } break;
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:{
    
            } break;
            case AbstractInsnNode.JUMP_INSN:{
                builder.append(" ").append(((JumpInsnNode)instruction).label.getLabel().hashCode());
            } break;
            case AbstractInsnNode.LABEL:{
                builder.setLength(0);
                builder.append("LABEL ").append(((LabelNode)instruction).getLabel().hashCode());
            } break;
            case AbstractInsnNode.LDC_INSN:{
                builder.append(" ").append(((LdcInsnNode)instruction).cst);
            } break;
            case AbstractInsnNode.IINC_INSN:{
    
            } break;
            case AbstractInsnNode.TABLESWITCH_INSN:{
    
            } break;
            case AbstractInsnNode.LOOKUPSWITCH_INSN:{
    
            } break;
            case AbstractInsnNode.MULTIANEWARRAY_INSN:{
    
            } break;
            case AbstractInsnNode.FRAME:{
                builder.setLength(0);
                FrameNode node = (FrameNode)instruction;
                switch(node.type){
                    case Opcodes.F_NEW:{
                        builder.append("F_NEW");
                    } break;
                    case Opcodes.F_FULL:{
                        builder.append("F_FULL");
                    } break;
                    case Opcodes.F_APPEND:{
                        builder.append("F_APPEND");
                    } break;
                    case Opcodes.F_CHOP:{
                        builder.append("F_CHOP");
                    } break;
                    case Opcodes.F_SAME:{
                        builder.append("F_SAME");
                    } break;
                    case Opcodes.F_SAME1:{
                        builder.append("F_SAME1");
                    } break;
                    default:{
                        builder.append("F_UNKNOWN (").append(node.type).append(")");
                    } break;
                }
                
                builder.append(" [");
                List<Object> stack = node.local;
                if(stack != null && !stack.isEmpty()){
                    for(Object o : stack){
                        builder.append(" ").append(o).append(",");
                    }
                    builder.setLength(builder.length() - 1);
                    builder.append(" ");
                }
                builder.append("] [");
                stack = node.stack;
                if(stack != null && !stack.isEmpty()){
                    for(Object o : stack){
                        builder.append(" ").append(o).append(",");
                    }
                    builder.setLength(builder.length() - 1);
                    builder.append(" ");
                }
                builder.append("]");
            } break;
            case AbstractInsnNode.LINE:{
                builder.setLength(0);
                LineNumberNode node = (LineNumberNode)instruction;
                builder.append("LINENUMBER ").append(node.line).append(" ").append(node.start.getLabel().hashCode());
            } break;
        }
        System.out.println(builder.toString());
    }
    
    private static String opcodeName(int opcode){
        switch(opcode){
            case Opcodes.NOP: return "NOP";
            case Opcodes.ACONST_NULL: return "ACONST_NULL";
            case Opcodes.ICONST_M1: return "ICONST_M1";
            case Opcodes.ICONST_0: return "ICONST_0";
            case Opcodes.ICONST_1: return "ICONST_1";
            case Opcodes.ICONST_2: return "ICONST_2";
            case Opcodes.ICONST_3: return "ICONST_3";
            case Opcodes.ICONST_4: return "ICONST_4";
            case Opcodes.ICONST_5: return "ICONST_5";
            case Opcodes.LCONST_0: return "LCONST_0";
            case Opcodes.LCONST_1: return "LCONST_1";
            case Opcodes.FCONST_0: return "FCONST_0";
            case Opcodes.FCONST_1: return "FCONST_1";
            case Opcodes.FCONST_2: return "FCONST_2";
            case Opcodes.DCONST_0: return "DCONST_0";
            case Opcodes.DCONST_1: return "DCONST_1";
            case Opcodes.BIPUSH: return "BIPUSH";
            case Opcodes.SIPUSH: return "SIPUSH";
            case Opcodes.LDC: return "LDC";
            case Opcodes.ILOAD: return "ILOAD";
            case Opcodes.LLOAD: return "LLOAD";
            case Opcodes.FLOAD: return "FLOAD";
            case Opcodes.DLOAD: return "DLOAD";
            case Opcodes.ALOAD: return "ALOAD";
            case Opcodes.IALOAD: return "IALOAD";
            case Opcodes.LALOAD: return "LALOAD";
            case Opcodes.FALOAD: return "FALOAD";
            case Opcodes.DALOAD: return "DALOAD";
            case Opcodes.AALOAD: return "AALOAD";
            case Opcodes.BALOAD: return "BALOAD";
            case Opcodes.CALOAD: return "CALOAD";
            case Opcodes.SALOAD: return "SALOAD";
            case Opcodes.ISTORE: return "ISTORE";
            case Opcodes.LSTORE: return "LSTORE";
            case Opcodes.FSTORE: return "FSTORE";
            case Opcodes.DSTORE: return "DSTORE";
            case Opcodes.ASTORE: return "ASTORE";
            case Opcodes.IASTORE: return "IASTORE";
            case Opcodes.LASTORE: return "LASTORE";
            case Opcodes.FASTORE: return "FASTORE";
            case Opcodes.DASTORE: return "DASTORE";
            case Opcodes.AASTORE: return "AASTORE";
            case Opcodes.BASTORE: return "BASTORE";
            case Opcodes.CASTORE: return "CASTORE";
            case Opcodes.SASTORE: return "SASTORE";
            case Opcodes.POP: return "POP";
            case Opcodes.POP2: return "POP2";
            case Opcodes.DUP: return "DUP";
            case Opcodes.DUP_X1: return "DUP_X1";
            case Opcodes.DUP_X2: return "DUP_X2";
            case Opcodes.DUP2: return "DUP2";
            case Opcodes.DUP2_X1: return "DUP2_X1";
            case Opcodes.DUP2_X2: return "DUP2_X2";
            case Opcodes.SWAP: return "SWAP";
            case Opcodes.IADD: return "IADD";
            case Opcodes.LADD: return "LADD";
            case Opcodes.FADD: return "FADD";
            case Opcodes.DADD: return "DADD";
            case Opcodes.ISUB: return "ISUB";
            case Opcodes.LSUB: return "LSUB";
            case Opcodes.FSUB: return "FSUB";
            case Opcodes.DSUB: return "DSUB";
            case Opcodes.IMUL: return "IMUL";
            case Opcodes.LMUL: return "LMUL";
            case Opcodes.FMUL: return "FMUL";
            case Opcodes.DMUL: return "DMUL";
            case Opcodes.IDIV: return "IDIV";
            case Opcodes.LDIV: return "LDIV";
            case Opcodes.FDIV: return "FDIV";
            case Opcodes.DDIV: return "DDIV";
            case Opcodes.IREM: return "IREM";
            case Opcodes.LREM: return "LREM";
            case Opcodes.FREM: return "FREM";
            case Opcodes.DREM: return "DREM";
            case Opcodes.INEG: return "INEG";
            case Opcodes.LNEG: return "LNEG";
            case Opcodes.FNEG: return "FNEG";
            case Opcodes.DNEG: return "DNEG";
            case Opcodes.ISHL: return "ISHL";
            case Opcodes.LSHL: return "LSHL";
            case Opcodes.ISHR: return "ISHR";
            case Opcodes.LSHR: return "LSHR";
            case Opcodes.IUSHR: return "IUSHR";
            case Opcodes.LUSHR: return "LUSHR";
            case Opcodes.IAND: return "IAND";
            case Opcodes.LAND: return "LAND";
            case Opcodes.IOR: return "IOR";
            case Opcodes.LOR: return "LOR";
            case Opcodes.IXOR: return "IXOR";
            case Opcodes.LXOR: return "LXOR";
            case Opcodes.IINC: return "IINC";
            case Opcodes.I2L: return "I2L";
            case Opcodes.I2F: return "I2F";
            case Opcodes.I2D: return "I2D";
            case Opcodes.L2I: return "L2I";
            case Opcodes.L2F: return "L2D";
            case Opcodes.L2D: return "L2D";
            case Opcodes.F2I: return "F2I";
            case Opcodes.F2L: return "F2L";
            case Opcodes.F2D: return "F2D";
            case Opcodes.D2I: return "D2I";
            case Opcodes.D2L: return "D2L";
            case Opcodes.D2F: return "D2F";
            case Opcodes.I2B: return "I2B";
            case Opcodes.I2C: return "I2C";
            case Opcodes.I2S: return "I2S";
            case Opcodes.LCMP: return "LCMP";
            case Opcodes.FCMPL: return "FCMPL";
            case Opcodes.FCMPG: return "FCMPG";
            case Opcodes.DCMPL: return "DCMPL";
            case Opcodes.DCMPG: return "DCMPG";
            case Opcodes.IFEQ: return "IFEQ";
            case Opcodes.IFNE: return "IFNE";
            case Opcodes.IFLT: return "IFLT";
            case Opcodes.IFGE: return "IFGE";
            case Opcodes.IFGT: return "IFGT";
            case Opcodes.IFLE: return "IFLE";
            case Opcodes.IF_ICMPEQ: return "IF_ICMPEQ";
            case Opcodes.IF_ICMPNE: return "IF_ICMPNE";
            case Opcodes.IF_ICMPLT: return "IF_ICMPLT";
            case Opcodes.IF_ICMPGE: return "IF_ICMPGE";
            case Opcodes.IF_ICMPGT: return "IF_ICMPGT";
            case Opcodes.IF_ICMPLE: return "IF_ICMPLE";
            case Opcodes.IF_ACMPEQ: return "IF_ACMPEQ";
            case Opcodes.IF_ACMPNE: return "IF_ACMPNE";
            case Opcodes.GOTO: return "GOTO";
            case Opcodes.JSR: return "JSR";
            case Opcodes.RET: return "RET";
            case Opcodes.TABLESWITCH: return "TABLESWITCH";
            case Opcodes.LOOKUPSWITCH: return "LOOKUPSWITCH";
            case Opcodes.IRETURN: return "IRETURN";
            case Opcodes.LRETURN: return "LRETURN";
            case Opcodes.FRETURN: return "FRETURN";
            case Opcodes.DRETURN: return "DRETURN";
            case Opcodes.ARETURN: return "ARETURN";
            case Opcodes.RETURN: return "RETURN";
            case Opcodes.GETSTATIC: return "GETSTATIC";
            case Opcodes.PUTSTATIC: return "PUTSTATIC";
            case Opcodes.GETFIELD: return "GETFIELD";
            case Opcodes.PUTFIELD: return "PUTFIELD";
            case Opcodes.INVOKEVIRTUAL: return "INVOKEVIRTUAL";
            case Opcodes.INVOKESPECIAL: return "INVOKESPECIAL";
            case Opcodes.INVOKESTATIC: return "INVOKESTATIC";
            case Opcodes.INVOKEINTERFACE: return "INVOKEINTERFACE";
            case Opcodes.INVOKEDYNAMIC: return "INVOKEDYNAMIC";
            case Opcodes.NEW: return "NEW";
            case Opcodes.NEWARRAY: return "NEWARRAY";
            case Opcodes.ANEWARRAY: return "ANEWARRAY";
            case Opcodes.ARRAYLENGTH: return "ARRAYLENGTH";
            case Opcodes.ATHROW: return "ATHROW";
            case Opcodes.CHECKCAST: return "CHESTCAST";
            case Opcodes.INSTANCEOF: return "INSTANCEOF";
            case Opcodes.MONITORENTER: return "MONITORETER";
            case Opcodes.MONITOREXIT: return "MONITOREXIT";
            case Opcodes.MULTIANEWARRAY: return "MULTIANEWARRAY";
            case Opcodes.IFNULL: return "IFNULL";
            case Opcodes.IFNONNULL: return "IFNONNULL";
            default: return "UNKNOWN (" + opcode + ")";
        }
    }
}

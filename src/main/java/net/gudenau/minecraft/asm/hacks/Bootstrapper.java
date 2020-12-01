package net.gudenau.minecraft.asm.hacks;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.gudenau.minecraft.asm.api.v2.AsmGenerator;
import net.gudenau.minecraft.asm.api.v2.AsmTransformer;
import net.gudenau.minecraft.asm.config.Config;
import net.gudenau.minecraft.asm.config.DumpMode;
import net.gudenau.minecraft.asm.impl.TransformerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import static net.gudenau.minecraft.asm.Asm.MOD_ID;

public class Bootstrapper{
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    @SuppressWarnings("unchecked") // WHY
    private static final Set<String> BLACKLIST = (Set<String>)(Object)ImmutableSet.builder()
        .add("net.gudenau.minecraft.asm.")
        .add("net.fabricmc.loader.")
        .build();
    
    public static void bootstrap(){
        ClassLoader classLoader = Bootstrapper.class.getClassLoader();
        String classLoaderName = classLoader.getClass().getName();
        if(classLoaderName.equals("net.fabricmc.loader.launch.knot.KnotClassLoader")){
            bootstrapKnotClassLoader();
        }else{
            throw new RuntimeException("gudASM was loaded under a non-supported ClassLoader: " + classLoaderName);
        }
        
        TransformerRegistry.init();
    }
    
    private static void bootstrapKnotClassLoader(){
        // Step 1, get handles to all of the classes.
        Class<?> KnotClassLoader = Reflection.findClass("net.fabricmc.loader.launch.knot.KnotClassLoader");
        Class<?> KnotClassDelegate = Reflection.findClass("net.fabricmc.loader.launch.knot.KnotClassDelegate");
        
        // Step 2, get handles to all of the fields we need
        FieldHandle KnotClassLoader$delegate = Reflection.findFieldHandle(KnotClassLoader, Modifier.getMask(Modifier.PRIVATE, Modifier.FINAL), KnotClassDelegate, "delegate");
        FieldHandle KnotClassDelegate$mixinTransformer = Reflection.findFieldHandle(KnotClassDelegate, Modifier.getMask(Modifier.PRIVATE), FabricMixinTransformerProxy.class, "mixinTransformer");
        FieldHandle MixinEnvironment$transformer = Reflection.findFieldHandle(MixinEnvironment.class, Modifier.getMask(Modifier.PRIVATE, Modifier.STATIC), IMixinTransformer.class, "transformer");
        
        // Step 3, hack it all
        ClassLoader classLoader = Bootstrapper.class.getClassLoader();
        Object delegate = KnotClassLoader$delegate.get(classLoader);
        FabricMixinTransformerProxy mixinTransformer = KnotClassDelegate$mixinTransformer.get(delegate);
        IMixinTransformer originalTransformer = MixinEnvironment$transformer.get();
        MixinEnvironment$transformer.set(null);
        BootstrapProxy proxy = new BootstrapProxy(mixinTransformer);
        MixinEnvironment$transformer.set(originalTransformer);
        KnotClassDelegate$mixinTransformer.set(delegate, proxy);
    }
    
    private static class BootstrapProxy extends FabricMixinTransformerProxy{
        private final FabricMixinTransformerProxy parent;
    
        private BootstrapProxy(FabricMixinTransformerProxy parent){
            this.parent = parent;
        }
    
        @Override
        public byte[] transformClassBytes(String name, String transformedName, byte[] originalClass){
            if(isBlacklisted(name, transformedName)){
                return parent.transformClassBytes(name, transformedName, originalClass);
            }
            
            byte[] newClass = doTransform(name, transformedName, originalClass);
            DumpMode dumpMode = Config.DUMP_MODE.getValue();
            if(dumpMode == DumpMode.MODIFIED && newClass != originalClass || dumpMode == DumpMode.ALL){
                dumpClass(name, newClass);
            }
            return newClass;
        }
        
        private static final Path DUMP_PATH = FabricLoader.getInstance().getGameDir().resolve("gudAsmDump");
        private static void dumpClass(String name, byte[] klass){
            if(klass == null){
                return;
            }
    
            try{
                Path classPath = DUMP_PATH;
                String[] split = name.split("\\.");
                int length = split.length - 1;
                for(int i = 0; i < length; i++){
                    classPath = classPath.resolve(split[i]);
                }
                classPath = classPath.resolve(split[length] + ".class");
                synchronized(DUMP_PATH){
                    if(!Files.exists(classPath)){
                        Files.createDirectories(classPath.getParent());
                    }
                }
                try(OutputStream stream = Files.newOutputStream(classPath, StandardOpenOption.CREATE)){
                    stream.write(klass);
                }
            }catch(IOException e){
                LOGGER.error("Failed to dump class " + name, e);
            }
        }
        
        private byte[] doTransform(String name, String transformedName, byte[] klass){
            klass = transform(name, klass, true);
            klass = parent.transformClassBytes(name, transformedName, klass);
            return transform(name, klass, false);
        }
    
        private static boolean isBlacklisted(String name, String transformedName){
            if(name.equals(transformedName)){
                for(String entry : BLACKLIST){
                    if(name.startsWith(entry)){
                        return true;
                    }
                }
            }else{
                for(String entry : BLACKLIST){
                    if(name.startsWith(entry) || transformedName.startsWith(entry)){
                        return true;
                    }
                }
            }
            return false;
        }
    
        private static byte[] transform(String name, byte[] klass, boolean early){
            ClassNode classNode = null;
            if(klass == null){
                for(AsmGenerator generator : TransformerRegistry.getGenerators(early, name)){
                    classNode = generator.generateClass(name);
                    if(classNode != null){
                        break;
                    }
                }
            }
    
            List<AsmTransformer> transformers = TransformerRegistry.getTransformers(early, name);
            if(!transformers.isEmpty()){
                if(classNode == null){
                    classNode = readClass(klass);
                }
                if(classNode != null){
                    boolean changed = false;
                    for(AsmTransformer transformer : transformers){
                        changed |= transformer.transformClass(classNode);
                    }
                    if(changed){
                        klass = writeClass(classNode);
                        classNode = null;
                    }
                }
            }
            if(classNode != null && klass == null){
                klass = writeClass(classNode);
            }
            return klass;
        }
    
        private static byte[] writeClass(ClassNode classNode){
            ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }
    
        private static ClassNode readClass(byte[] klass){
            if(klass == null){
                return null;
            }
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(klass);
            reader.accept(node, 0);
            return node;
        }
    }
}

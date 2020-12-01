package net.gudenau.minecraft.asm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.gudenau.minecraft.asm.api.v2.AsmGenerator;
import net.gudenau.minecraft.asm.api.v2.AsmIdentified;
import net.gudenau.minecraft.asm.api.v2.AsmInitializer;
import net.gudenau.minecraft.asm.api.v2.AsmTransformer;
import net.gudenau.minecraft.asm.api.v2.util.AsmIdentifier;
import net.gudenau.minecraft.asm.config.Config;
import net.gudenau.minecraft.asm.util.MiscUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.gudenau.minecraft.asm.Asm.MOD_ID;

public class TransformerRegistry{
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    private static List<AsmTransformer> earlyTransformers;
    private static List<AsmTransformer> transformers;
    private static List<AsmGenerator> earlyGenerators;
    private static List<AsmGenerator> generators;
    
    public static void init(){
        LOGGER.info("Initializing AsmInitializers...");
        List<EntrypointContainer<AsmInitializer>> entries = FabricLoader.getInstance().getEntrypointContainers("gud_asm", AsmInitializer.class);
        Set<EntrypointContainer<AsmInitializer>> failures = new HashSet<>();
        for(EntrypointContainer<AsmInitializer> entry : entries){
            AsmInitializer init = entry.getEntrypoint();
            try{
                init.init();
            }catch(Throwable t){
                LOGGER.error(String.format(
                    "AsmInitializer %s from %s threw an exception",
                    init.getClass().getName(),
                    entry.getProvider().getMetadata().getId()
                ), t);
                failures.add(entry);
            }
        }
        entries.removeAll(failures);
    
        earlyTransformers = entries.stream()
            .flatMap((entry)->{
                AsmInitializer initializer = entry.getEntrypoint();
                String id = entry.getProvider().getMetadata().getId();
                
                return filterBadTransformers(
                    id,
                    initializer.getEarlyTransformers(),
                    "Mod {} provided an early transformer {} which had an incorrect modId, it will be disabled"
                );
            })
            .collect(Collectors.toList());
    
        transformers = entries.stream()
            .flatMap((entry)->{
                AsmInitializer initializer = entry.getEntrypoint();
                String id = entry.getProvider().getMetadata().getId();
            
                return filterBadTransformers(
                    id,
                    initializer.getTransformers(),
                    "Mod {} provided a transformer {} which had an incorrect modId, it will be disabled"
                );
            })
            .collect(Collectors.toList());
    
        earlyGenerators = entries.stream()
            .flatMap((entry)->{
                AsmInitializer initializer = entry.getEntrypoint();
                String id = entry.getProvider().getMetadata().getId();
            
                return filterBadTransformers(
                    id,
                    initializer.getEarlyGenerators(),
                    "Mod {} provided an early generator {} which had an incorrect modId, it will be disabled"
                );
            })
            .collect(Collectors.toList());
    
        generators = entries.stream()
            .flatMap((entry)->{
                AsmInitializer initializer = entry.getEntrypoint();
                String id = entry.getProvider().getMetadata().getId();
            
                return filterBadTransformers(
                    id,
                    initializer.getGenerators(),
                    "Mod {} provided a generator {} which had an incorrect modId, it will be disabled"
                );
            })
            .collect(Collectors.toList());
    }
    
    private static <T extends AsmIdentified> Stream<T> filterBadTransformers(String id, Collection<T> transformers, String message){
        return transformers.stream()
            .filter((transformer)->{
                AsmIdentifier identifier = transformer.getId();
                if(identifier.getModId().equals(id)){
                    return !Config.BLACKLIST.getValue().contains(identifier.toString());
                }else{
                    LOGGER.error(
                        message,
                        id, transformer.getId()
                    );
                    return false;
                }
            });
    }
    
    public static List<AsmGenerator> getGenerators(boolean early, String name){
        List<AsmGenerator> generators = early ? earlyGenerators : TransformerRegistry.generators;
        if(generators == null){
            return Collections.emptyList();
        }
        List<AsmGenerator> result = new ArrayList<>();
        for(AsmGenerator generator : generators){
            if(generator.generatesClass(name)){
                result.add(generator);
            }
        }
        return result.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(result);
    }
    
    public static List<AsmTransformer> getTransformers(boolean early, String name){
        List<AsmTransformer> transformers = early ? earlyTransformers : TransformerRegistry.transformers;
        if(transformers == null){
            return Collections.emptyList();
        }
        List<AsmTransformer> result = new ArrayList<>();
        for(AsmTransformer transformer : transformers){
            if(transformer.transformsClass(name)){
                result.add(transformer);
            }
        }
        return result.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(result);
    }
    
    public static List<AsmTransformer> getLoadedTransformers(){
        return MiscUtil.merge(earlyTransformers, transformers);
    }
    
    public static List<AsmGenerator> getLoadedGenerators(){
        return MiscUtil.merge(earlyGenerators, generators);
    }
}

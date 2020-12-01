package net.gudenau.minecraft.asm.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.gudenau.minecraft.asm.api.v2.AsmGenerator;
import net.gudenau.minecraft.asm.api.v2.AsmTransformer;
import net.gudenau.minecraft.asm.impl.TransformerRegistry;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CrashReport.class, priority = 999)
public abstract class CrashReportMixin{
    @Shadow @Final private CrashReportSection systemDetailsSection;
    
    @Inject(
        method = "fillSystemDetails",
        at = @At("RETURN")
    )
    private void fillSystemDetails(CallbackInfo callbackInfo){
        systemDetailsSection.add("gudASM Transformers", ()->{
            Map<String, List<AsmTransformer>> transformerMap = new HashMap<>();
            for(AsmTransformer transformer : TransformerRegistry.getLoadedTransformers()){
                transformerMap.computeIfAbsent(
                    transformer.getId().getModId(),
                    (t)->new ArrayList<>()
                ).add(transformer);
            }
            
            StringBuilder builder = new StringBuilder();
            transformerMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered((e)->{
                    builder.append("\n\t\t").append(e.getKey()).append(":");
                    e.getValue().stream().sorted()
                        .forEachOrdered((t)->builder.append("\n\t\t\t").append(t.getId().getName()));
                });
            return builder.toString();
        });
        systemDetailsSection.add("gudASM Generators", ()->{
            Map<String, List<AsmGenerator>> generatorMap = new HashMap<>();
            for(AsmGenerator generator : TransformerRegistry.getLoadedGenerators()){
                generatorMap.computeIfAbsent(
                    generator.getId().getModId(),
                    (t)->new ArrayList<>()
                ).add(generator);
            }
        
            StringBuilder builder = new StringBuilder();
            generatorMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered((e)->{
                    builder.append("\n\t\t").append(e.getKey()).append(":");
                    e.getValue().stream().sorted()
                        .forEachOrdered((t)->builder.append("\n\t\t\t").append(t.getId().getName()));
                });
            return builder.toString();
        });
    }
}

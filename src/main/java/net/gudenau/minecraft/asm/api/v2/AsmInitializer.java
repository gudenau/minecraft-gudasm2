package net.gudenau.minecraft.asm.api.v2;

import java.util.Collections;
import java.util.List;

public interface AsmInitializer{
    void init();
    List<AsmTransformer> getTransformers();
    default List<AsmTransformer> getEarlyTransformers(){
        return Collections.emptyList();
    }
    default List<AsmGenerator> getGenerators(){
        return Collections.emptyList();
    }
    default List<AsmGenerator> getEarlyGenerators(){
        return Collections.emptyList();
    }
}

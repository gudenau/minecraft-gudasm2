package net.gudenau.minecraft.asm.hacks;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;

import static net.gudenau.minecraft.asm.Asm.MOD_ID;

@SuppressWarnings("unused")
public class HackAdapter implements LanguageAdapter{
    static{
        LogManager.getLogger(MOD_ID).warn("gudASM is present, things might get a little weird!");
    }
    
    @Override
    public <T> T create(ModContainer modContainer, String s, Class<T> aClass){
        throw new RuntimeException("https://www.youtube.com/watch?v=rb8z2BMrd60");
    }
}

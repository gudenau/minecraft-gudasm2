package net.gudenau.minecraft.asm.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.gudenau.minecraft.asm.Asm.MOD_ID;

public abstract class Config<T>{
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final List<Config<?>> VALUES = new ArrayList<>();
    
    public static final Config<DumpMode> DUMP_MODE = new EnumConfig<>("dump", DumpMode.class, DumpMode.NONE);
    public static final Config<Set<String>> BLACKLIST = new SetConfig("blacklist");
    
    static{
        VALUES.sort((a, b)->a.name.compareToIgnoreCase(b.name));
        Map<String, String> values = new HashMap<>();
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("gud").resolve("asm.cfg");
        if(Files.exists(configFile)){
            try(BufferedReader reader = Files.newBufferedReader(configFile)){
                for(String line = reader.readLine(); line != null; line = reader.readLine()){
                    if(line.isEmpty()){
                        continue;
                    }
                    String[] split = line.split("=");
                    if(split.length > 1){
                        values.put(split[0], split[1]);
                    }else{
                        values.put(split[0], "");
                    }
                }
            }catch(IOException e){
                LOGGER.error("Failed to read config file", e);
            }
        }
        boolean changed = false;
        for(Config<?> config : VALUES){
            String readValue = values.get(config.name);
            if(readValue != null){
                config.parse(readValue);
            }
            String currentValue = config.getStringValue();
            if(!currentValue.equalsIgnoreCase(readValue)){
                changed = true;
                values.put(config.name, currentValue);
            }
        }
        if(changed){
            Path parent = configFile.getParent();
            try{
                if(!Files.exists(parent)){
                    Files.createDirectories(parent);
                }
                try(BufferedWriter writer = Files.newBufferedWriter(configFile)){
                    for(Config<?> config : VALUES){
                        writer.write(config.name);
                        writer.write("=");
                        writer.write(config.getStringValue());
                        writer.write("\n");
                    }
                }
            }catch(IOException e){
                LOGGER.error("Failed to write config file", e);
            }
        }
    }
    
    private final String name;
    private T value;
    
    protected Config(String name, T defaultValue){
        VALUES.add(this);
        this.name = name;
        this.value = defaultValue;
    }
    
    abstract T parseValue(String value);
    abstract String getStringValue();
    
    public final T getValue(){
        return value;
    }
    
    private void parse(String value){
        T parsedValue = parseValue(value);
        if(parsedValue != null){
            this.value = parsedValue;
        }
    }
}

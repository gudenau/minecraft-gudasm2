package net.gudenau.minecraft.asm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MiscUtil{
    public static String toString(String joiner, Object... data){
        StringBuilder builder = new StringBuilder();
        int length = data.length - 1;
        for(int i = 0; i < length; i++){
            builder.append(data[i]).append(joiner);
        }
        builder.append(data[length]);
        return builder.toString();
    }
    
    @SafeVarargs
    public static <T> List<T> merge(Collection<T>... collections){
        List<T> result = new ArrayList<>();
        for(Collection<T> collection : collections){
            result.addAll(collection);
        }
        return result;
    }
}

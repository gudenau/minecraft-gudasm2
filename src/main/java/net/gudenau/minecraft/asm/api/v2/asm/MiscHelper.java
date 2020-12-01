package net.gudenau.minecraft.asm.api.v2.asm;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import org.objectweb.asm.tree.InsnList;

public final class MiscHelper{
    public static boolean checkAccess(int access, int modifier){
        return (access & modifier) != 0;
    }
    
    public static <T> Stream<T> stream(Iterable<T> iterable){
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}

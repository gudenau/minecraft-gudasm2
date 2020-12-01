package net.gudenau.minecraft.asm.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.tag.Tag;
import net.minecraft.util.Nameable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, CommandOutput{
    @Shadow protected Object2DoubleMap<Tag<Fluid>> fluidHeight;
    
    @Inject(
        method = "updateWaterState",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/entity/Entity;fluidHeight:Lit/unimi/dsi/fastutil/objects/Object2DoubleMap;",
            shift = At.Shift.AFTER
        )
    )
    private void updateWaterState(CallbackInfoReturnable<Boolean> cir){
        fluidHeight = null;
    }
}

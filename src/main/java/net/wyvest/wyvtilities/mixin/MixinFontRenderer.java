package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.wyvest.wyvtilities.listeners.FontRendererEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

    @Unique private FontRendererEvent wyvtils$fontrendererevent;

    @Inject(method = "renderString", at = @At("HEAD"), cancellable = true)
    private void onStringRendered(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        FontRendererEvent event = new FontRendererEvent(text);
        MinecraftForge.EVENT_BUS.post(event);
        this.wyvtils$fontrendererevent = event;
    }

    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    public String renderString_modifyText(String original) {
        return wyvtils$fontrendererevent.getText();
    }

}

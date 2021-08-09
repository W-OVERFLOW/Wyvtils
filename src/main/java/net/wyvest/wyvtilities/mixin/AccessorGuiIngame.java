package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiIngame.class)
public interface AccessorGuiIngame {

    @Accessor
    String getRecordPlaying();

    @Accessor
    String getDisplayedTitle();

    @Accessor
    void setDisplayedTitle(String title);
}
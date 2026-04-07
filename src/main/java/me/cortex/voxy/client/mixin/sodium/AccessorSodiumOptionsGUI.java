package me.cortex.voxy.client.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SodiumOptionsScreen.class)
public interface AccessorSodiumOptionsGUI {
    @Invoker(value = "<init>")
    static SodiumOptionsScreen newScreen(Screen currentScreen) {
        throw new AssertionError(); // Used for Embeddium support
    }
}

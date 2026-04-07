package me.cortex.voxy.client.core.rendering;

import me.cortex.voxy.client.core.util.IrisUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewportSelector <T extends Viewport<?>> {
    public static final boolean VIVECRAFT_INSTALLED = FabricLoader.getInstance().isModLoaded("vivecraft");

    private final Supplier<T> creator;
    private final T defaultViewport;
    private final Map<Object, T> extraViewports = new HashMap<>();//TODO should maybe be a weak hashmap with value cleanup queue thing?

    public ViewportSelector(Supplier<T> viewportCreator) {
        this.creator = viewportCreator;
        this.defaultViewport = viewportCreator.get();
    }

    private T getOrCreate(Object holder) {
        return this.extraViewports.computeIfAbsent(holder, a->this.creator.get());
    }

    private T getVivecraftViewport() {
        try {
            Class<?> apiClass = Class.forName("org.vivecraft.api.client.VRRenderingAPI");
            Method instanceMethod = apiClass.getMethod("instance");
            Object api = instanceMethod.invoke(null);
            Method passMethod = apiClass.getMethod("getCurrentRenderPass");
            Object pass = passMethod.invoke(api);
            if (pass == null) return null;
            Class<?> vanillaClass = Class.forName("org.vivecraft.api.client.data.RenderPass");
            Object vanillaValue = vanillaClass.getField("VANILLA").get(null);
            if (pass == vanillaValue) return null;
            return this.getOrCreate(pass);
        } catch (Exception e) {
            return null;
        }
    }

    private static final Object IRIS_SHADOW_OBJECT = new Object();
    public T getViewport() {
        T viewport = null;
        if (viewport == null && VIVECRAFT_INSTALLED) {
            viewport = getVivecraftViewport();
        }

        if (viewport == null && IrisUtil.irisShadowActive()) {
            viewport = this.getOrCreate(IRIS_SHADOW_OBJECT);
        }

        if (viewport == null) {
            viewport = this.defaultViewport;
        }
        return viewport;
    }

    public void free() {
        this.defaultViewport.delete();
        this.extraViewports.values().forEach(Viewport::delete);
        this.extraViewports.clear();
    }
}

package net.pneumono.locator_lodestones.config;

import net.minecraft.util.ARGB;
import net.pneumono.locator_lodestones.LocatorLodestones;
import org.jetbrains.annotations.Nullable;

public record ColorProvider(@Nullable Integer color) {
    public static ColorProvider validate(String color) {
        try {
            return new ColorProvider(Integer.parseInt(color, 16));
        } catch (NumberFormatException e) {
            if (!color.equalsIgnoreCase("random")) {
                LocatorLodestones.LOGGER.error("Invalid config value '{}'", color);
            }
            return new ColorProvider(null);
        }
    }

    public @Nullable Integer getColorWithAlpha() {
        return color == null ? null : ARGB.color(255, color);
    }
}

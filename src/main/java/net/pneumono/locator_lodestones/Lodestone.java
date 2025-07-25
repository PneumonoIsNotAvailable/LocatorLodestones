package net.pneumono.locator_lodestones;

import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public record Lodestone(Vec3d pos, Optional<Integer> color) {
    public int getColor() {
        if (color().isPresent()) {
            return color().get();
        }

        return pos().toString().hashCode();
    }
}

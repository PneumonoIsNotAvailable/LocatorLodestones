package net.pneumono.locator_lodestones;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorHandler {
    public static Optional<Integer> getColor(ItemStack stack) {
        if (!ConfigManager.colorCustomization()) return Optional.empty();

        Optional<Integer> color = getColor(stack.get(DataComponents.CUSTOM_NAME));
        if (color.isEmpty()) {
            color = getColor(stack.get(DataComponents.ITEM_NAME));
        }
        return color;
    }

    public static Optional<Integer> getColor(Component text) {
        if (text == null) {
            return Optional.empty();
        } else {
            return getColor(text.getString());
        }
    }

    public static Optional<Integer> getColor(String text) {
        if (text == null) {
            return Optional.empty();
        }

        Matcher hexMatcher = Pattern.compile("#[0-9a-fA-F]{6}").matcher(text);
        Matcher codeMatcher = Pattern.compile("[§&][0-9a-f]").matcher(text);

        if (hexMatcher.find()) {
            return parseHexCode(hexMatcher.group());

        } else if (codeMatcher.find()) {
            return parseFormattingCode(codeMatcher.group());
        }

        return Optional.empty();
    }

    public static Optional<Integer> parseFormattingCode(@Nullable String string) {
        if (string == null) return Optional.empty();

        if (string.length() < 2) {
            LocatorLodestones.LOGGER.error("String '{}' is not a valid formatting code!", string);
        }

        ChatFormatting formatting = ChatFormatting.getByCode(string.charAt(1));
        if (formatting == null) {
            return Optional.empty();
        } else {
            Integer integer = formatting.getColor();
            return integer == null ? Optional.empty() : Optional.of(ARGB.color(255, integer));
        }
    }

    public static Optional<Integer> parseHexCode(@Nullable String string) {
        if (string == null) return Optional.empty();

        try {
            return Optional.of(ARGB.color(255, Integer.parseInt(string, 1, 7, 16)));
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException e) {
            LocatorLodestones.LOGGER.error("String '{}' is not a valid hex code!", string, e);
            return Optional.empty();
        }
    }

    public static Optional<Component> removeColorCode(Component text) {
        if (text == null) return Optional.empty();

        String string = text.getString()
                .replaceAll("( ?)([({<\\[]?)(#[0-9a-fA-F]{6})([)}>\\]]?)", "")
                .replaceAll("( ?)([({<\\[]?)([§&][0-9a-f])([)}>\\]]?)", "");
        return string.isEmpty() ? Optional.empty() : Optional.of(Component.literal(string));
    }
}

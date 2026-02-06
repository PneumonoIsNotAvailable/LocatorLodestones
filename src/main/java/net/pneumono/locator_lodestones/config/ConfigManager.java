package net.pneumono.locator_lodestones.config;

import net.pneumono.locator_lodestones.LocatorLodestones;
import net.pneumono.pneumonocore.config_api.ConfigApi;
import net.pneumono.pneumonocore.config_api.configurations.AbstractConfiguration;
import net.pneumono.pneumonocore.config_api.configurations.BooleanConfiguration;
import net.pneumono.pneumonocore.config_api.configurations.ConfigSettings;
import net.pneumono.pneumonocore.config_api.configurations.StringConfiguration;
import net.pneumono.pneumonocore.config_api.enums.LoadType;

public class ConfigManager {
    public static final BooleanConfiguration TAB_FORCES_LOCATOR_BAR = register("tab_forces_locator_bar", new BooleanConfiguration(
            true, new ConfigSettings().category("display").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final BooleanConfiguration TAB_SHOWS_NAMES = register("tab_shows_names", new BooleanConfiguration(
            true, new ConfigSettings().category("display").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final BooleanConfiguration SHOW_RECOVERY_COMPASSES = register("show_recovery_compasses", new BooleanConfiguration(
            true, new ConfigSettings().category("display").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final BooleanConfiguration SHOW_BUNDLED_COMPASSES = register("show_bundled_compasses", new BooleanConfiguration(
            true, new ConfigSettings().category("display").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final BooleanConfiguration SHOW_IN_SPECTATOR = register("show_in_spectator", new BooleanConfiguration(
            true, new ConfigSettings().category("display").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final BooleanConfiguration COLOR_CUSTOMIZATION = register("color_customization", new BooleanConfiguration(
            true, new ConfigSettings().category("color").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final StringConfiguration LODESTONE_COLOR = register("lodestone_color", new StringConfiguration(
            "random", new ConfigSettings().category("color").loadType(LoadType.INSTANT).clientSide()
    ));
    public static final StringConfiguration RECOVERY_COLOR = register("recovery_color", new StringConfiguration(
            "bce0eb", new ConfigSettings().category("color").loadType(LoadType.INSTANT).clientSide()
    ));

    public static <T extends AbstractConfiguration<?>> T register(String name, T config) {
        return ConfigApi.register(LocatorLodestones.id(name), config);
    }

    public static void initConfig() {
        ConfigApi.finishRegistry(LocatorLodestones.MOD_ID);
    }

    public static boolean tabForcesLocatorBar() {
        return TAB_FORCES_LOCATOR_BAR.getValue();
    }

    public static boolean tabShowsNames() {
        return TAB_SHOWS_NAMES.getValue();
    }

    public static boolean shouldShowRecovery() {
        return SHOW_RECOVERY_COMPASSES.getValue();
    }

    public static boolean shouldShowBundled() {
        return SHOW_BUNDLED_COMPASSES.getValue();
    }

    public static boolean shouldShowInSpectator() {
        return SHOW_IN_SPECTATOR.getValue();
    }

    public static boolean colorCustomization() {
        return COLOR_CUSTOMIZATION.getValue();
    }

    public static ColorProvider getLodestoneColor() {
        return ColorProvider.validate(LODESTONE_COLOR.getValue());
    }

    public static ColorProvider getRecoveryColor() {
        return ColorProvider.validate(RECOVERY_COLOR.getValue());
    }
}

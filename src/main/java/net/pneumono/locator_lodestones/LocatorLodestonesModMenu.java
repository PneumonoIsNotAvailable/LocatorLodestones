package net.pneumono.locator_lodestones;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.pneumono.pneumonocore.PneumonoCoreModMenu;

public class LocatorLodestonesModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PneumonoCoreModMenu.getModConfigScreenFactory(LocatorLodestones.MOD_ID);
    }
}

package com.fabledruns.korenet;

import com.fabledruns.korenet.config.KorenetConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class KorenetModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return KorenetConfigScreen::new;
    }
}

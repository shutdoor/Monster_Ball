package com.shutdoor.monsterball.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "monsterball")
public class monsterballConfig implements ConfigData {
    @Comment("Blacklisted mobs will not be captured by the monsterball, ex: minecraft:zombie")
    public List<String> BLACKLIST = new ArrayList<>();

    public static monsterballConfig getConfig() {
        return AutoConfig.getConfigHolder(monsterballConfig.class).getConfig();
    }
}

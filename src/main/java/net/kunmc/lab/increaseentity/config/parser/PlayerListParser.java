package net.kunmc.lab.increaseentity.config.parser;

import java.util.ArrayList;
import java.util.List;

public class PlayerListParser extends Parser<List<String>> {
    public PlayerListParser() {
        this.canUseFromCommand = true;
    }

    @Override
    public List<String> parse(String str) {
        List<String> uuidList = new ArrayList<>();
        return uuidList;
    }
}

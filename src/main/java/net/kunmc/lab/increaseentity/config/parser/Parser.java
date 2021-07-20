package net.kunmc.lab.increaseentity.config.parser;

public abstract class Parser<T> {
    protected boolean canUseFromCommand;

    public abstract T parse(String str);

    public boolean canUseFromCommand() {
        return canUseFromCommand;
    }
}

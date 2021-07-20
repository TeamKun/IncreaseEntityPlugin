package net.kunmc.lab.increaseentity.config.parser;

public abstract class Parser<T> {
    public abstract T parse(String str);
}

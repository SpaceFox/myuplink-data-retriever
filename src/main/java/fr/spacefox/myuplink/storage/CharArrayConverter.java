package fr.spacefox.myuplink.storage;

import org.eclipse.microprofile.config.spi.Converter;

public class CharArrayConverter implements Converter<char[]> {

    @Override
    public char[] convert(String s) throws IllegalArgumentException, NullPointerException {
        return s.toCharArray();
    }
}

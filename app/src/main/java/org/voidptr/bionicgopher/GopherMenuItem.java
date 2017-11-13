package org.voidptr.bionicgopher;

import java.util.List;

/**
 * Created by errant on 11/11/17.
 * Represents a menu item
 */

public class GopherMenuItem {
    public enum Type {
        TEXT_FILE('0'),
        GOPHER_SUBMENU('1'),
        CCSO_NAMESERVER('2'),
        ERROR('3'),
        BINHEX('4'),
        DOS('5'),
        UUENCODED('6'),
        SEARCH('6'),
        TELNET('7'),
        BINARY('8'),
        MIRROR('9'),
        GIF('g'),           //Pronounced with hard G
        IMAGE('I'),
        TELNET3270('T'),
        HTML('h'),
        INFORMATION('i'),
        SOUND('s');

        private char value;

        private Type(char value){
            this.value = value;
        }

        public static Type forValue(char i) {
            for(Type t : Type.values()) {
                if (t.value == i) {
                    return t;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    private String title;
    private String path;
    private Integer port;
    private Type type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static GopherMenuItem fromLine(String line) {
        GopherMenuItem item = new GopherMenuItem();
        String parts[] = line.substring(1).split("\t");

        try {
            switch (Type.forValue(line.charAt(0))) {
                case INFORMATION:
                    item.setTitle(parts[0]);
                    break;
                case GOPHER_SUBMENU:
                    item.setTitle(parts[0]);
                    item.setPath(parts[1]);
                    break;
                case TEXT_FILE:
                    item.setTitle(parts[0]);
                    item.setPath(parts[1]);
                    break;
            }

            item.setType(Type.forValue(line.charAt(0)));
            return item;
        } catch (IllegalArgumentException e) {
            throw new GopherParseError();
        }

    }
}

package org.voidptr.bionicgopher.model;

import android.os.Bundle;
import android.util.Log;

import org.voidptr.bionicgopher.exception.GopherParseError;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by errant on 11/11/17.
 * Represents a menu item
 */

public class GopherMenuItem {
    public GopherUri getUri() {
        return uri;
    }

    public void setUri(GopherUri uri) {
        this.uri = uri;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum Type {
        TEXT_FILE('0'),
        GOPHER_SUBMENU('1'),
        CCSO_NAMESERVER('2'),
        ERROR('3'),
        BINHEX('4'),
        DOS('5'),
        UUENCODED('6'),
        SEARCH('7'),
        TELNET('8'),
        BINARY('9'),
        MIRROR('+'),
        GIF('g'),           //Pronounced with hard G
        IMAGE('I'),
        TELNET3270('T'),
        HTML('h'),
        INFORMATION('i'),
        SOUND('s'),
        BLANK(' ');

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
    private Type type;
    private String data;
    private GopherUri uri;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static GopherMenuItem fromLine(GopherUri base, String line) {
        GopherMenuItem item = new GopherMenuItem();

        if(line.isEmpty()){
            item.setType(Type.BLANK);
            return item;
        }
        String parts[] = line.substring(1).split("\t");

        try {
            GopherUri path = new GopherUri();
            switch (Type.forValue(line.charAt(0))) {
                case INFORMATION:
                    item.setTitle(parts[0]);
                    break;
                case GOPHER_SUBMENU:
                    //Host name
                    if(parts[2].isEmpty()){
                        path.setHost(base.getHost());
                    }else{
                        path.setHost(parts[2]);
                    }

                    //Port
                    if(parts[3].isEmpty()){
                        path.setPort(base.getPort());
                    }else {
                        path.setPort(Integer.getInteger(parts[3]));
                    }

                    //Path
                    if(!parts[1].isEmpty()){
                        path.setPathElements(Arrays.asList(parts[1].split("/")));
                    }

                    item.setUri(path);
                    item.setTitle(parts[0]);
                    break;
                case TEXT_FILE:
                case GIF:
                case SEARCH:
                    path.setPathElements(Arrays.asList(parts[1].split("/")));
                    path.setHost(parts[2]);
                    path.setPort(Integer.getInteger(parts[3]));
                    item.setTitle(parts[0]);
                    item.setUri(path);
                    break;
                default:
                    Log.d("GopherMenuItem", "Unhandled line: "+line);
            }

            item.setType(Type.forValue(line.charAt(0)));
            return item;
        } catch (IllegalArgumentException e) {
            throw new GopherParseError(line);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(uri.toString());
        sb.append("\t");
        sb.append(title);
        sb.append("\t");
        sb.append(type.value);
        sb.append("\t");
        sb.append(data);

        return sb.toString();
    }

    public static GopherMenuItem fromString(String serialized){
        GopherMenuItem newItem = new GopherMenuItem();

        String[] parts = serialized.split("\t");
        newItem.setUri(new GopherUri(parts[0]));
        newItem.setTitle(parts[1]);
        newItem.setType(Type.valueOf(parts[2]));
        newItem.setData(parts[3]);

        return newItem;
    }
}

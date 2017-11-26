package org.voidptr.bionicgopher.model;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by errant on 11/12/17.
 */

public class PathStack {
    private List<GopherUri> pathElements;
    private Integer index;

    public PathStack() {
        pathElements = new ArrayList<>();
    }

    public GopherUri getCurrent(){
        if(index != null) {
            return pathElements.get(index);
        }else{
            return null;
        }
    }

    public void push(GopherUri path) {
        if(index == null){
            index = 0;
        }else{
            if(index != pathElements.size()-1){
                pathElements = pathElements.subList(0, index+1);
            }
            index++;
        }
        pathElements.add(path);
    }

    public void back(){
        if(index != null && index > 0){
            index--;
        }
    }

    public void forward(){
        if(index != null && index < pathElements.size()){
            index++;
        }
    }

    public void clear() {
        pathElements.clear();
    }

    public boolean havePrevious() {
        return (index > 0);
    }

    public Bundle toBundle() {
        Bundle out = new Bundle();

        ArrayList<String> stack = new ArrayList<>();
        for(GopherUri uri : pathElements) {
            stack.add(uri.toString());
        }

        out.putStringArrayList("stack", stack);
        out.putInt("index", index);

        return out;
    }

    public void fromBundle(Bundle from) {
        if(from != null) {
            ArrayList<GopherUri> uris = new ArrayList<>();
            List<String> serialized = from.getStringArrayList("stack");
            if(serialized != null) {
                for (String uri : serialized) {
                    uris.add(new GopherUri(uri));
                }
            }
            index = from.getInt("index");
        }
    }
}

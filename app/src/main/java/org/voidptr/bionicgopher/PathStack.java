package org.voidptr.bionicgopher;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by errant on 11/12/17.
 */

public class PathStack {
    private ArrayList<String> pathElements;

    public PathStack() {
        pathElements = new ArrayList<>();
    }

    public String getPath(){
        StringBuilder builder = new StringBuilder();
        Iterator<String> elementIterator = pathElements.iterator();
        while(elementIterator.hasNext()) {
            builder.append(elementIterator.next());
            if(elementIterator.hasNext()) {
                builder.append("/");
            }
        }
        return builder.toString();
    }

    public void push(String path) {
        pathElements.add(0, path);
    }

    public void pop() {
        pathElements.remove(0);
    }

    public void clear() {
        pathElements.clear();
    }
}

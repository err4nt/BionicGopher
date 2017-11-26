package org.voidptr.bionicgopher.model;

import android.os.Bundle;
import android.util.Log;

import org.voidptr.bionicgopher.exception.GopherParseError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by errant on 11/11/17.
 * Represents a single page
 */

public class GopherPage {

    public GopherPage() {
        menu = new ArrayList<>();
    }

    private List<GopherMenuItem> menu;

    public List<GopherMenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<GopherMenuItem> menu) {
        this.menu = menu;
    }

    public static GopherPage fromStringList(GopherUri base, List<String> strings) {
        GopherPage page = new GopherPage();

        for (String line : strings) {
            try {
                page.getMenu().add(GopherMenuItem.fromLine(base, line));
            } catch (GopherParseError e) {
                Log.e("GopherPage", e.toString());
                e.printStackTrace();
            }
        }

        return page;
    }

    public Bundle toBundle() {
        Bundle out = new Bundle();

        ArrayList<String> items = new ArrayList<>();
        for(GopherMenuItem item : menu) {
            items.add(item.toString());
        }

        out.putStringArrayList("items", items);

        return out;
    }

    public static GopherPage fromBundle(Bundle from) {
        GopherPage newPage = new GopherPage();
        ArrayList<GopherMenuItem> newMenu = new ArrayList<>();
        if(from != null){
            ArrayList<String> items = from.getStringArrayList("items");
            if(items != null){
                for(String item : items) {
                    newMenu.add(GopherMenuItem.fromString(item));
                }
            }
        }
        newPage.setMenu(newMenu);

        return newPage;
    }
}

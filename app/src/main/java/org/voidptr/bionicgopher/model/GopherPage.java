package org.voidptr.bionicgopher;

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

    public static GopherPage fromStringList(List<String> strings) {
        GopherPage page = new GopherPage();

        for(String line : strings) {
            try {
                page.getMenu().add(GopherMenuItem.fromLine(line));
            } catch (GopherParseError e) {
                e.printStackTrace();
            }
        }

        return page;
    }
}

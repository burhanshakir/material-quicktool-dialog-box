package burhan.com.material_quicktooldialogbox;

import android.graphics.drawable.Drawable;

/**
 * Created by burha on 15-07-2017.
 */

public class QuickToolDialogButton
{
    private int id;
    private Drawable icon;
    private String title;

    public QuickToolDialogButton()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

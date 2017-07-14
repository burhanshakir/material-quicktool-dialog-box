package burhan.com.material_quicktooldialogbox;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;

/**
 * Created by burha on 13-07-2017.
 */

public class QuickToolDialog extends PopupWindow implements OnDismissListener
{
    ImageView ivArrowUp, ivArrowDown;
    public static int DARK_THEME = 52;
    public static int LIGHT_THEME = 53;
    private View mRootView;
    private LayoutInflater mLayoutInflater;
    private ScrollView mScrollView;

    @Override
    public void onDismiss()
    {

    }
}

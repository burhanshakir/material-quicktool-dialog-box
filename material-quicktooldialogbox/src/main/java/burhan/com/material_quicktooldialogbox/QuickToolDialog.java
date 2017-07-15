package burhan.com.material_quicktooldialogbox;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.XmlRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by burhan on 13-07-2017.
 */

public class QuickToolDialog extends PopupWindow implements OnDismissListener
{
    ImageView ivArrowUp, ivArrowDown;
    public static int DARK_THEME = 52;
    public static int LIGHT_THEME = 53;
    private View mRootView;
    private LayoutInflater mLayoutInflater;
    private ScrollView mScrollView;
    private ViewGroup mContainer;
    private Context mContext;

    private ImageView mArrowUp;
    private ImageView mArrowDown;

    private PopupWindow mWindow;

    private List<QuickToolDialogButton> dialogButtons = new ArrayList<>();

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mInsertPos;
    private int rootWidth=0;

    public static final int DRAW_FROM_LEFT = 1;
    public static final int DRAW_FROM_RIGHT = 2;
    public static final int DRAW_FROM_CENTER = 3;
    public static final int ANIM_REFLECT = 4;
    public static final int ANIM_AUTO = 5;

    int mOrientation;
    int mTheme;
    private int mAnimStyle;
    OnDialogItemClickListener mItemClickListener;
    private int buttonXmlResource;
    private XmlResourceParser parser;
    private final String TAB_TAG = "tab";
    private static final int RESOURCE_NOT_FOUND = 0;
    private int mChildPos;
    private boolean mDidAction;
    WindowManager mWindowManager;
    private OnDismissListener mDismissListener;

    public QuickToolDialog(Context context)
    {
        this(context,VERTICAL);
    }

    public QuickToolDialog(Context context, int orientation)
    {
        this(context,VERTICAL,DARK_THEME);
    }

    public QuickToolDialog(Context context, int orientation, int theme)
    {
        mOrientation = orientation;
        mTheme = theme;
        mContext = context;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mOrientation == HORIZONTAL)
        {
            if(mTheme == DARK_THEME)
                setLayoutIds(R.layout.quicktool_dialog_horizontal, R.drawable.dark_theme);
            else
                setLayoutIds(R.layout.quicktool_dialog_horizontal,R.drawable.light_theme);
        }
        else
        {
            if(mTheme == DARK_THEME)
                setLayoutIds(R.layout.quicktool_dialog_vertical, R.drawable.dark_theme);
            else
                setLayoutIds(R.layout.quicktool_dialog_vertical,R.drawable.light_theme);
        }

        mChildPos = 0;
        mAnimStyle = ANIM_AUTO;
    }

    private void setLayoutIds(int rootView, int theme )
    {
        mRootView = mLayoutInflater.inflate(rootView,null);

        mContainer = (ViewGroup) mRootView.findViewById(R.id.container);
        mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
        mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);

        mScrollView	= (ScrollView) mRootView.findViewById(R.id.scrollview);
        mScrollView.setBackground(ContextCompat.getDrawable(mContext, theme));

        mWindow = new PopupWindow(mContext);
        mWindow.setContentView(mRootView);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setAnimStyle(int mAnimStyle)
    {
        this.mAnimStyle = mAnimStyle;
    }

    @Override
    public void onDismiss()
    {

    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener listener)
    {
        mItemClickListener = listener;
    }

    public void setButtons(@XmlRes int xmlRes)
    {
        parser = mContext.getResources().getXml(xmlRes);

        if (xmlRes == 0)
        {
            throw new RuntimeException("No buttons specified for the Quicktool Dialog!");
        }
        else
            parseButtons();

        for(QuickToolDialogButton dialogButton : dialogButtons)
        {
            String title 	= dialogButton.getTitle();
            Drawable icon 	= dialogButton.getIcon();

            View container;

            if (mOrientation == HORIZONTAL)
            {
                container = mLayoutInflater.inflate(R.layout.dialog_button_horizontal, null);
            }
            else
            {
                container = mLayoutInflater.inflate(R.layout.dialog_button_vertical, null);
            }

            ImageView img 	= (ImageView) container.findViewById(R.id.ivIcon);
            TextView text 	= (TextView) container.findViewById(R.id.tvTitle);

            if(mTheme == DARK_THEME)
            {
                text.setTextColor(Color.WHITE);
            }
            else
            {
                text.setTextColor(Color.BLACK);
            }

            if (icon != null)
            {
                img.setImageDrawable(icon);
            }
            else
            {
                img.setVisibility(View.GONE);
            }

            if (title != null)
            {
                text.setText(title);
            }
            else
            {
                text.setVisibility(View.GONE);
            }

            final int pos 		=  mChildPos;
            final int actionId 	= dialogButton.getId();

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mItemClickListener != null)
                    {
                        mItemClickListener.onItemClick(QuickToolDialog.this, pos, actionId);
                    }
                }
            });

            container.setFocusable(true);
            container.setClickable(true);

            if (mOrientation == HORIZONTAL && mChildPos != 0) {
                View separator = mLayoutInflater.inflate(R.layout.horiz_separator, null);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);

                separator.setLayoutParams(params);
                separator.setPadding(5, 0, 5, 0);

                mContainer.addView(separator, mInsertPos);

                mInsertPos++;
            }

            mContainer.addView(container, mInsertPos);

            mChildPos++;
            mInsertPos++;

        }


    }

    public void show(View anchor)
    {
        int xPos, yPos, arrowPos;

        mDidAction 			= false;

        int[] location 		= new int[2];

        anchor.getLocationOnScreen(location);

        Rect outline = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootHeight = mRootView.getMeasuredHeight();

        if(rootWidth == 0)
        {
            rootWidth = mRootView.getWidth();
        }

        int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight	= mWindowManager.getDefaultDisplay().getHeight();

        if ((outline.left + rootWidth) > screenWidth) {
            xPos 		= outline.left - (rootWidth-anchor.getWidth());
            xPos 		= (xPos < 0) ? 0 : xPos;

            arrowPos 	= outline.centerX()-xPos;

        }
        else
        {
            if (anchor.getWidth() > rootWidth)
            {
                xPos = outline.centerX() - (rootWidth/2);
            }
            else
            {
                xPos = outline.left;
            }

            arrowPos = outline.centerX()-xPos;
        }

        int dyTop			= outline.top;
        int dyBottom		= screenHeight - outline.bottom;

        boolean onTop		= (dyTop > dyBottom);

        if (onTop)
        {
            if (rootHeight > dyTop)
            {
                yPos 			= 15;
                ViewGroup.LayoutParams l 	= mScrollView.getLayoutParams();
                l.height		= dyTop - anchor.getHeight();
            }
            else
            {
                yPos = outline.top - rootHeight;
            }
        }
        else
        {
            yPos = outline.bottom;

            if (rootHeight > dyBottom)
            {
                ViewGroup.LayoutParams l 	= mScrollView.getLayoutParams();
                l.height		= dyBottom;
            }
        }

        showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

        setAnimationStyle(screenWidth, outline.centerX(), onTop);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);


    }

    private void showArrow(int i, int arrowPos)
    {
        final View showArrow = (i == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (i == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();

        param.leftMargin = arrowPos - arrowWidth / 2;

        hideArrow.setVisibility(View.INVISIBLE);
    }


    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop)
    {
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

        if (arrowPos <= screenWidth/4)
        {
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
        }
        else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4))
        {
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
        }
        else
        {
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
        }



    }

    private List<QuickToolDialogButton> parseButtons()
    {
        if(TAB_TAG.equals(parser.getName()))
        {
            dialogButtons.add(parseNewTab(buttonXmlResource));
        }

        return  dialogButtons;

    }

    private QuickToolDialogButton parseNewTab(int buttonXmlResource)
    {
        QuickToolDialogButton quickToolDialogButton = new QuickToolDialogButton();

        final int numberOfAttributes = parser.getAttributeCount();

        for (int i = 0; i < numberOfAttributes; i++) {

            String attrName = parser.getAttributeName(i);
            switch (attrName)
            {
                case "id" :
                    quickToolDialogButton.setId(parser.getIdAttributeResourceValue(i));
                    break;
                case "icon":
                    quickToolDialogButton.setIcon(ContextCompat.getDrawable(mContext,parser.getAttributeResourceValue(i,mTheme)));
                    break;
                case "title":
                    quickToolDialogButton.setTitle(String.valueOf(parser.getAttributeResourceValue(i,0)));
                    break;
            }
        }

        return quickToolDialogButton;
    }

    public void setOnDismissListener(QuickToolDialog.OnDismissListener listener)
    {
        setOnDismissListener(this);

        mDismissListener = listener;
    }

    public interface OnDismissListener
    {
        public abstract void onDismiss();
    }

}

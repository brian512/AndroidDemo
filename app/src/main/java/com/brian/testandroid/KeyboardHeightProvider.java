package com.brian.testandroid;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed.
 * @Link https://github.com/siebeprojects/samples-keyboardheight
 */
public class KeyboardHeightProvider extends PopupWindow {

    /**
     * The tag for logging purposes
     */
    private final static String TAG = "KeyboardHeightProvider";

    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;

    /**
     * The cached landscape height of the keyboard
     */
    private int keyboardLandscapeHeight;

    /**
     * The cached portrait height of the keyboard
     */
    private int keyboardPortraitHeight;

    private int mDisplayBottom = 0;

    /**
     * The view that is used to calculate the keyboard height
     */
    private View popupView;

    /**
     * The parent view
     */
    private View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private Activity activity;

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        this.popupView = new LinearLayout(activity);
        this.popupView.setBackgroundDrawable(null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.popupView.setLayoutParams(params);
        setContentView(this.popupView);

        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {

        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        this.observer = null;
        dismiss();
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    public int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        if (rect.bottom > mDisplayBottom) {
            mDisplayBottom = rect.bottom;
        }

        int displayBottom = mDisplayBottom > 0 ? mDisplayBottom : screenSize.y;

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = displayBottom - rect.bottom;

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.keyboardPortraitHeight = keyboardHeight;
            notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation);
        } else {
            this.keyboardLandscapeHeight = keyboardHeight;
            notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation);
        }
    }

    /**
     *
     */
    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }

    /**
     * The observer that will be notified when the height of
     * the keyboard has changed
     */
    public interface KeyboardHeightObserver {

        /**
         * Called when the keyboard height has changed, 0 means keyboard is closed,
         * >= 1 means keyboard is opened.
         *
         * @param height      The height of the keyboard in pixels
         * @param orientation The orientation either: Configuration.ORIENTATION_PORTRAIT or
         *                    Configuration.ORIENTATION_LANDSCAPE
         */
        void onKeyboardHeightChanged(int height, int orientation);
    }
}

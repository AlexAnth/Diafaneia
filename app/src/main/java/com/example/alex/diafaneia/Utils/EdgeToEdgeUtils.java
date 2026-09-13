package com.example.alex.diafaneia.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Since targetSdkVersion 36 (Android 16), edge-to-edge display can no longer be opted out of,
 * so screen content is drawn behind the status bar by default, and android:statusBarColor is
 * ignored. Our screens use fixed-height custom headers instead of a real Toolbar/ActionBar, so
 * without this the status bar icons overlap the header content. This pushes each screen's root
 * content down by the status bar inset, and draws a scrim behind the status bar in the given
 * color (matching that screen's header) so the bar reads as part of the screen instead of a
 * plain white/transparent gap.
 */
public class EdgeToEdgeUtils {

    public static void applyStatusBarPadding(Activity activity, int statusBarColor) {
        View content = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        final int basePaddingLeft = content.getPaddingLeft();
        final int basePaddingTop = content.getPaddingTop();
        final int basePaddingRight = content.getPaddingRight();
        final int basePaddingBottom = content.getPaddingBottom();

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final View scrim = new View(activity);
        scrim.setBackgroundColor(statusBarColor);
        decorView.addView(scrim, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 0, Gravity.TOP));

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(basePaddingLeft, basePaddingTop + statusBars.top, basePaddingRight, basePaddingBottom);

            ViewGroup.LayoutParams scrimParams = scrim.getLayoutParams();
            if (scrimParams.height != statusBars.top) {
                scrimParams.height = statusBars.top;
                scrim.setLayoutParams(scrimParams);
            }
            return insets;
        });

        // The scrim is a colored (non-white) bar, so use light (white) status bar icons
        // to keep them visible against it.
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
    }

    /** Averages the pixels along the top edge of the bitmap into a single color. */
    public static int sampleTopEdgeColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        long r = 0, g = 0, b = 0;
        for (int x = 0; x < width; x++) {
            int pixel = bitmap.getPixel(x, 0);
            r += Color.red(pixel);
            g += Color.green(pixel);
            b += Color.blue(pixel);
        }
        return Color.rgb((int) (r / width), (int) (g / width), (int) (b / width));
    }
}

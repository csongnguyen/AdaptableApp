package com.adaptableandroid;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Connie on 5/28/2015.
 */
public class PopupUtils {

    public static void showPopup(Activity activity, String message){
        // We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) activity.findViewById(R.id.popup_element));

        // create a 300px width and 470px height PopupWindow
        final PopupWindow pw = new PopupWindow(layout, 400, 400, true);
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

        // Create the text view
        TextView mResultText = (TextView) layout.findViewById(R.id.popup_text);
        mResultText.setText(message);
        Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", .3f, 1f);
        fadeIn.setDuration(200);

        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.start();
    }
}

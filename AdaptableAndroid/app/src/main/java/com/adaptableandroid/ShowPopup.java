package com.adaptableandroid;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Connie on 4/8/2015.
 */
public class ShowPopup extends Activity {
    PopupWindow popup;
    LinearLayout layout;
    TextView tv;
    LinearLayout.LayoutParams params;
    LinearLayout mainLayout;
    Button but;
    boolean click = true;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        popup = new PopupWindow(this);
        layout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        tv = new TextView(this);
        but = new Button(this);
        but.setText("Click me!");
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(click) {
                    popup.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popup.update(50, 50, 300, 80);
                    click = false;

                }else{
                    popup.dismiss();
                    click = true;
                }
            }
        });

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText("Hi this is a sample text for the popup window");
        layout.addView(tv, params);
        popup.setContentView(layout);
        // popup.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        mainLayout.addView(but, params);
        setContentView(mainLayout);
    }

}

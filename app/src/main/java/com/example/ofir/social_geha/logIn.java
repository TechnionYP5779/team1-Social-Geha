package com.example.ofir.social_geha;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class logIn extends AppCompatActivity {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    Button button;
    EditText username;
    EditText password;
    EditText personal_code;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // get views
        username = this.findViewById(R.id.user_name);
        password = this.findViewById(R.id.password);
        personal_code = this.findViewById(R.id.personal_code);
        button = this.findViewById(R.id.log_in_button);

        // setting listeners
        personal_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() <= (personal_code.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()))
                    {
                        Toast.makeText(logIn.this, "pressed on info icon",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorEnd = logIn.this.getResources().getColor(R.color.transparent_primary);
                int colorStart = logIn.this.getResources().getColor(R.color.colorPrimaryDark);
                ValueAnimator colorAnim = ObjectAnimator.ofInt(button, "backgroundColor",colorStart,colorEnd);
                colorAnim.setDuration(100);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();
                Toast.makeText(logIn.this,"processing information...",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

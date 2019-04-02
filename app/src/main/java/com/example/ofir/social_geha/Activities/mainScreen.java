package com.example.ofir.social_geha.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ofir.social_geha.R;

public class mainScreen extends AppCompatActivity {

    LinearLayout settings_screen;
    LinearLayout edit_anonymous;
    LinearLayout all_conversations;
    LinearLayout search_guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        settings_screen = this.findViewById(R.id.settings);
        edit_anonymous = this.findViewById(R.id.edit_anonymous);
        all_conversations = this.findViewById(R.id.current_conversations);
        search_guide = this.findViewById(R.id.search_guide);
    }

    public void gotoScreen(View view) {
        if(view.equals(settings_screen))
            Toast.makeText(mainScreen.this, "settings", Toast.LENGTH_SHORT).show();
        if(view.equals(edit_anonymous))
            Toast.makeText(mainScreen.this, "edit anonymous", Toast.LENGTH_SHORT).show();
        if(view.equals(all_conversations))
            Toast.makeText(mainScreen.this, "all conversations", Toast.LENGTH_SHORT).show();
        if(view.equals(search_guide))
            Toast.makeText(mainScreen.this, "search guide", Toast.LENGTH_SHORT).show();
    }
}

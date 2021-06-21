package com.example.chat_android.Game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_android.Activities.MainActivity;
import com.example.chat_android.Activities.StartActivity;
import com.example.chat_android.R;

public class StartGameActivity extends AppCompatActivity {

    ImageButton play, more, shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        //full screnn

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        play = findViewById(R.id.play);
        more = findViewById(R.id.more);
        shop = findViewById(R.id.shop);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainGameActivity.class));
                finish();
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartGameActivity.this, ShopActivity.class));
                finish();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            private boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent, chooser;
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.feedback: {
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        String[] to = {"maiminhlqd0@gmail.com"}; // my email
                        intent.putExtra(Intent.EXTRA_EMAIL, to);
                        intent.setType("message/rfc822");
                        chooser = Intent.createChooser(intent, "Send Feedback");
                        startActivity(chooser);
                    }
                    case R.id.message: {
                        intent = new Intent(StartGameActivity.this, StartActivity.class);
                        startActivity(intent);
                        return true;
                    }
////                    case R.id.share: {
////                        intent = new Intent(Intent.ACTION_SEND);
////                        intent.setType("text/plain");
////                        intent.putExtra(Intent.EXTRA_SUBJECT, "Vermiculi");
////                        String sAux = "\n Let me recommend you this Game \n\n";
////                        sAux = sAux + "https://www.facebook.com/uet0903 \n\n";
////                        intent.putExtra(Intent.EXTRA_TEXT, sAux);
////                        startActivity(Intent.createChooser(intent, "Share"));
//                    }
                }
                return true;
            }

            @SuppressLint({"IntentReset", "NonConstantResourceId"})
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(StartGameActivity.this, more);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
                popupMenu.show();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case  KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return  super.dispatchKeyEvent(event);
    }
}

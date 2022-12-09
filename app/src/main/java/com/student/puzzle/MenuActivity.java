package com.student.puzzle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    private long onBackPressedTime;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        editor = preferences.edit();


        Button button = findViewById(R.id.startbuton);
        editText = findViewById(R.id.player_name);
        String OxirgiPlayer = preferences.getString("player", "");
        editText.setText(OxirgiPlayer);
        button.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("player", editText.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        if (editText.getText().toString().length() > 0) {
            editor.putString("player", editText.getText().toString());
            editor.commit();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else
            Toast.makeText(getApplicationContext(), "Please prees back again to exit!", Toast.LENGTH_SHORT).show();
        onBackPressedTime = System.currentTimeMillis();
    }
}
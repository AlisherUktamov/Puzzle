package com.student.puzzle;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<Integer> sonlar = new ArrayList<>();
    private GridLayout gridLayout;
    private Button emptyButton;
    private TextView playername, textMoves;
    private ImageButton pauseButton, refreshbutton, infoButton, playButton;
    private Chronometer chronometer;
    private RelativeLayout pauseLayout;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private long onBackPressedTime, timePaused = 0;
    private final int[][] ARRAY = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 16}
    };
    //Bo'sh button kordinatlari
    private int x = 3;
    private int y = 3;
    int moves = 0;
    boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("app_button", MODE_PRIVATE);

        LoadNumbers();
        initializeGame();
        if (!preferences.getString("Numbers", "").isEmpty()) {
            ContinueGame();
        } else
            startGame();
    }

    private void createOldGame() {
        String numbers = preferences.getString("Numbers", "");
        String[] strs = numbers.split("\\s");
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Button button = (Button) gridLayout.getChildAt(index);
                button.setText(String.valueOf(strs[index]));
                button.setVisibility(View.VISIBLE);
                if (strs[index++].equals("16")) {
                    x = i;
                    y = j;
                    button.setVisibility(View.INVISIBLE);
                    emptyButton = button;
                }
                button.setOnClickListener(this::click);
                setBackgroundButton(button, i, j);
            }
        }
    }

    private void restartUI() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void initializeGame() {
        gridLayout = findViewById(R.id.grid_layout);
        playername = findViewById(R.id.textPlayer);
        textMoves = findViewById(R.id.textMoves);
        pauseButton = findViewById(R.id.pasue_image);
        playButton = findViewById(R.id.play_image);
        refreshbutton = findViewById(R.id.refresh);
        pauseLayout = findViewById(R.id.pauseLayout);
        chronometer = findViewById(R.id.chronometr);
        infoButton = findViewById(R.id.about);
        playername.setText(getIntent().getStringExtra("player"));
        updateMovementUI();
        pauseButton.setOnClickListener(v -> {

        });

        pauseButton.setOnClickListener(v -> {
            pauseGame();

        });
        playButton.setOnClickListener(v -> {
            resumeGame();

        });
        refreshbutton.setOnClickListener(v -> {
            RestartGame();
        });
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        });
    }

    private void resumeGame() {
        chronometer.setBase(SystemClock.elapsedRealtime() + timePaused);
        chronometer.start();
        pauseLayout.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void RestartGame() {
        flag = true;
        restartUI();
        moves = 0;
        updateMovementUI();
        startGame();
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());

    }

    private void pauseGame() {
        pauseTime();
        pauseLayout.setFocusable(true);
        pauseLayout.setClickable(true);
        pauseLayout.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
    }

    private void pauseTime() {
        timePaused = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    private void LoadNumbers() {
        for (int i = 1; i <= 16; i++) {
            sonlar.add(i);
        }
    }

    private void startGame() {
        do {
            Collections.shuffle(sonlar);
        } while (!isSolvable(sonlar));
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Button button = (Button) gridLayout.getChildAt(index);
                button.setText(String.valueOf(sonlar.get(index)));
                button.setVisibility(View.VISIBLE);
                if (sonlar.get(index++) == 16) {
                    x = i;
                    y = j;
                    button.setVisibility(View.INVISIBLE);
                    emptyButton = button;
                }
                button.setOnClickListener(this::click);
                setBackgroundButton(button, i, j);
            }
        }
    }

    private boolean isSolvable(List<Integer> sonlar) {
        int counter = 0;
        for (int i = 0; i < sonlar.size(); i++) {
            if (sonlar.get(i) == 16) {
                counter += i / 4 + 1;
                continue;
            }
            for (int j = i + 1; j < sonlar.size(); j++) {
                if (sonlar.get(i) > sonlar.get(j)) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;
    }

    public void click(View view) {

        if (flag) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            String time = chronometer.getText().toString();
            editor = preferences.edit();
            editor.putString("time", time);
            editor.commit();
            flag = false;
        }
        Button cliked = (Button) view;
        String tag = cliked.getTag().toString();
        int ClickX = tag.charAt(0) - '0';
        int ClickY = tag.charAt(1) - '0';
        if (CanMove(ClickX, ClickY)) {
            moves++;
            updateMovementUI();
            editor = preferences.edit();
            editor.putInt("Mov", moves);
            editor.commit();
            swap(cliked, ClickX, ClickY);
            if (IsGameOver()) {
                String text = chronometer.getText().toString();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String textinfo = "Moves=" + moves + "\nTime=" + text +
                        "\nWill you start again?";
                builder.setTitle("Congratulations!")
                        .setMessage(textinfo)
                        .setIcon(R.drawable.cup1)
                        .setCancelable(false)
                        .setPositiveButton("Exit game", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RestartGame();
                    }
                });
                builder.show();
            }
        }
    }

    public boolean CanMove(int ClickX, int ClickY) {
        return ((Math.abs(ClickX + ClickY - (x + y)) == 1) && Math.abs(ClickX - x) != 2 && Math.abs(ClickY - y) != 2);
    }

    public void updateMovementUI() {
        textMoves.setText(String.valueOf(moves));
    }

    public void swap(Button cliked, int ClickX, int ClickY) {
        String text = cliked.getText().toString();
        cliked.setText(emptyButton.getText());
        cliked.setVisibility(View.INVISIBLE);
        emptyButton.setText(text);
        emptyButton.setVisibility(View.VISIBLE);
        setBackgroundButton(emptyButton, x, y);
        emptyButton = cliked;
        x = ClickX;
        y = ClickY;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
            SaveButton();
            super.onBackPressed();
        } else
            Toast.makeText(getApplicationContext(), "Please press back again to exit!", Toast.LENGTH_SHORT).show();
        onBackPressedTime = System.currentTimeMillis();
    }

    private boolean IsGameOver() {
        int counter = 1;
        for (int i = 0; i < sonlar.size(); i++) {
            Button checkerButton = (Button) gridLayout.getChildAt(i);
            String text = checkerButton.getText().toString();
            if (Integer.parseInt(text) == counter)
                counter++;
        }
        return counter == 17;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBackgroundButton(Button button, int i, int j) {
        if (Integer.parseInt(button.getText().toString()) == ARRAY[i][j]) {
            button.setBackground(getDrawable(R.drawable.shape4));
        } else
            button.setBackground(getDrawable(R.drawable.shape));
    }

    public void SaveButton() {
        editor = preferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            stringBuilder.append(button.getText().toString()).append(" ");
        }

        editor.putString("Numbers", stringBuilder.toString());
        editor.apply();
    }

    public void ContinueGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to continue the game!")
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RestartGame();
                    }
                });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createOldGame();
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        SaveButton();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SaveButton();
        super.onDestroy();
    }
}

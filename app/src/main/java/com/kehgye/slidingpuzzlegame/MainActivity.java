package com.kehgye.slidingpuzzlegame;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout; // Use androidx.gridlayout.widget.GridLayout
import androidx.gridlayout.widget.GridLayout.LayoutParams;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView timerText;
    private Button shuffleButton, resetButton;
    private int[][] puzzle = new int[4][4];
    private Button[][] buttons = new Button[4][4];
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private int seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        timerText = findViewById(R.id.timerText);
        shuffleButton = findViewById(R.id.shuffleButton);
        resetButton = findViewById(R.id.resetButton);

        initializePuzzle();
        drawPuzzle();

        shuffleButton.setOnClickListener(v -> {
            shufflePuzzle();
            drawPuzzle();
            resetTimer();
            startTimer();
        });

        resetButton.setOnClickListener(v -> {
            initializePuzzle();
            drawPuzzle();
            resetTimer();
            startTimer();
        });

        startTimer();
    }

    private void initializePuzzle() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (count < 16) {
                    puzzle[i][j] = count++;
                } else {
                    puzzle[i][j] = 0; // Empty space
                }
            }
        }
    }

    private void drawPuzzle() {
        gridLayout.removeAllViews();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Button button = new Button(this);
                button.setTextSize(24);
                button.setTextColor(getResources().getColor(R.color.black));
                if (puzzle[i][j] != 0) {
                    button.setText(String.valueOf(puzzle[i][j]));
                } else {
                    button.setText("");
                }

                LayoutParams params = new LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                button.setLayoutParams(params);

                int finalI = i;
                int finalJ = j;
                button.setOnClickListener(view -> onTileClick(finalI, finalJ));
                gridLayout.addView(button);

                buttons[i][j] = button;
            }
        }
    }

    private void onTileClick(int x, int y) {
        if (isAdjacentToEmptySpace(x, y)) {
            swapWithEmptySpace(x, y);
            drawPuzzle();
            checkWinCondition();
        }
    }

    private boolean isAdjacentToEmptySpace(int x, int y) {
        return (x > 0 && puzzle[x - 1][y] == 0) ||
                (x < 3 && puzzle[x + 1][y] == 0) ||
                (y > 0 && puzzle[x][y - 1] == 0) ||
                (y < 3 && puzzle[x][y + 1] == 0);
    }

    private void swapWithEmptySpace(int x, int y) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (puzzle[i][j] == 0) {
                    puzzle[i][j] = puzzle[x][y];
                    puzzle[x][y] = 0;
                    return;
                }
            }
        }
    }

    private void shufflePuzzle() {
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = random.nextInt(4);
                int y = random.nextInt(4);
                int temp = puzzle[i][j];
                puzzle[i][j] = puzzle[x][y];
                puzzle[x][y] = temp;
            }
        }
    }

    private void checkWinCondition() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (puzzle[i][j] != count % 16) {
                    return;
                }
                count++;
            }
        }
        Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d", minutes, secs);
                timerText.setText(time);
                seconds++;
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timerRunnable);
    }

    private void resetTimer() {
        handler.removeCallbacks(timerRunnable);
        seconds = 0;
        timerText.setText("00:00");
    }
}

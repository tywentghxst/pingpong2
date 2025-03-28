package com.example.pingpong;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PingPongView.GameListener {

    private PingPongView pingPongView;
    private TextView scoreTextView;
    private LinearLayout gameOverLayout;
    private Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        pingPongView = findViewById(R.id.ping_pong_view);
        scoreTextView = findViewById(R.id.score_text);
        gameOverLayout = findViewById(R.id.game_over_layout);
        restartButton = findViewById(R.id.restart_button);

        // Set game listener
        pingPongView.setGameListener(this);

        // Update initial score
        updateScore(0);

        // Set restart button click listener
        restartButton.setOnClickListener(v -> {
            gameOverLayout.setVisibility(View.GONE);
            pingPongView.restartGame();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pingPongView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pingPongView.resume();
    }

    @Override
    public void onScoreChanged(int newScore) {
        updateScore(newScore);
    }

    @Override
    public void onGameOver(int finalScore) {
        runOnUiThread(() -> {
            gameOverLayout.setVisibility(View.VISIBLE);
        });
    }

    private void updateScore(int score) {
        runOnUiThread(() -> {
            scoreTextView.setText(getString(R.string.score, score));
        });
    }
}

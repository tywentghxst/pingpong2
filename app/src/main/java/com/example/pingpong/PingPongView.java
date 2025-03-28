package com.example.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class PingPongView extends SurfaceView implements SurfaceHolder.Callback {

    // Interface for game events
    public interface GameListener {
        void onScoreChanged(int newScore);
        void onGameOver(int finalScore);
    }

    // Game constants
    private static final int PADDLE_WIDTH = 200;
    private static final int PADDLE_HEIGHT = 30;
    private static final int BALL_RADIUS = 20;
    private static final int BALL_SPEED_INCREMENT = 1;
    private static final int INITIAL_BALL_SPEED = 10;

    // Game objects
    private RectF paddle;
    private float ballX, ballY;
    private float ballSpeedX, ballSpeedY;
    private int score = 0;
    private boolean gameOver = false;

    // Drawing objects
    private Paint paddlePaint;
    private Paint ballPaint;
    private Paint backgroundPaint;

    // Game thread
    private GameThread gameThread;
    private boolean isRunning = false;

    // Game listener
    private GameListener gameListener;

    public PingPongView(Context context) {
        super(context);
        init();
    }

    public PingPongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize paints
        paddlePaint = new Paint();
        paddlePaint.setColor(getResources().getColor(R.color.paddle_color));
        paddlePaint.setStyle(Paint.Style.FILL);

        ballPaint = new Paint();
        ballPaint.setColor(getResources().getColor(R.color.ball_color));
        ballPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.background_color));
        backgroundPaint.setStyle(Paint.Style.FILL);

        // Set up the surface holder
        getHolder().addCallback(this);

        // Create game objects
        paddle = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        restartGame();
    }

    public void restartGame() {
        // Reset game state
        score = 0;
        gameOver = false;

        // Reset paddle position to the bottom center
        float paddleX = getWidth() / 2f - PADDLE_WIDTH / 2f;
        float paddleY = getHeight() - PADDLE_HEIGHT - 50;
        paddle.set(paddleX, paddleY, paddleX + PADDLE_WIDTH, paddleY + PADDLE_HEIGHT);

        // Reset ball position to the center
        ballX = getWidth() / 2f;
        ballY = getHeight() / 2f;

        // Set initial ball direction (randomly)
        ballSpeedX = Math.random() > 0.5 ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
        ballSpeedY = INITIAL_BALL_SPEED;

        // Notify listener of score change
        if (gameListener != null) {
            gameListener.onScoreChanged(score);
        }
    }

    public void pause() {
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        if (!gameOver) {
            isRunning = true;
            gameThread = new GameThread();
            gameThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver) {
            return true;
        }

        float touchX = event.getX();

        // Ensure paddle stays within screen bounds
        float newPaddleX = touchX - PADDLE_WIDTH / 2f;
        if (newPaddleX < 0) {
            newPaddleX = 0;
        } else if (newPaddleX + PADDLE_WIDTH > getWidth()) {
            newPaddleX = getWidth() - PADDLE_WIDTH;
        }

        // Update paddle position
        paddle.offsetTo(newPaddleX, paddle.top);

        return true;
    }

    private void update() {
        if (gameOver) {
            return;
        }

        // Move ball
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Check for collision with walls
        if (ballX - BALL_RADIUS < 0 || ballX + BALL_RADIUS > getWidth()) {
            ballSpeedX = -ballSpeedX;
        }

        // Check for collision with ceiling
        if (ballY - BALL_RADIUS < 0) {
            ballSpeedY = -ballSpeedY;
        }

        // Check for collision with paddle
        if (ballY + BALL_RADIUS >= paddle.top &&
            ballY - BALL_RADIUS <= paddle.bottom &&
            ballX >= paddle.left &&
            ballX <= paddle.right) {

            ballSpeedY = -ballSpeedY;

            // Increase ball speed slightly with each paddle hit
            if (ballSpeedX > 0) {
                ballSpeedX += BALL_SPEED_INCREMENT;
            } else {
                ballSpeedX -= BALL_SPEED_INCREMENT;
            }

            if (ballSpeedY > 0) {
                ballSpeedY += BALL_SPEED_INCREMENT;
            } else {
                ballSpeedY -= BALL_SPEED_INCREMENT;
            }

            // Increment score
            score++;
            if (gameListener != null) {
                gameListener.onScoreChanged(score);
            }
        }

        // Check if ball falls below paddle (game over)
        if (ballY - BALL_RADIUS > getHeight()) {
            gameOver = true;
            if (gameListener != null) {
                gameListener.onGameOver(score);
            }
        }
    }

    private void draw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            if (canvas != null) {
                // Clear screen
                canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

                // Draw paddle
                canvas.drawRect(paddle, paddlePaint);

                // Draw ball
                canvas.drawCircle(ballX, ballY, BALL_RADIUS, ballPaint);
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (!gameOver) {
            isRunning = true;
            gameThread = new GameThread();
            gameThread.start();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Nothing to do here
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        pause();
    }

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    private class GameThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                update();
                draw();

                // Control frame rate
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

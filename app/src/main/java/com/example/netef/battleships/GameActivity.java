package com.example.netef.battleships;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private final int NUMBER_OF_BOATS_EASY = 1;
    private final int NUMBER_OF_BOATS_NORMAL = 2;
    private final int NUMBER_OF_BOATS_HARD = 3;
    private final String PLAYER_TURN = "Your Turn";
    private final String OPPONENT_TURN = "Opponent's Turn";

    private String playerWin;
    private int i, numberOfButtons, shipCount;
    private ProgressBar progressBar;
    private TextView turn;
    private ImageButton[] playerButtons, opponentButtons;
    private Bundle bundle;
    private AnimationDrawable animationDrawable;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor gyro;
    private boolean first = true, flag = false, red = false;
    private double startXValue, xValue, time;
    private int score;
    private TextView viewScore;
    private GridLayout playerBoard, opponentBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        bundle = getIntent().getExtras();
        numberOfButtons = bundle.getInt("numberOfButtons", 0);

        /*sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(GameActivity.this, gyro, SensorManager.SENSOR_DELAY_NORMAL);//*/

        score = 100;

        viewScore = findViewById(R.id.scoreView);
        viewScore.setText("Score: " + score);

        turn = findViewById(R.id.turn);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        opponentButtons = new ImageButton[numberOfButtons];
        playerButtons = new ImageButton[numberOfButtons];

        opponentBoard = findViewById(R.id.opponentBoard);
        playerBoard = findViewById(R.id.playerBoard);

        opponentBoard.setColumnCount(4);
        opponentBoard.setRowCount(numberOfButtons);
        opponentBoard.setBackgroundColor(Color.BLUE);

        playerBoard.setColumnCount(4);
        playerBoard.setRowCount(numberOfButtons);
        playerBoard.setBackgroundColor(Color.BLUE);

        //Creating the boards
        createBoard();

        //Setting the number of ships according to difficulty
        setShipCount();

        //Putting the ships on the boards
        addShipsToBoard();

        //Adding the action listeners
        setActionListeners();


    }


    private void setActionListeners() {
        for (int j = 0; j < numberOfButtons; j++) {
            if (opponentButtons[j].getTag().toString().contentEquals("Ship")) {
                opponentButtons[j].setOnClickListener(v -> {
                    if (animationDrawable != null && animationDrawable.isRunning())
                        animationDrawable.stop();
                    handler = new Handler();
                    progressBar.setVisibility(View.VISIBLE);
                    ImageButton b = (ImageButton) v;
                    b.setTag("Clicked");
                    b.setBackgroundResource(R.drawable.explosionanimation);
                    animationDrawable = (AnimationDrawable) b.getBackground();
                    animationDrawable.start();

                    turn.setText(OPPONENT_TURN);

                    for (ImageButton o : opponentButtons)
                        o.setClickable(false);

                    if (gameOver()) {
                        endGame();
                    }

                    handler.postDelayed(() -> {

                        int random = (int) (Math.random() * numberOfButtons);

                        if (!playerButtons[random].getTag().toString().contentEquals("Clicked")) {
                            if (playerButtons[random].getTag().toString().contentEquals("Ship")) {
                                playerButtons[random].setBackgroundResource(R.drawable.explosionanimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            } else {
                                playerButtons[random].setBackgroundResource(R.drawable.wateranimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            }
                            playerButtons[random].setTag("Clicked");
                        } else {
                            while (playerButtons[random].getTag().toString().contentEquals("Clicked"))
                                random = (int) (Math.random() * numberOfButtons);

                            if (playerButtons[random].getTag().toString().contentEquals("Ship")) {
                                playerButtons[random].setBackgroundResource(R.drawable.explosionanimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            } else {
                                playerButtons[random].setBackgroundResource(R.drawable.wateranimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            }
                            playerButtons[random].setTag("Clicked");
                        }

                        for (ImageButton b1 : opponentButtons)
                            if (!b1.getTag().toString().contentEquals("Clicked"))
                                b1.setClickable(true);

                        if (gameOver()) {
                            endGame();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        turn.setText(PLAYER_TURN);
                    }, 3000);
                });
            } else {
                opponentButtons[j].setOnClickListener(v -> {
                    if (animationDrawable != null && animationDrawable.isRunning())
                        animationDrawable.stop();
                    handler = new Handler();
                    progressBar.setVisibility(View.VISIBLE);
                    ImageButton b = (ImageButton) v;
                    b.setTag("Clicked");
                    b.setBackgroundResource(R.drawable.wateranimation);
                    animationDrawable = (AnimationDrawable) b.getBackground();
                    animationDrawable.start();

                    if (score != 0)
                        score -= 10;

                    viewScore.setText("Score: " + score);
                    turn.setText(OPPONENT_TURN);

                    for (ImageButton o : opponentButtons)
                        o.setClickable(false);


                    if (gameOver()) {
                        endGame();
                    }

                    handler.postDelayed(() -> {

                        int random = (int) (Math.random() * numberOfButtons);

                        if (!playerButtons[random].getTag().toString().contentEquals("Clicked")) {
                            if (playerButtons[random].getTag().toString().contentEquals("Ship")) {
                                playerButtons[random].setBackgroundResource(R.drawable.explosionanimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();

                            } else {
                                playerButtons[random].setBackgroundResource(R.drawable.wateranimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            }
                            playerButtons[random].setTag("Clicked");
                        } else {
                            while (playerButtons[random].getTag().toString().contentEquals("Clicked"))
                                random = (int) (Math.random() * numberOfButtons);

                            if (playerButtons[random].getTag().toString().contentEquals("Ship")) {
                                playerButtons[random].setBackgroundResource(R.drawable.explosionanimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            } else {
                                playerButtons[random].setBackgroundResource(R.drawable.wateranimation);
                                animationDrawable = (AnimationDrawable) playerButtons[random].getBackground();
                                animationDrawable.start();
                            }
                            playerButtons[random].setTag("Clicked");
                        }

                        for (ImageButton b12 : opponentButtons)
                            if (!b12.getTag().toString().contentEquals("Clicked"))
                                b12.setClickable(true);

                        if (gameOver()) {
                            endGame();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        turn.setText(PLAYER_TURN);
                    }, 3000);
                });
            }
        }
    }

    private boolean gameOver() {

        boolean tp = true;
        boolean tc = true;

        for (i = 0; i < numberOfButtons; i++) {
            if (opponentButtons[i].getTag().toString().contentEquals("Ship"))
                tc = false;
        }

        for (i = 0; i < numberOfButtons; i++) {
            if (playerButtons[i].getTag().toString().contentEquals("Ship"))
                tp = false;
        }

        if (tc || tp) {
            if (tc)
                playerWin = "You won!";
            else
                playerWin = "Computer won!";
            return true;
        }
        return false;
    }

    private void setShipCount() {

        switch (bundle.getString("Difficulty", "0")) {
            case "Easy":
                shipCount = NUMBER_OF_BOATS_EASY;
                break;
            case "Normal":
                shipCount = NUMBER_OF_BOATS_NORMAL;
                break;
            case "Hard":
                shipCount = NUMBER_OF_BOATS_HARD;
                break;
        }

    }


    private void createBoard() {
        int columnIndicator = 1;

        for (i = 0; i < numberOfButtons; i++) {
            ImageButton opponentBtn = new ImageButton(this);
            ImageButton playerBtn = new ImageButton(this);

            opponentBtn.setTag("" + columnIndicator);
            playerBtn.setTag("" + columnIndicator);

            playerBtn.setClickable(false);

            opponentButtons[i] = opponentBtn;
            playerButtons[i] = playerBtn;

            opponentBoard.addView(opponentBtn, 200, 125);
            playerBoard.addView(playerBtn, 200, 125);

            columnIndicator++;
            if (columnIndicator == 5)
                columnIndicator = 1;
        }

    }

    private void addShipsToBoard() {

        while (shipCount > 0) {
            //Generating random spot
            int temp = (int) (Math.random() * (numberOfButtons - 1));

            //First column
            if (opponentButtons[temp].getTag().toString().contentEquals("1")) {
                //Horizontal or vertical ship
                if (temp + 4 >= numberOfButtons || temp + 8 >= numberOfButtons) {
                    if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 1].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 2].getTag().toString().contentEquals("Ship")) {
                        opponentButtons[temp].setTag("Ship");
                        opponentButtons[temp + 1].setTag("Ship");
                        opponentButtons[temp + 2].setTag("Ship");
                        shipCount--;
                    }
                } else if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                        && !opponentButtons[temp + 4].getTag().toString().contentEquals("Ship") &&
                        !opponentButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                    opponentButtons[temp].setTag("Ship");
                    opponentButtons[temp + 4].setTag("Ship");
                    opponentButtons[temp + 8].setTag("Ship");
                    shipCount--;
                }
                //Second column
            } else if (opponentButtons[temp].getTag().toString().contentEquals("2")) {
                //Horizontal or vertical ship
                if (temp + 4 >= numberOfButtons || temp + 8 >= numberOfButtons) {
                    if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 1].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 2].getTag().toString().contentEquals("Ship")) {
                        opponentButtons[temp].setTag("Ship");
                        opponentButtons[temp + 1].setTag("Ship");
                        opponentButtons[temp + 2].setTag("Ship");
                        shipCount--;
                    }
                } else if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                        && !opponentButtons[temp + 4].getTag().toString().contentEquals("Ship") &&
                        !opponentButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                    opponentButtons[temp].setTag("Ship");
                    opponentButtons[temp + 4].setTag("Ship");
                    opponentButtons[temp + 8].setTag("Ship");
                    shipCount--;
                }
                //Third column
            } else if (opponentButtons[temp].getTag().toString().contentEquals("3")) {
                //out of bounds check
                if (temp + 4 < numberOfButtons && temp + 8 < numberOfButtons)
                    if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 4].getTag().toString().contentEquals("Ship") &&
                            !opponentButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                        opponentButtons[temp].setTag("Ship");
                        opponentButtons[temp + 4].setTag("Ship");
                        opponentButtons[temp + 8].setTag("Ship");
                        shipCount--;
                    }
                //forth column
            } else if (opponentButtons[temp].getTag().toString().contentEquals("4")) {
                //out of bounds check
                if (temp + 4 < numberOfButtons && temp + 8 < numberOfButtons)
                    if (!opponentButtons[temp].getTag().toString().contentEquals("Ship")
                            && !opponentButtons[temp + 4].getTag().toString().contentEquals("Ship") &&
                            !opponentButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                        opponentButtons[temp].setTag("Ship");
                        opponentButtons[temp + 4].setTag("Ship");
                        opponentButtons[temp + 8].setTag("Ship");
                        shipCount--;
                    }
            }
        }

        setShipCount();


        while (shipCount > 0) {
            //Generating random spot
            int temp = (int) (Math.random() * (numberOfButtons - 1));

            //First column
            if (playerButtons[temp].getTag().toString().contentEquals("1")) {
                //Horizontal or vertical ship
                if (temp + 4 >= numberOfButtons || temp + 8 >= numberOfButtons) {
                    if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 1].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 2].getTag().toString().contentEquals("Ship")) {
                        playerButtons[temp].setTag("Ship");
                        playerButtons[temp + 1].setTag("Ship");
                        playerButtons[temp + 2].setTag("Ship");

                        playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 1].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 2].setBackgroundResource(R.drawable.ic_boat);

                        shipCount--;
                    }
                } else if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                        && !playerButtons[temp + 4].getTag().toString().contentEquals("Ship")
                        && !playerButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                    playerButtons[temp].setTag("Ship");
                    playerButtons[temp + 4].setTag("Ship");
                    playerButtons[temp + 8].setTag("Ship");

                    playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                    playerButtons[temp + 4].setBackgroundResource(R.drawable.ic_boat);
                    playerButtons[temp + 8].setBackgroundResource(R.drawable.ic_boat);
                    shipCount--;
                }
                //Second column
            } else if (playerButtons[temp].getTag().toString().contentEquals("2")) {
                //Horizontal or vertical ship
                if (temp + 4 >= numberOfButtons || temp + 8 >= numberOfButtons) {
                    if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 1].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 2].getTag().toString().contentEquals("Ship")) {
                        playerButtons[temp].setTag("Ship");
                        playerButtons[temp + 1].setTag("Ship");
                        playerButtons[temp + 2].setTag("Ship");

                        playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 1].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 2].setBackgroundResource(R.drawable.ic_boat);
                        shipCount--;
                    }
                } else if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                        && !playerButtons[temp + 4].getTag().toString().contentEquals("Ship")
                        && !playerButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                    playerButtons[temp].setTag("Ship");
                    playerButtons[temp + 4].setTag("Ship");
                    playerButtons[temp + 8].setTag("Ship");

                    playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                    playerButtons[temp + 4].setBackgroundResource(R.drawable.ic_boat);
                    playerButtons[temp + 8].setBackgroundResource(R.drawable.ic_boat);
                    shipCount--;
                }
                //Third column
            } else if (playerButtons[temp].getTag().toString().contentEquals("3")) {
                //out of bounds check
                if (temp + 4 < numberOfButtons && temp + 8 < numberOfButtons)
                    if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 4].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                        playerButtons[temp].setTag("Ship");
                        playerButtons[temp + 4].setTag("Ship");
                        playerButtons[temp + 8].setTag("Ship");

                        playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 4].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 8].setBackgroundResource(R.drawable.ic_boat);
                        shipCount--;
                    }
                //forth column
            } else if (playerButtons[temp].getTag().toString().contentEquals("4")) {
                //out of bounds check
                if (temp + 4 < numberOfButtons && temp + 8 < numberOfButtons)
                    if (!playerButtons[temp].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 4].getTag().toString().contentEquals("Ship")
                            && !playerButtons[temp + 8].getTag().toString().contentEquals("Ship")) {
                        playerButtons[temp].setTag("Ship");
                        playerButtons[temp + 4].setTag("Ship");
                        playerButtons[temp + 8].setTag("Ship");

                        playerButtons[temp].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 4].setBackgroundResource(R.drawable.ic_boat);
                        playerButtons[temp + 8].setBackgroundResource(R.drawable.ic_boat);
                        shipCount--;
                    }
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {


        /*if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (first) {
                startXValue = event.values[0];
                first = false;
            } else {
                if (Math.abs(startXValue - event.values[0]) > 3 && !flag) {
                    flag = true;
                    int random2 = (int) (Math.random() * numberOfButtons);
                    while (!playerButtons[random2].getTag().toString().contentEquals("Ship"))
                        random2 = (int) (Math.random() * numberOfButtons);
                    playerButtons[random2].setBackgroundResource(R.drawable.explosionanimation);
                    animationDrawable = (AnimationDrawable) playerButtons[random2].getBackground();
                    animationDrawable.start();
                    playerButtons[random2].setTag("Clicked");
                    if (gameOver())
                        endGame();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            flag = false;
                        }
                    }, 2000);
                    runOnUiThread(() -> {
                        for (int i = 0; i < numberOfButtons; i++) {
                            if (playerButtons[i].getTag().toString().contentEquals("Ship")) {
                                if (red)
                                    playerButtons[i].setBackgroundResource(R.drawable.ic_boat);
                                else
                                    playerButtons[i].setBackgroundResource(R.drawable.ic_boat_red);
                            }
                        }
                        red = !red;
                    });
                }
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void endGame() {
        Intent intent = new Intent(getApplicationContext(), EndActivity.class);
        intent.putExtra("numberOfButtons", numberOfButtons);
        intent.putExtra("Winner", playerWin);
        intent.putExtra("Difficulty", bundle.getString("Difficulty"));
        intent.putExtra("Score", score);
        //sensorManager.unregisterListener(GameActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        finish();
        startActivity(intent);
    }
}
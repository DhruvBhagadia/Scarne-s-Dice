package com.example.dhruv.scanrnesdice;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView yourScore, compScore, yourCurrentScore;
    Button rollButton, holdButton, resetButton, playAgainButton;
    ImageView diceImageView;
    Random random;
    int randomno, random_no_for_computer, currentUserScore, userScore, currentComputerScore,
            compSessionScore, flag;
    /*currentUserScore: Manages users points for a particular session
    userScore: total points of the user.
    compSessionScore: Manages computer's points for a particular session
    flag: to decide who won
    */
    Timer timer;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yourScore = (TextView) findViewById(R.id.yourScore);
        compScore = (TextView) findViewById(R.id.compScore);
        yourCurrentScore = (TextView) findViewById(R.id.yourCurrentScore);
        rollButton = (Button) findViewById(R.id.rollButton);
        holdButton = (Button) findViewById(R.id.holdButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        diceImageView = (ImageView) findViewById(R.id.diceImageView);
        random = new Random();
        // Initialy all scores are 0
        currentUserScore = 0;
        userScore = 0;
        random_no_for_computer = 0;
        currentComputerScore = 0;
        compSessionScore = 0;
        flag = 0;
        // For delaying computer's turn
        timer = new Timer();
        handler = new Handler();

        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Animations to feel like the die is rolling
                final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.shake);
                final Animation.AnimationListener animationListener = new
                        Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        randomno= random.nextInt(6) + 1;
                        String image = "dice" + Integer.toString(randomno);
                        int id = getResources().getIdentifier(image, "drawable",
                                getPackageName()); //To change the die image to whatever randomno
                        // comes
                        diceImageView.setImageResource(id);

                        if (randomno != 1) { //If randomno. is not 1 keep adding the score to
                            // current session's score
                            currentUserScore += randomno;
                            yourCurrentScore.setText("Your Current Score: " +
                                    Integer.toString(currentUserScore));
                        } else {
                            currentUserScore = 0; //if it rolls to be 1 then remove the previous
                            // sessions points and change it to 0
                            yourCurrentScore.setText("Your Current Score: " +
                                    Integer.toString(currentUserScore));
                            // Disable the Buttons because it's computers turn
                            rollButton.setEnabled(false);
                            holdButton.setEnabled(false);
                            computersTurn();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                anim1.setAnimationListener(animationListener);
                diceImageView.startAnimation(anim1);
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userScore += currentUserScore;// Add the current session's points to total points
                yourScore.setText("Your Score: " + Integer.toString(userScore));
                yourCurrentScore.setText("Your Current Score: 0");
                currentUserScore = 0;// For the next session
                flag = 1;
                checkIfSomeoneWon(userScore, flag);//Check if user won so here flag is 1
                // Disable the Buttons because it's computers turn
                rollButton.setEnabled(false);
                holdButton.setEnabled(false);
                computersTurn();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() { //Reset the game
            @Override
            public void onClick(View view) {
                yourCurrentScore.setText("Your Current Score: 0");
                yourScore.setText("Your Score: 0");
                compScore.setText("Computer's score: 0");
                currentUserScore = 0;
                userScore = 0;
                random_no_for_computer = 0;
                currentComputerScore = 0;
                compSessionScore = 0;
            }
        });

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show all the buttons
                resetButton.setVisibility(View.VISIBLE);
                rollButton.setVisibility(View.VISIBLE);
                holdButton.setVisibility(View.VISIBLE);
                yourCurrentScore.setText("Your Current Score: 0");
                yourScore.setText("Your Score: 0");
                compScore.setText("Computer's score: 0");
                //Reser all score to 0 for the new game
                currentUserScore = 0;
                userScore = 0;
                random_no_for_computer = 0;
                currentComputerScore = 0;
                compSessionScore = 0;
                playAgainButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    void computersTurn(){
        random_no_for_computer = 0;
        compSessionScore = 0;
        flag = 2;
        Toast.makeText(getApplicationContext(), "Computer's turn", Toast.LENGTH_SHORT).show();
        timer.schedule(new TimerTask() { //delay of 3sec for computer's turn
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // A limit of 25 to Computer's Score.
                        while(random_no_for_computer != 1 && compSessionScore <= 25){
                            random_no_for_computer= random.nextInt(6) + 1;
                            Log.i("Random no.", Integer.toString(random_no_for_computer));
                            if(random_no_for_computer == 1){ //Exit from computer's turn because
                                // the die rolled out to be 1
                                Toast.makeText(getApplicationContext(), "Computer's turn Over!"
                                                + "Computer's score in this session: " +
                                                    Integer.toString(compSessionScore),
                                                        Toast.LENGTH_SHORT).show();
                                rollButton.setEnabled(true);
                                holdButton.setEnabled(true);
                            }
                            else {
                                if(compSessionScore > 25)
                                    currentComputerScore += 25; // Whatever the points maybe add
                                    // only 25 to restrict computer
                                else
                                    currentComputerScore += random_no_for_computer;
                                compSessionScore += random_no_for_computer;
                                compScore.setText("Computer's  score: " +
                                        Integer.toString(currentComputerScore));
                                Boolean b = checkIfSomeoneWon(currentComputerScore, flag);
                                if (b) {
                                    //When we enter computer's session we disable rollButton and
                                    // holdButton hence enable both of them
                                    rollButton.setEnabled(true);
                                    holdButton.setEnabled(true);
                                    break;
                                }
                            }
                        }
                        if (compSessionScore > 25){

                            Toast.makeText(getApplicationContext(), "Computer's turn Over " +
                                            "because it scored more than 25 in one turn",
                                    Toast.LENGTH_LONG
                            ).show();
                            //When we enter computer's session we disable rollButton and
                            // holdButton hence enable both of them
                            rollButton.setEnabled(true);
                            holdButton.setEnabled(true);

                        }
                    }
                });
            }
        }, 3000);


    }
    Boolean checkIfSomeoneWon(int score, int flag){

        if(score > 100){
            //Only show playAgain button
            resetButton.setVisibility(View.INVISIBLE);
            rollButton.setVisibility(View.INVISIBLE);
            holdButton.setVisibility(View.INVISIBLE);
            playAgainButton.setVisibility(View.VISIBLE);
            if (flag == 1)
                Toast.makeText(getApplicationContext(), "You Won!", Toast.LENGTH_SHORT).show();
            else if (flag == 2)
                Toast.makeText(getApplicationContext(), "Computer Won!", Toast.LENGTH_SHORT)
                        .show();
            return true;
        }
        else
            return false;
    }
}

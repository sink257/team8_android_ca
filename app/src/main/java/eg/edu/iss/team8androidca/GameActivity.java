package eg.edu.iss.team8androidca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isBusy = false;
    private int numberOfElements;
    private MemoryButton[] buttons;
    private int[] buttonGraphicLocations;
    ArrayList<Bitmap> buttonGraphics = new ArrayList<Bitmap>();
    private int matchCount = 0;
    private TextView timerText;
    private Timer timer;
    private TimerTask timerTask;
    private Double time = 0.0;
    private Double fastestTime = 45.0;
    private Boolean isPause = false;
    private MemoryButton selectedButton1;
    private MemoryButton selectedButton2;
    private final HashSet<String> bitarrayset = new HashSet<>();
    private int clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout_activity2);

        Intent intent = getIntent();

        SharedPreferences prefArray = getPreferences(Context.MODE_PRIVATE);
        HashSet<String> retrievedArray = (HashSet<String>) prefArray.getStringSet("bitArray", null);
        if (retrievedArray != null) {
            for (String temp : retrievedArray) {
                byte[] retrievedString = Base64.decode(temp.getBytes(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(retrievedString, 0, retrievedString.length);
                buttonGraphics.add(bitmap);
            }
        } else {
            for (int i = 1; i < 7; i++) {
                byte[] bitmapdata = intent.getByteArrayExtra("selectedImg" + i);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                String encoded = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                bitarrayset.add(encoded);
                buttonGraphics.add(bitmap);
            }

            SharedPreferences.Editor editor = prefArray.edit();
            editor.putStringSet("bitArray", bitarrayset);
            editor.apply();
        }

        int numColumns = 3;
        int numRows = 4;

        numberOfElements = numColumns * numRows;
        gridLayout.setColumnCount(numColumns);
        gridLayout.setRowCount(numRows);

        timerText = findViewById(R.id.timer);

        TextView textview = findViewById(R.id.score);
        String score = "Matched sets: " + String.valueOf(matchCount) + " / " + String.valueOf(numberOfElements / 2);
        textview.setText(score);
        timer = new Timer();


        final SharedPreferences pref = getSharedPreferences("fastest_time", MODE_PRIVATE);
        fastestTime = Double.parseDouble(pref.getString("fastestTime", String.valueOf(fastestTime)));

        buttons = new MemoryButton[numberOfElements];

        buttonGraphicLocations = new int[numberOfElements];

        shuffleButtonGraphics();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MemoryButton tempButton = new MemoryButton(this, r, c, buttonGraphics.get(buttonGraphicLocations[r * numColumns + c]));
                tempButton.setId(View.generateViewId());
                tempButton.setOnClickListener(this);
                buttons[r * numColumns + c] = tempButton;
                gridLayout.addView(tempButton);
            }
        }

        TextView instructions = findViewById(R.id.instruction);
        instructions.setText("Click on one of the cards to start matching!");
    }

    @Override
    public void onClick(View view) {
        if (clickCount < 1) {
            startTime();
        }
        clickCount++;

        TextView instructions = findViewById(R.id.instruction);
        instructions.setText("");

        if (isBusy)
            return;

        MemoryButton button = (MemoryButton) view;

        if (button.isMatched)
            return;

        if (selectedButton1 == null) {
            selectedButton1 = button;
            selectedButton1.flip();
            return;
        }

//        if user presses ID twice
        if (selectedButton1.getId() == button.getId()) {
            return;
        }

        if (selectedButton1.getFrontImage() == button.getFrontImage()) {
            button.flip();

            button.setMatched(true);
            selectedButton1.setMatched(true);

            selectedButton1.setEnabled(false);
            button.setEnabled(false);

            selectedButton1 = null;

            matchCount++;
            if (matchCount < 6) {
                final MediaPlayer correctSound = MediaPlayer.create(this, R.raw.correct);
                correctSound.start();
                Toast msg = Toast.makeText(this, "Good job! Correct match!", Toast.LENGTH_SHORT);
                msg.show();
            }

            TextView textview = findViewById(R.id.score);
            String score = "Matched sets: " + String.valueOf(matchCount) + " / " + String.valueOf(numberOfElements / 2);
            textview.setText(score);

            if (matchCount == numberOfElements / 2) {
                timerTask.cancel();
                final MediaPlayer winningSound = MediaPlayer.create(this, R.raw.winning);
                winningSound.start();

                if (time >= fastestTime) {
                    gameFinished();
                } else if (fastestTime > time) {
                    fastestTime = time;
                    SharedPreferences pref = getSharedPreferences("fastest_time", MODE_PRIVATE);
                    pref.edit().putString("fastestTime", (String.valueOf(fastestTime))).apply();
                    gameWon();
                }
            }

            return;

        } else {
            selectedButton2 = button;
            selectedButton2.flip();
            final MediaPlayer wrongSound = MediaPlayer.create(this, R.raw.wrong);
            wrongSound.start();
            Toast msg = Toast.makeText(this, "Wrong match, please try again!", Toast.LENGTH_SHORT);
            msg.show();

            isBusy = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton2.flip();
                    selectedButton1.flip();

                    selectedButton1 = null;
                    selectedButton2 = null;
                    isBusy = false;
                }
            }, 1000);
        }
    }

    protected void shuffleButtonGraphics() {
        Random rand = new Random();

        for (int i = 0; i < numberOfElements; i++) {
            buttonGraphicLocations[i] = i % (numberOfElements / 2);
        }
        for (int i = 0; i < numberOfElements; i++) {
            int temp = buttonGraphicLocations[i];
            int swapIndex = rand.nextInt(12);
            buttonGraphicLocations[i] = buttonGraphicLocations[swapIndex];
            buttonGraphicLocations[swapIndex] = temp;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void startTime() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPause) {
                            time++;
                            String timerString = "Time: " + getTimerText();
                            timerText.setText(timerString);
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = (rounded % 86400) % 3600 % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onBackPressed() {
        SharedPreferences deletePref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = deletePref.edit();
        editor.remove("bitArray").apply();
        Intent intent = new Intent(this, FetchImg.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private String getFastestTimeText() {
        int rounded = (int) Math.round(fastestTime);

        int seconds = (rounded % 86400) % 3600 % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private void gameFinished() {
        String _time = getTimerText();
        String _fastestTime = getFastestTimeText();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        alertDialogBuilder
                .setTitle("Game Over! " + ("\u2639\uFE0F"))
                .setMessage("Try harder to beat the fastest time!\n\n" + "Your Time: " + _time + "\n" + "Fastest Time: " + _fastestTime + "\n")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("New Game", (dialog, which) -> {
                    SharedPreferences deletePref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = deletePref.edit();
                    editor.remove("bitArray").apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void gameWon() {
        String _time = getTimerText();
        String _fastestTime = getFastestTimeText();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        alertDialogBuilder
                .setTitle("You Win! " + ("\uD83C\uDF89"))
                .setMessage("You set a new fastest time!\n\n" + "Your Time: " + _time + "\n" + "Fastest Time: " + _fastestTime + "\n")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("New Game", (dialog, which) -> {
                    SharedPreferences deletePref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = deletePref.edit();
                    editor.remove("bitArray").apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
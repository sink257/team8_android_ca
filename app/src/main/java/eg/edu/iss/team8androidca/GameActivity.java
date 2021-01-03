package eg.edu.iss.team8androidca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private int numberOfElements;
    private MemoryButton[] buttons;
    private int[] buttonGraphicLocations;
    private int[] buttonGraphicsId;
    private int matchCount = 0;
    Chronometer chrono;
    private long timeWhenStopped = 0;

    private MemoryButton selectedButton1;
    private MemoryButton selectedButton2;

    public boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridLayout gridLayout = findViewById(R.id.grid_layout_activity2);

        chrono = findViewById(R.id.chronometer);
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });

        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.setText("00:00:00");
        chrono.start();

        int numColumns = gridLayout.getColumnCount();
        int numRows = gridLayout.getRowCount();

        numberOfElements = numColumns * numRows;

        buttons = new MemoryButton[numberOfElements];

        buttonGraphicsId = new int[numberOfElements / 2];

        buttonGraphicsId[0] = R.drawable.apple;
        buttonGraphicsId[1] = R.drawable.banana;
        buttonGraphicsId[2] = R.drawable.kiwi;
        buttonGraphicsId[3] = R.drawable.oranges;
        buttonGraphicsId[4] = R.drawable.strawberry;
        buttonGraphicsId[5] = R.drawable.watermelon;

        buttonGraphicLocations = new int[numberOfElements];

        shuffleButtonGraphics();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MemoryButton tempButton = new MemoryButton(this, r, c, buttonGraphicsId[buttonGraphicLocations[r * numColumns + c]]);
                tempButton.setId(View.generateViewId());
                tempButton.setOnClickListener(this);
                buttons[r * numColumns + c] = tempButton;
                gridLayout.addView(tempButton);
            }
        }
    }

    @Override
    public void onClick(View view) {

        TextView textview = findViewById(R.id.score);
        String score = matchCount +" / "+ numberOfElements / 2;
        textview.setText(score);

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

        if (selectedButton1.getFrontDrawableId() == button.getFrontDrawableId()) {
            button.flip();

            button.setMatched(true);
            selectedButton1.setMatched(true);

            selectedButton1.setEnabled(false);
            button.setEnabled(false);

            selectedButton1 = null;

            matchCount++;

            if(matchCount==numberOfElements/2){
                timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                chrono.stop();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                alertDialogBuilder
                        .setMessage("GAME OVER!\n" + "YOU WIN!\n" + "Your Timing: " + timeWhenStopped + "\n" + "Fastest Timing: \n")
                        .setCancelable(false)
                        .setPositiveButton("Play Again", (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


            return;

        } else {
            selectedButton2 = button;
            selectedButton2.flip();
            isBusy = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton2.flip();
                    selectedButton1.flip();

                    selectedButton1=null;
                    selectedButton2= null;
                    isBusy = false;
                }
            }, 500);
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

    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
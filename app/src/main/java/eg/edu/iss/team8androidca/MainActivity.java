package eg.edu.iss.team8androidca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button fetchBtn;
    private Double fastestTime = 45.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchBtn = (Button) findViewById(R.id.btnFetch);
        fetchBtn.setOnClickListener(v -> fetchImgActivity());

        TextView textview = findViewById(R.id.highScore);
        final SharedPreferences pref = getSharedPreferences("fastest_time", MODE_PRIVATE);

        if (pref == null) {
            String _fastestTime = getFastestTimeText();
            textview.setText("\uD83E\uDD47 " + _fastestTime);
        } else {
            fastestTime = Double.parseDouble(pref.getString("fastestTime", String.valueOf(fastestTime)));
            String _fastestTime = getFastestTimeText();
            textview.setText("\uD83E\uDD47 " + _fastestTime);
        }


    }


    private void fetchImgActivity() {
        Intent intent = new Intent(this, FetchImg.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String getFastestTimeText() {
        int rounded = (int) Math.round(fastestTime);

        int seconds = (rounded % 86400) % 3600 % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}

package eg.edu.iss.team8androidca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button mGameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameBtn = (Button) findViewById(R.id.btnGame);

        mGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });

    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

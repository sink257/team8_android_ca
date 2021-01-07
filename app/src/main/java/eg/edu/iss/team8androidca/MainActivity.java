package eg.edu.iss.team8androidca;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button gameBtn;
    Button fetchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBtn = (Button) findViewById(R.id.btnGame);
        gameBtn.setOnClickListener(v -> startGameActivity());

        fetchBtn = (Button) findViewById(R.id.btnFetch);
        fetchBtn.setOnClickListener(v -> fetchImgActivity());

    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void fetchImgActivity() {
        Intent intent = new Intent(this, FetchImg.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

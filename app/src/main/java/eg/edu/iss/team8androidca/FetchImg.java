package eg.edu.iss.team8androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FetchImg extends AppCompatActivity {

    String url = "https://stocksnap.io/search/dessert";
    ImageView imageView;
    TextView textView;
    Bitmap bitmap;
    String title;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);

        imageView = (ImageView) findViewById(R.id.test);
        textView = (TextView) findViewById(R.id.title);
        new Content().execute();
    }

    private class Content extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FetchImg.this);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Connect to the website
                Document document = Jsoup.connect(url).get();

                //Get the logo source of the website
                Element img = document.select("img[src~=(?i)\\.(png|jpe?g)]").first();
                // Locate the src attribute
                String imgSrc = img.absUrl("src");

                // Download image from URL
//                InputStream input = new java.net.URL(imgSrc).openStream();
                InputStream input = new java.net.URL(imgSrc).openConnection().getInputStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                title = imgSrc;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(bitmap);
            textView.setText(title);
            progressDialog.dismiss();

        }
    }
}


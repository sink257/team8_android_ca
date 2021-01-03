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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FetchImg extends AppCompatActivity {

    String url;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    //arrayList to store the urls
    ArrayList<Bitmap> imgBits = new ArrayList<Bitmap>();
    Bitmap bitmap;
    String title;
    ProgressDialog progressDialog;
    Button mfetch;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);
        imageView1 = (ImageView) findViewById(R.id.test1);
        imageView2 = (ImageView) findViewById(R.id.test2);
        imageView3 = (ImageView) findViewById(R.id.test3);

        mfetch = (Button) findViewById(R.id.fetch);
        mfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit = (EditText)findViewById(R.id.newURL);
                //url = "https://stocksnap.io/search/dessert";
                url = mEdit.getText().toString();
                imgBits.clear();
                new Content().execute();
            }
        });
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
                Document document = Jsoup.connect(url).get();
                //select all img elements
                Elements imgs = document.select("img[src~=(?i)\\.(png|jpe?g)]");

                ListIterator<Element> elementIt = imgs.listIterator();

                for(int i = 0; i < 3; i++){
                    if(elementIt.hasNext()){
                        String imgSrc = elementIt.next().absUrl("src");
                        InputStream input = new java.net.URL(imgSrc).openStream();
                        Bitmap imgbit = BitmapFactory.decodeStream(input);
                        imgBits.add(imgbit);
                    }
                }


//                InputStream input = new java.net.URL(imageUrls.get(3)).openConnection().getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView1.setImageBitmap(imgBits.get(0));
            imageView2.setImageBitmap(imgBits.get(1));
            imageView3.setImageBitmap(imgBits.get(2));
            progressDialog.dismiss();
        }
    }
}


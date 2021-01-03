package eg.edu.iss.team8androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FetchImg extends AppCompatActivity {

    String url;
    LinearLayout gallery;
    ImageView[] imageViews = new ImageView[20];
    //arrayList to store the urls
    ArrayList<Bitmap> imgBits = new ArrayList<Bitmap> ();
    Bitmap bitmap;
    String title;
    ProgressDialog progressDialog;
    Button mfetch;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);

        gallery = findViewById(R.id.gallery);

        // need to shift this into the code
        loadDefaultImageViews();

        mfetch = (Button) findViewById(R.id.fetch);
        mfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit = (EditText)findViewById(R.id.newURL);
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
            progressDialog.setMessage("Imma move it move it...");
            progressDialog.setMax(20);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document document = Jsoup.connect(url).get();
                //select all img elements
                Elements imgs = document.select("img[src~=(?i)\\.(png|jpe?g)]");

                ListIterator<Element> elementIt = imgs.listIterator();

                for(int i = 0; i < 20; i++){
                    if(elementIt.hasNext()){
                        String imgSrc = elementIt.next().absUrl("src");
                        InputStream input = new java.net.URL(imgSrc).openStream();
                        Bitmap imgbit = BitmapFactory.decodeStream(input);
                        imgBits.add(imgbit);
                        progressDialog.incrementProgressBy(1);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for(int i=0 ; i<20; i++)
            {
                imageViews[i].setImageBitmap(imgBits.get(i));
            }
            progressDialog.dismiss();
        }
    }

    private void loadDefaultImageViews() {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1);
        linearParams.setMargins(10,0,10,0);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);
        ivParams.setMargins(10,10,10,10);
        int count = 0 ;
        for(int i=0 ; i<5; i++){
            LinearLayout layout = new LinearLayout(this);
            layout.setWeightSum(4);
            layout.setLayoutParams(linearParams);

            for(int j=0 ; j<4 ; j++){
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.peep);
                iv.setLayoutParams(ivParams);
//                iv.setPadding(0,10,0,10);
                iv.setId(count);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageViews[count] = iv;
                layout.addView(iv);
                count++;
            }
            gallery.addView(layout);
        }
    }

    private void loadFetchedImageViews(String src, ImageView iv) {
        try {
            InputStream input = new java.net.URL(src).openStream();
            Bitmap imgbit = BitmapFactory.decodeStream(input);
            iv.setImageBitmap(imgbit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}


package eg.edu.iss.team8androidca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import android.view.inputmethod.InputMethodManager;
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
    ArrayList<Bitmap> imgBits = new ArrayList<Bitmap> ();
    ArrayList<Bitmap> imgSelected= new ArrayList<Bitmap>();
    ProgressDialog progressDialog;
    Button mfetch;
    EditText mEdit;


    int clickCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);

        gallery = findViewById(R.id.gallery);

        loadDefaultImageViews();

        mfetch = (Button) findViewById(R.id.fetch);
        mfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertToDefault();
                mEdit = (EditText)findViewById(R.id.newURL);
                url = mEdit.getText().toString();
                hideKeybaord(v);
                new Content().execute();
            }
        });
    }

    private class Content extends AsyncTask<Void, Integer, Void> {

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
                        publishProgress(i);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            imageViews[values[0]].setImageBitmap(imgBits.get(values[0]));
            progressDialog.incrementProgressBy(1);
        }
  
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for(int i=0 ; i< imgBits.size() ; i++)
            {

                imageViews[i].setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                    Bitmap img = imgBits.get(v.getId());
                        if (imgSelected.contains(img)){
                            v.setForeground(null);
                            v.setAlpha(1);
                            clickCount--;
                            imgSelected.remove(img);
                        }
                        else {
                            if (clickCount<6){
                            v.setForeground(getDrawable(R.drawable.selected));
                            v.setAlpha((float) 0.5);
                            clickCount++;
                            imgSelected.add(img);
                            }
                        }
                    }

                });
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
                iv.setId(count);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageViews[count] = iv;
                layout.addView(iv);
                count++;
            }
            gallery.addView(layout);
        }
    }


    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void revertToDefault()
    {
        imgBits.clear();
        for(ImageView iv : imageViews)
        {
            iv.setImageResource(R.drawable.peep);
            iv.setForeground(null);
        }
    }


}


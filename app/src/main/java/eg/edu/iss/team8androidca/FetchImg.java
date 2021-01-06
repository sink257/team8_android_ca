package eg.edu.iss.team8androidca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
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
import android.os.Handler;
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
import android.widget.Toast;

public class FetchImg extends AppCompatActivity {

    String url;
    LinearLayout gallery;
    ImageView[] imageViews = new ImageView[20];
    ArrayList<Bitmap> imgBits = new ArrayList<Bitmap> ();
    ArrayList<Bitmap> imgSelected= new ArrayList<Bitmap>();
    Button mfetch;
    EditText mEdit;
    int progress = 0;
    ProgressBar progressBar;
    TextView textView;
    Toast msg;
    Content content = null;


    int clickCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);
        gallery = findViewById(R.id.gallery);
        msg = Toast.makeText(this, "Download Completed!", Toast.LENGTH_SHORT);
        textView = findViewById(R.id.progress_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(20);

        loadDefaultImageViews();

        mfetch = (Button) findViewById(R.id.fetch);
        mfetch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                mEdit = (EditText)findViewById(R.id.newURL);
                url = mEdit.getText().toString();
                hideKeybaord(v);
                if (content!=null){
                    content.cancel(true);
                }
                revertToDefault();
                content = new Content();
                content.execute();

            }
        });
    }

    private class Content extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
            textView.setVisibility(textView.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document document = Jsoup.connect(url).get();
                //select all img elements
                Elements imgs = document.select("img[src~=(?i)\\.(png|jpe?g)]");

                ListIterator<Element> elementIt = imgs.listIterator();

                for(int i = 0; i < 20; i++){
                    if (isCancelled()){break;}
                    if(elementIt.hasNext()){
                        String imgSrc = elementIt.next().absUrl("src");
                        InputStream input = new java.net.URL(imgSrc).openStream();
                        Bitmap imgbit = BitmapFactory.decodeStream(input);
                        if (isCancelled()){break;}
                        imgBits.add(imgbit);
                        progressBar.incrementProgressBy(1);
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
            if(!isCancelled()){
            imageViews[values[0]].setImageBitmap(imgBits.get(values[0]));
            textView.setText(values[0]+1 + "/" + progressBar.getMax());}
        }
  
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            msg.show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);

            for(int i=0 ; i< imgBits.size() ; i++)
            {

                imageViews[i].setOnClickListener(new View.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        textView.setVisibility(View.VISIBLE);

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
                        if (imgSelected.size() == 6) {
                            byte[] byteArray = null;
                            int c =1;
                            Intent intent = new Intent(FetchImg.this, GameActivity.class);
                            for (int i=0; i<imgSelected.size();i++)
                            {
                                Bitmap bitmap = imgSelected.get(i);
                                //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byteArray = stream.toByteArray();
                                intent.putExtra("selectedImg"+c, byteArray);
                                c++;
                            }
                            startActivity(intent);
                        }
                        textView.setText(clickCount + " / 6 images selected");
                    }

                });
            }

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
        imgSelected.clear();
        clickCount = 0;
        progressBar.setProgress(0);
        textView.setText("0/" + progressBar.getMax());

        for(ImageView iv : imageViews)
        {
            iv.setImageResource(R.drawable.peep);
            iv.setForeground(null);
        }
        for (View v:imageViews)
        {
            v.setAlpha(1);
        }

    }
}


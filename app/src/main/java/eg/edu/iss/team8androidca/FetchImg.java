package eg.edu.iss.team8androidca;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ListIterator;

public class FetchImg extends AppCompatActivity {
    String url;
    LinearLayout gallery;
    ImageView[] imageViews = new ImageView[20];
    ArrayList<Bitmap> imgBits = new ArrayList<Bitmap>();
    ArrayList<Bitmap> imgSelected = new ArrayList<Bitmap>();
    Button mfetch;
    EditText mEdit;
    Button mStart;
    ProgressBar progressBar;
    TextView textView;
    Toast msg, opps;
    Content content = null;
    int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_img);
        gallery = findViewById(R.id.gallery);
        textView = findViewById(R.id.progress_text);
        mfetch = (Button) findViewById(R.id.fetch);
        mStart = (Button) findViewById(R.id.start);
        msg = Toast.makeText(this, "Download Completed!", Toast.LENGTH_SHORT);
        opps = Toast.makeText(this, "Opps! Choose another url with 6 or more images", Toast.LENGTH_LONG);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(20);

        loadDefaultImageViews();

        mfetch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                mEdit = (EditText) findViewById(R.id.newURL);
                url = mEdit.getText().toString();
                if (!Patterns.WEB_URL.matcher(url).matches()) {
                    mEdit.setError("Please enter a valid url");
                    return;
                } else if (!url.startsWith("http://")) {
                    if (!url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                }

                hideKeyboard(v);
                revertToDefault();

                if (content != null) {
                    content.cancel(true);
                }
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

                for (int i = 0; i < 20; i++) {

                    if (elementIt.hasNext()) {
                        String imgSrc = elementIt.next().absUrl("src");
                        InputStream input = new java.net.URL(imgSrc).openStream();
                        Bitmap imgbit = BitmapFactory.decodeStream(input);

                        //crop the downloaded img to imageView ratio
                        float imgBitRatio = (float) imgbit.getHeight() / imgbit.getWidth();
                        float imgViewRatio = (float) imageViews[1].getMeasuredHeight() / imageViews[1].getMeasuredWidth();
                        if (imgViewRatio > imgBitRatio) {
                            int imgbitWidth = (int) (imgbit.getHeight() / imgViewRatio);
                            int startPosX = (int) (imgbit.getWidth() - (imgbit.getHeight() / imgViewRatio)) / 2;
                            imgbit = Bitmap.createBitmap(imgbit, startPosX, 0, imgbitWidth, imgbit.getHeight());
                        } else {
                            int imgbitHeight = (int) (imgbit.getWidth() * imgViewRatio);
                            int startPosY = (int) (imgbit.getHeight() - (imgbit.getWidth() * imgViewRatio)) / 2;
                            imgbit = Bitmap.createBitmap(imgbit, 0, startPosY, imgbit.getWidth(), imgbitHeight);
                        }

                        if (isCancelled()) {
                            return null;
                        }
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
            if (!isCancelled()) {
                imageViews[values[0]].setImageBitmap(imgBits.get(values[0]));
                textView.setText(values[0] + 1 + "/" + progressBar.getMax());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            if (imgBits.size() < 6) {
                textView.setVisibility(View.INVISIBLE);
                opps.show();
            } else {
                msg.show();
                textView.setText("Please select 6 images");

                for (int i = 0; i < imgBits.size(); i++) {

                    imageViews[i].setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            textView.setVisibility(View.VISIBLE);

                            Bitmap img = imgBits.get(v.getId());
                            if (imgSelected.contains(img)) {
                                v.setForeground(null);
                                v.setAlpha(1);
                                clickCount--;
                                imgSelected.remove(img);
                                if (clickCount < 6) {
                                    mStart.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                if (clickCount < 6) {
                                    mStart.setVisibility(View.INVISIBLE);
                                    v.setForeground(getDrawable(R.drawable.selected));
                                    v.setAlpha((float) 0.5);
                                    clickCount++;
                                    imgSelected.add(img);
                                }
                            }
                            textView.setText(clickCount + " / 6 images selected");
                            if (clickCount == 6) {
                                mStart.setVisibility(View.VISIBLE);
                            }
                        }

                    });
                }

                mStart.setOnClickListener(v -> {
                    byte[] byteArray = null;
                    int c = 1;
                    Intent intent = new Intent(FetchImg.this, GameActivity.class);
                    for (int i = 0; i < imgSelected.size(); i++) {
                        Bitmap bitmap = imgSelected.get(i);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byteArray = stream.toByteArray();
                        intent.putExtra("selectedImg" + c, byteArray);
                        c++;
                    }
                    startActivity(intent);
                });
            }
        }
    }

    private void loadDefaultImageViews() {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        linearParams.setMargins(10, 0, 10, 0);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        ivParams.setMargins(10, 10, 10, 10);

        int column, row;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            column = 4;
            row = 5;
        } else {
            column = 10;
            row = 2;
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) gallery.getLayoutParams();
            lp.height = 480;
            gallery.setLayoutParams(lp);
        }

        int count = 0;
        for (int i = 0; i < row; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setWeightSum(column);
            layout.setLayoutParams(linearParams);

            for (int j = 0; j < column; j++) {
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


    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void revertToDefault() {
        imgBits.clear();
        imgSelected.clear();
        clickCount = 0;
        progressBar.setProgress(0);
        textView.setText("0/" + progressBar.getMax());
        mStart.setVisibility(View.INVISIBLE);

        for (ImageView iv : imageViews) {
            iv.setImageResource(R.drawable.peep);
            iv.setForeground(null);
            iv.setClickable(false);
        }
        for (View v : imageViews) {
            v.setAlpha(1);
        }
    }
}


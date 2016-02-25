package com.apps.jonathan.cumhoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    final static int RANDOM_CORRELATION_MIN = 1;
    final static int RANDOM_CORRELATION_MAX = 55365; // update this number, as this grows every hour...

    private ProgressDialog progressDialog;

    private static final String TAG = "MainActivity";

    public static int def_id = 0;

    private static String correlationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setValues();

        new SetCorrelation().execute(getURLbyID(def_id));
    }

    private void setValues() {
        def_id = PreferencesHandler.getDefaultId(this);
        if(def_id >= RANDOM_CORRELATION_MIN && def_id < RANDOM_CORRELATION_MAX)
            correlationTitle = getString(R.string.defaultCorrelationTitle);
        else {
            correlationTitle = getString(R.string.randomCorrelationTitle);
            def_id = 0;
        }
    }

    private URL getURLbyID(int id) {
        String u = getString(R.string.correlation_id_prefix) + ((id > 0) ? id : getRandomID());
        URL url = null;
        try {
            url = new URL(u);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private int getRandomID() {
        int randomID = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            randomID = ThreadLocalRandom.current().nextInt(RANDOM_CORRELATION_MIN, RANDOM_CORRELATION_MAX + 1);
        }
        else {
            Random r = new Random();
            randomID = r.nextInt((RANDOM_CORRELATION_MAX - RANDOM_CORRELATION_MIN) + 1) + RANDOM_CORRELATION_MIN;
        }
        return randomID;
    }

    private class SetCorrelation extends AsyncTask<URL, Void, Document> {
        private String TAG = "SetCorrelation";

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.progressDialogMassage));
            progressDialog.show();
        }

        @Override
        protected Document doInBackground(URL... urls) {
            Log.d(TAG, "doInBackground: ");
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String r = "";
            StringBuilder sb = new StringBuilder();
            InputStream is = null;
            try {
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                r = sb.toString();
                Log.d(TAG, "doInBackground: string 0 - 200: ");
                Log.d(TAG, "doInBackground: " + r.substring(0, 200));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Document d = Jsoup.parse(r);
            Log.d(TAG, "doInBackground: d null? " + Boolean.toString(d == null));
            return d;
        }

        protected void onPostExecute(Document r) {
            if(r!= null) {
                Elements c_elements = r.getElementsByAttributeValue("class", "variable");
                String c1 = c_elements.get(0).text();
                String c2 = c_elements.get(1).text();
                Elements img_elements = r.getElementsByAttributeValueContaining("src", "correlation_project/correlation_images/");
                String img_src = "http://tylervigen.com/" + img_elements.get(0).attributes().get("src");
                Elements td_elements = r.getElementsByAttributeValue("colspan", "2");
                String broad_corr = td_elements.get(0).text(); //Correlation: 0.77621
                double corr = Double.parseDouble(broad_corr.substring(12));
                Element elementId = r.getElementsByAttributeValueContaining("href", "view_correlation?id=").get(0);
                String s = elementId.attr("href");
                int id = Integer.parseInt(s.substring(20));

                // set views:
                TextView corr1 = (TextView) findViewById(R.id.CorrelationArg1);
                TextView corr2 = (TextView) findViewById(R.id.CorrelationArg2);
                TextView cPercentage = (TextView) findViewById(R.id.CorrelationPercentage);
                TextView corrTitle = (TextView) findViewById(R.id.CorrelationTitle);
                TextView corrWith = (TextView) findViewById(R.id.CorrelationWith);
                Button b = (Button) findViewById(R.id.CorrelationButton);

                corr1.setText(c1);
                corr2.setText(c2);
                cPercentage.setText(getString(R.string.cPercentagePrefix) + corr);
                corrTitle.setText(correlationTitle);
                corrWith.setText(getString(R.string.correlationWith));

                FavoritesActivity.FavoriteCorrelation fc = new FavoritesActivity.FavoriteCorrelation(id, c1, c2);
                b.setText(R.string.correlationAddFavoriteButton);
                b.setVisibility(Button.VISIBLE);
                b.setTag(fc);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FavoritesActivity.FavoriteCorrelation fc = (FavoritesActivity.FavoriteCorrelation) v.getTag();
                        PreferencesHandler.addFavoriteCorrelation(fc, getApplicationContext());
                        Toast.makeText(getApplicationContext(), R.string.addFavorite, Toast.LENGTH_SHORT).show();
                        Button b = (Button) v;
                        b.setVisibility(View.INVISIBLE);
                    }
                });
                new SetCorrelationImage().execute(img_src);
            } else {
                Toast.makeText(MainActivity.this, R.string.correlationLoadError, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SetCorrelationImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(urls[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                ImageView imageView = (ImageView) findViewById(R.id.CorrelationImageView);
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(MainActivity.this, R.string.imageLoadError, Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }

    public void openFavoritesActivity(View v) {
        Intent i = new Intent(this, FavoritesActivity.class);
        startActivity(i);
    }
}

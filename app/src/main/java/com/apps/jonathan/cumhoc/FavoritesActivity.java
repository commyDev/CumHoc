package com.apps.jonathan.cumhoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
    }


    public static class FavoriteCorrelation {
        private int correlationId;

        public int getCorrelationId() {
            return correlationId;
        }

        public String getArg1() {
            return arg1;
        }

        public String getArg2() {
            return arg2;
        }

        private String arg1;
        private String arg2;

        public FavoriteCorrelation(int id, String a1, String a2) {
            this.correlationId = id;
            this.arg1 = a1;
            this.arg2 = a2;
        }
    }
}

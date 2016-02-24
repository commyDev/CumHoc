package com.apps.jonathan.cumhoc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private FavoritesAdapter fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ListView Favorites = (ListView) findViewById(R.id.FavoritesListView);
        ArrayList<FavoriteCorrelation> l = new ArrayList<>();
        l.add(new FavoriteCorrelation(100001,"1a", "1b"));
        l.add(new FavoriteCorrelation(100002,"2a", "2b"));
        l.add(new FavoriteCorrelation(100003,"3a", "3b"));
        l.add(new FavoriteCorrelation(100004,"4a", "4b"));

        fa = new FavoritesAdapter(l, this);
        Favorites.setAdapter(fa);
    }


    public static class FavoriteCorrelation {
        private int correlationId;
        private String arg1;
        private String arg2;
        private boolean isDefault = false;

        public FavoriteCorrelation(int id, String a1, String a2) {
            this.correlationId = id;
            this.arg1 = a1;
            this.arg2 = a2;
        }

        public int getCorrelationId() {
            return correlationId;
        }

        public String getArg1() {
            return arg1;
        }

        public String getArg2() {
            return arg2;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setIsDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }

    }

    private class FavoritesAdapter extends BaseAdapter implements ListAdapter {

        private static final String TAG = "FavoritesAdapter";

        private static final int TYPE_REGULAR = 0;
        private static final int TYPE_DEFAULT = 1;
        private static final int TYPE_MAX = 2;

        private ArrayList<FavoriteCorrelation> list = new ArrayList<>();
        private Context context;
        private LayoutInflater inflater;

        public FavoritesAdapter(ArrayList<FavoriteCorrelation> list, Context context) {
            this.list = list;
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemViewType(int position) {
            if(getItem(position).isDefault() && position == 0)
                return TYPE_DEFAULT;
            else
                return TYPE_REGULAR;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public FavoriteCorrelation getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0; //list.get(pos).getId()
            //just return 0 if your list items do not have an Id variable.
        }

        public void editDefault(int pos, boolean b) {
            list.get(pos).setIsDefault(b);
        }

        public void setDefault(int pos) {
            FavoriteCorrelation c = list.get(pos);
            c.setIsDefault(true);
            list.remove(c);
            list.add(0, c);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            vHolder holder = null;
            FavoriteCorrelation c = getItem(position);
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new vHolder();
                switch(type) {
                    case TYPE_DEFAULT:
                        convertView = inflater.inflate(R.layout.default_correlation_list_item, null);
                        holder.arg1 = (TextView) convertView.findViewById(R.id.default_corr_list_item_arg1);
                        holder.arg2 = (TextView) convertView.findViewById(R.id.default_corr_list_item_arg2);
                        holder.b = (Button) convertView.findViewById(R.id.default_corr_list_item_button);
                        break;
                    case TYPE_REGULAR:
                        convertView = inflater.inflate(R.layout.regular_correlation_list_item, null);
                        holder.arg1 = (TextView) convertView.findViewById(R.id.regular_corr_list_item_arg1);
                        holder.arg2 = (TextView) convertView.findViewById(R.id.regular_corr_list_item_arg2);
                        holder.b = (Button) convertView.findViewById(R.id.regular_corr_list_item_button);
                        break;
                }
                convertView.setTag(holder);
            } else
                holder = (vHolder) convertView.getTag();

            holder.arg1.setText(c.getArg1());
            holder.arg2.setText(c.getArg2());
            holder.b.setTag(c.getCorrelationId());
            if(c.isDefault()) {
                holder.b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.def_id = 0;
                        PreferencesHandler.setDefaultId(0, FavoritesActivity.this);
                        fa.editDefault(0, false);
                        fa.notifyDataSetChanged();
                    }
                });
            } else {
                holder.b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = (int) v.getTag();
                        MainActivity.def_id = id;
                        PreferencesHandler.setDefaultId(id, FavoritesActivity.this);
                        fa.editDefault(0, false);
                        fa.setDefault(position);
                        fa.notifyDataSetChanged();
                    }
                });
            }



            return convertView;
        }
    }

    public static class vHolder {
        public TextView arg1;
        public TextView arg2;
        public Button b;
    }

}

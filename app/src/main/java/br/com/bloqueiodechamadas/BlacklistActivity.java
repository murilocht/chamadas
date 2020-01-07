package br.com.bloqueiodechamadas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import br.com.bloqueiodechamadas.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BlacklistActivity extends AppCompatActivity {

    public ListView lv;
    public TextView textEmptyBlacklist;
    public ArrayList<String> modelArrayList;

    public Adapter adapter = null;

    public SharedPreferences prefs;
    public SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.home));
        }

        lv = findViewById(R.id.lv);
        textEmptyBlacklist = findViewById(R.id.textEmptyBlacklist);

        prefs = PreferenceManager.getDefaultSharedPreferences(BlacklistActivity.this);

        if (getArrayList("modelArrayList") != null) {
            textEmptyBlacklist.setVisibility(View.INVISIBLE);

            if (adapter == null) {
                modelArrayList = getArrayList("modelArrayList");
                adapter = new Adapter(BlacklistActivity.this, modelArrayList);
                lv.setAdapter(adapter);
            }
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if (key.equals("modelArrayList")) {
                    if (getArrayList("modelArrayList") != null && getArrayList("modelArrayList").size() > 0) {
                        textEmptyBlacklist.setVisibility(View.INVISIBLE);
                        lv.setVisibility(View.VISIBLE);

                        if (adapter == null) {
                            modelArrayList = getArrayList("modelArrayList");
                            adapter = new Adapter(BlacklistActivity.this, modelArrayList);
                            lv.setAdapter(adapter);
                        }

                        adapter.update(getArrayList("modelArrayList"));
                    } else {
                        textEmptyBlacklist.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public ArrayList<String> getArrayList(String key){
        Gson gson = new Gson();
        String json = prefs.getString(key, null);

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addNumber(View view) {
        String[] opcoes = {
                getResources().getString(R.string.calls_log),
                getResources().getString(R.string.contacts)
        };

        new AlertDialog.Builder(BlacklistActivity.this)
                .setSingleChoiceItems(opcoes, 0, null)
                .setPositiveButton(getResources().getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                        startActivityNumberList(checkedItem);
                    }
                })
                .create()
                .show();
    }

    public void startActivityNumberList(Object checkedItem) {
        Intent intent = new Intent(BlacklistActivity.this, NumberListActivity.class);
        intent.putExtra("checkedItem", String.valueOf(checkedItem));
        startActivity(intent);
    }
}

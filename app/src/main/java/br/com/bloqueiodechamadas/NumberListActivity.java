package br.com.bloqueiodechamadas;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.widget.ListView;
import android.provider.ContactsContract;

import com.google.gson.Gson;
import br.com.bloqueiodechamadas.R;

import java.util.ArrayList;

public class NumberListActivity extends AppCompatActivity {

    public ListView lv;
    public ArrayList<NumberModel> modelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list);

        Intent intent = getIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(intent.getStringExtra("checkedItem"));
        }

        lv = findViewById(R.id.lv);

        modelArrayList = readNumbers();

        CustomAdapter customAdapter = new CustomAdapter(NumberListActivity.this, modelArrayList);
        lv.setAdapter(customAdapter);
    }

    public ArrayList<NumberModel> readNumbers() {
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return new ArrayList<>();
        }

        ArrayList<NumberModel> list = new ArrayList<>();
        String[] projection;
        String search = null;
        String checkedItem = getIntent().getStringExtra("checkedItem");
        Cursor cursor = null;

        if (checkedItem != null && checkedItem.equals(getResources().getString(R.string.calls_log))) {
            projection = new String[]{CallLog.Calls.NUMBER};

            cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
            );

            search = CallLog.Calls.NUMBER;

        } else if (checkedItem != null && checkedItem.equals(getResources().getString(R.string.contacts))) {
            projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            search = ContactsContract.CommonDataKinds.Phone.NUMBER;
        }

        if (cursor != null) {
            cursor.moveToFirst();
            do {
                String number = cursor.getString(cursor.getColumnIndex(search));

                NumberModel model = new NumberModel();
                model.setSelected(false);
                model.setNumber(number);

                list.add(model);
            } while (cursor.moveToNext());

            cursor.close();
        }

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 1; j < list.size(); j++) {
                if(list.get(i).getNumber().equals(list.get(j).getNumber())) {
                 list.remove(i);
                }
            }
        }

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveArrayList();
    }

    public void saveArrayList(){

        ArrayList<String> newArrayList = new ArrayList<>();

        for (NumberModel arrayList : modelArrayList) {
            if (arrayList.getSelected()) {
                newArrayList.add(arrayList.getNumber());
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NumberListActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newArrayList);
        editor.putString("modelArrayList", json);
        editor.apply();
    }
}

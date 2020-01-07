package br.com.bloqueiodechamadas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.TextView;

import br.com.bloqueiodechamadas.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public TextView textTitle;
    public TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(permissionsIsGranted()) {
            permission();
        }

        textTitle = findViewById(R.id.textTitle);
        textMessage = findViewById(R.id.textMessage);

        setTexts();
    }

    public void setTexts() {
        textTitle.setText(getResources().getString(R.string.text_title));
        textMessage.setText(getResources().getString(R.string.text_message));
    }

    public boolean permissionsIsGranted() {
        int hasSendSmsPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
        int hasReadCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);

        return
                hasSendSmsPermission == PackageManager.PERMISSION_GRANTED &&
                hasReadCallLogPermission == PackageManager.PERMISSION_GRANTED &&
                hasReadContactsPermission == PackageManager.PERMISSION_GRANTED &&
                getSystemService(TelecomManager.class).getDefaultDialerPackage().equals(getPackageName());
    }

    public void close(View v) {
        finish();
    }

    public void contine(View v) {
        permission();
    }

    public void permission() {
        int hasSendSmsPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
        int hasReadCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);

        List<String> permissions = new ArrayList<>();

        if (hasSendSmsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS);
        }

        if (hasReadCallLogPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), 1);
        } else {
            offerReplacingDefaultDialer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean requiredPermission = false;

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                requiredPermission = true;
            }
        }

        if (!requiredPermission) {
            offerReplacingDefaultDialer();
        } else {
            permission();
        }
    }

    public void offerReplacingDefaultDialer() {
        if (!getSystemService(TelecomManager.class).getDefaultDialerPackage().equals(getPackageName())) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());

            startActivity(intent);
        } else {
            startBlacklistActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            startBlacklistActivity();
        } else {
            offerReplacingDefaultDialer();
        }
    }

    public void startBlacklistActivity() {
        Intent intent = new Intent(MainActivity.this, BlacklistActivity.class);
        startActivity(intent);
        finish();
    }
}

package br.com.bloqueiodechamadas;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import br.com.bloqueiodechamadas.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IncomingCallService extends CallScreeningService {
    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IncomingCallService.this);
        Gson gson = new Gson();
        String json = prefs.getString("modelArrayList", null);

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> modelArrayList = gson.fromJson(json, type);

        String number = callDetails.getHandle().toString().replace("tel:", "");
        String originalNumber = callDetails.getHandle().toString();

        if (modelArrayList != null && modelArrayList.contains(number)) {
            respondToCall(callDetails, rejectCall());
            sendSMS(originalNumber);
        }
    }

    public CallResponse rejectCall() {
        return new CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(false)
                .setSkipCallLog(false)
                .build();
    }

    public void sendSMS(String number) {
        if (number != null && !number.equals("")) {
            SmsManager smsManager = SmsManager.getDefault();
            String message = getString(R.string.message);
            smsManager.sendTextMessage(number,null, message, null,null);
        }
    }
}

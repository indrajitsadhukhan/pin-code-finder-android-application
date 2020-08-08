package com.example.automaticaddressfill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private EditText txt_city, txt_state;
    private RequestQueue mQueue;
    private StringBuilder s_total, s_phone, s_pin, s_city, s_state, s_others;
    boolean b = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
    }

    public void onClick1(View v) {
        TextView pin = findViewById(R.id.pin);
        jsonparse(pin.getText().toString());

    }

    public void onClick(View v) {
        if (b) {
            s_total = new StringBuilder();
            s_phone = new StringBuilder();
            s_pin = new StringBuilder();
            s_others = new StringBuilder();
            EditText e = findViewById(R.id.edtTxt);
            s_total.append(e.getText().toString());
            System.out.println("TOTAL" + s_total);
            findPhone(s_total, s_phone);
            System.out.println("Phone" + s_phone);
            findPin(s_total, s_pin);
            System.out.println("pin" + s_pin);
            System.out.println("tot" + s_total);
            EditText phone = findViewById(R.id.phone);
            EditText others = findViewById(R.id.others);
            EditText pin = findViewById(R.id.pin);
            phone.setText(s_phone);
            pin.setText(s_pin);
            jsonparse(s_pin.toString());
            others.setText(s_total);
            Button btn = findViewById(R.id.btn);
            btn.setText("RESET");
            b = !b;
        } else {
            Button btn = findViewById(R.id.btn);
            btn.setText("GET RESULT");
            EditText phone = findViewById(R.id.phone);
            EditText others = findViewById(R.id.others);
            EditText pin = findViewById(R.id.pin);
            EditText city = findViewById(R.id.city);
            EditText state = findViewById(R.id.state);
            phone.setText("");
            pin.setText("");
            others.setText("");
            city.setText("");
            state.setText("");
            b = !b;
        }
    }

    private void jsonparse(String s) {
        String url = "https://api.postalpincode.in/pincode/" + s;
        JsonArrayRequest jarr = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                TextView err = findViewById(R.id.error);
                err.setVisibility(View.INVISIBLE);
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    JSONArray jarr1 = jsonObject.getJSONArray("PostOffice");
                    JSONObject jsonObject1 = jarr1.getJSONObject(0);
                    String city = jsonObject1.getString("District");
                    String state = jsonObject1.getString("State");
                    txt_city = findViewById(R.id.city);
                    txt_state = findViewById(R.id.state);
                    txt_city.setText(city);
                    txt_state.setText(state);
                } catch (Exception e) {
                    EditText pin = findViewById(R.id.pin);
                    if (pin.getText().toString().length() != 0) {
                        err = findViewById(R.id.error);
                        err.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(jarr);
    }

    private static boolean isNum(char c) {
        if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
            return true;
        }
        return false;
    }

    private static void findPhone(StringBuilder a, StringBuilder b) {
        a.append("a");
        int count = 0, i, s = 0, i1;
        ArrayList<Integer> arr = new ArrayList<>();
        for (i = 0; i < a.length(); i++) {
            while (i < a.length() && isNum(a.charAt(i))) {
                count++;
                i++;
            }
            if (count >= 10 && count < 13) {
                break;
            }
            count = 0;
        }
        if (count >= 10) {
            if (i >= a.length()) {
                i = a.length() - 1;
            }
            StringBuilder a1 = new StringBuilder();
            a1.append(a);
            if (i >= 10) {
                for (int j = 0; j < 10; j++) {
                    b.append(a1.charAt(i - 10 + j));
                    arr.add(i - 10 + j);
                }
            }
            Collections.sort(arr);
            for (i = arr.size() - 1; i >= 0; i--) {
                a.deleteCharAt(arr.get(i));
            }
            if (a.length() > 0)
                a.deleteCharAt(a.length() - 1);
        }
        if (a.length() > 0)
            a.deleteCharAt(a.length() - 1);
    }

    private static void findPin(StringBuilder a, StringBuilder b) {
        a.append("a");
        int count = 0, i, s = 0, i1;
        ArrayList<Integer> arr = new ArrayList<>();
        for (i = 0; i < a.length(); i++) {
            while (i < a.length() && isNum(a.charAt(i))) {
                count++;
                i++;
            }

            if (count == 6) {
                break;
            }
            count = 0;
        }
        if (count == 6) {
            if (i >= a.length()) {
                i = a.length() - 1;
            }
            StringBuilder a1 = new StringBuilder();
            a1.append(a);
            if (i >= 6) {
                for (int j = 0; j < 6; j++) {
                    b.append(a1.charAt(i - 6 + j));
                    arr.add(i - 6 + j);
                }
            }
            Collections.sort(arr);
            for (i = arr.size() - 1; i >= 0; i--) {
                a.deleteCharAt(arr.get(i));
            }
            if (a.length() > 0)
                a.deleteCharAt(a.length() - 1);
        }
        if (a.length() > 0)
            a.deleteCharAt(a.length() - 1);
    }
}
package com.rever.myforum.member;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.network.RemoteAccess;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "Login";

    private Activity activity;
    private SharedPreferences shp;
    private Button buttonLogin;
    private EditText editTextAccount, editTextPassword;
    private TextView textViewForgetPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shp = MainActivity.getShp(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonLogin = view.findViewById(R.id.signIn_buttonLogin);
        editTextAccount = view.findViewById(R.id.signIn_editTextAccount);
        editTextPassword = view.findViewById(R.id.signIn_editTextPassword);
        textViewForgetPassword = view.findViewById(R.id.signIn_textViewForgetPassword);

        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.signIn_buttonLogin) {
            if (editTextAccount.getText().toString().trim().isEmpty() ||
                    editTextPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity, R.string.toast_notEmpty, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailValid(editTextAccount.getText().toString())) {
                editTextAccount.setError("無效信箱");
                return;
            }

            if (RemoteAccess.networkConnected(activity)) {
                String url = RemoteAccess.URL_SERVER + "MemberServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "login");
                jsonObject.addProperty("account", editTextAccount.getText().toString());
                jsonObject.addProperty("password", editTextPassword.getText().toString());
                String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
                int result;
                if (!jsonIn.isEmpty()) {
                    result = Integer.parseInt(jsonIn);
                } else {
                    result = 1;
                }

                Log.d(TAG, "loginResultCode: " + result);

                if (result == 0) {
                    Toast.makeText(activity, R.string.toast_signInSuccess, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, R.string.toast_signInFail, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
            }

        } else if (itemId == R.id.signIn_textViewForgetPassword) {
            //
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }
}
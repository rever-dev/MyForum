package com.rever.myforum.member;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.network.RemoteAccess;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SignUpFrag";

    private Activity activity;
    private Button buttonSignUp;
    private EditText editTextAccount, editTextNickname, editTextPassword, editTextPasswordAgain;
    private TextView textViewTermsOfService, textViewPrivacyPolicy;
    private SharedPreferences shp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shp = MainActivity.getShp(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSignUp = view.findViewById(R.id.signUp_buttonSignUp);
        editTextAccount = view.findViewById(R.id.signUp_editTextAccount);
        editTextNickname = view.findViewById(R.id.signUp_editTextNickname);
        editTextPassword = view.findViewById(R.id.signUp_edittextPassword);
        editTextPasswordAgain = view.findViewById(R.id.signUp_editTextPasswordAgain);
        textViewTermsOfService = view.findViewById(R.id.signUp_textViewTermsOfService);
        textViewPrivacyPolicy = view.findViewById(R.id.signUp_textViewPrivacyPolicy);

        buttonSignUp.setOnClickListener(this);
        textViewTermsOfService.setOnClickListener(this);
        textViewPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.signUp_buttonSignUp) {
            boolean checkResult = true;
            /* 檢查信箱格式 */
            if (editTextAccount.getText().toString().trim().isEmpty() ||
                    !MainActivity.isEmailValid(editTextAccount.getText().toString().trim())) {
                editTextAccount.setError("無效信箱");
                checkResult = false;
            }
            /* 檢查暱稱 */
            if (editTextNickname.getText().toString().trim().isEmpty()) {
                editTextNickname.setError("無效暱稱");
                checkResult = false;
            }
            /* 檢查密碼 必須大於6字元 */
            if (editTextPassword.getText().toString().trim().isEmpty() ||
                    editTextPassword.getText().length() < 6) {
                editTextPassword.setError("無效密碼");
                checkResult = false;
            }
            /* 檢查再次輸入密碼 必須與密碼一致 */
            if (editTextPasswordAgain.getText().toString().trim().isEmpty() ||
                    !editTextPasswordAgain.getText().toString().trim().equals(editTextPassword.getText().toString().trim())) {
                editTextPasswordAgain.setError("與密碼不符");
                checkResult = false;
            }

            if (checkResult) {
                if (RemoteAccess.networkConnected(activity)) {
                    String url = RemoteAccess.URL_SERVER + "MemberServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "signUp");
                    jsonObject.addProperty("nickname", editTextNickname.getText().toString().trim());
                    jsonObject.addProperty("account", editTextAccount.getText().toString().trim());
                    jsonObject.addProperty("password", editTextPassword.getText().toString().trim());
                    String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
                    int result;
                    if (!jsonIn.isEmpty()) {
                        result = Integer.parseInt(jsonIn);
                    } else {
                        result = 1;
                    }

                    Log.d(TAG, "signUpResultCode: " + result);

                    /* *
                    *   result == 0 : 成功
                    *   result == 1 : 失敗
                    *   result == 2 : 帳號重覆
                    * */
                    if (result == 0) {
                        Toast.makeText(activity, R.string.toast_signUpSuccess, Toast.LENGTH_SHORT).show();
                        shp.edit().putBoolean("signIn", true)
                                .putString("account", editTextAccount.getText().toString().trim())
                                .putString("password", editTextPassword.getText().toString().trim())
                                .apply();
                        MemberBase.getMemberDetail(activity, editTextAccount.getText().toString());
                        Navigation.findNavController(v).navigate(R.id.memberDetailFragment);
                    } else if (result == 1) {
                        Toast.makeText(activity, R.string.toast_signUpFail, Toast.LENGTH_SHORT).show();
                    } else if (result == 2) {
                        Toast.makeText(activity, R.string.toast_accountIsUsed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
                }
            }

        } else if (itemId == R.id.signUp_textViewTermsOfService) {
            Toast.makeText(activity, "服務條款", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.signUp_textViewPrivacyPolicy) {
            Toast.makeText(activity, "隱私政策", Toast.LENGTH_SHORT).show();
        }
    }
}
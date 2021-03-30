package com.rever.myforum.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rever.myforum.R;

public class LoginFragment extends Fragment {

    private Button buttonLogin, buttonSingUp;
    private EditText editTextAccount, editTextPassword;
    private TextView textViewForgetPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonLogin = view.findViewById(R.id.login_buttonLogin);
        buttonSingUp = view.findViewById(R.id.login_buttonSignUp);
        editTextAccount = view.findViewById(R.id.login_editTextAccount);
        editTextPassword = view.findViewById(R.id.login_editTextPassword);
        textViewForgetPassword = view.findViewById(R.id.login_textViewForgetPassword);
    }
}
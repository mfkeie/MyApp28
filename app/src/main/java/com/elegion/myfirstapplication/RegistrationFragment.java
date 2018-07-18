package com.elegion.myfirstapplication;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elegion.myfirstapplication.model.Error;
import com.elegion.myfirstapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;

public class RegistrationFragment extends Fragment {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mRegistration;

    public static RegistrationFragment newInstance() {

        return new RegistrationFragment();
    }

    private View.OnClickListener mOnRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isInputValid()) {
                User user = new User(
                        mEmail.getText().toString(),
                        mName.getText().toString(),
                        mPassword.getText().toString());

                ApiUtils.getApiService().registration(user).enqueue(
                        new retrofit2.Callback<Void>() {
                            //используем Handler, чтобы показывать ошибки в Main потоке, т.к. наши коллбеки возвращаются в рабочем потоке
                            Handler mainHandler = new Handler(getActivity().getMainLooper());

                            @Override
                            public void onResponse(retrofit2.Call<Void> call, final retrofit2.Response<Void> response) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!response.isSuccessful()) {
                                            boolean flag = false;
                                            //todo добавить полноценную обработку ошибок по кодам ответа от сервера и телу запроса
                                            //showMessage(R.string.registration_error);
                                            try {
                                                if (response.code() == 400) {
                                                    Gson gson = new Gson();
                                                    Error error = gson.fromJson(response.errorBody().string(), Error.class);
                                                    if(error.getData() != null) {
                                                        String errorMessage = "";
                                                        if (error.getData().getEmail() != null && error.getData().getEmail().length > 0) {
                                                            String emailErrorMessage = createErrorMessage(error.getData().getEmail());
                                                            mEmail.setError(emailErrorMessage);
                                                            mEmail.setBackgroundResource(R.drawable.edit_text_style);
                                                            errorMessage = errorMessage + emailErrorMessage;
                                                        }
                                                        if (error.getData().getName() != null && error.getData().getName().length > 0) {
                                                            String nameErrorMessage = createErrorMessage(error.getData().getName());
                                                            mName.setError(nameErrorMessage);
                                                            mName.setBackgroundResource(R.drawable.edit_text_style);
                                                            errorMessage = errorMessage + nameErrorMessage;
                                                        }
                                                        if (error.getData().getPassword() != null && error.getData().getPassword().length > 0) {
                                                            String passwordErrorMessage = createErrorMessage(error.getData().getPassword());
                                                            mPassword.setError(passwordErrorMessage);
                                                            mPassword.setBackgroundResource(R.drawable.edit_text_style);
                                                            errorMessage = errorMessage + passwordErrorMessage;
                                                        }
                                                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                                        flag = true;
                                                    }
                                                }
                                                if (response.code() == 500) {
                                                    Toast.makeText(getActivity(), "Внутренняя ошибка сервера", Toast.LENGTH_LONG).show();
                                                    flag = true;
                                                }
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            if(!flag)
                                                showMessage(R.string.registration_error);
                                        } else {
                                            showMessage(R.string.registration_success);
                                            getFragmentManager().popBackStack();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMessage(R.string.request_error);
                                    }
                                });
                            }
                        });
            } else {
                showMessage(R.string.input_error);
            }
        }
    };

    private String createErrorMessage(String[] errors) {
        StringBuffer sb = new StringBuffer();
        for(String error : errors)
            sb.append(error);
        return sb.toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_registration, container, false);

        mEmail = view.findViewById(R.id.etEmail);
        mName = view.findViewById(R.id.etName);
        mPassword = view.findViewById(R.id.etPassword);
        mPasswordAgain = view.findViewById(R.id.tvPasswordAgain);
        mRegistration = view.findViewById(R.id.btnRegistration);

        mRegistration.setOnClickListener(mOnRegistrationClickListener);
        final Drawable originalDrawable = mEmail.getBackground();

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEmail.setBackgroundDrawable(originalDrawable);
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mName.setBackgroundDrawable(originalDrawable);
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPassword.setBackgroundDrawable(originalDrawable);
            }
        });


        return view;
    }

    private boolean isInputValid() {
        return isEmailValid(mEmail.getText().toString())
                && !TextUtils.isEmpty(mName.getText())
                && isPasswordsValid();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordsValid() {
        String password = mPassword.getText().toString();
        String passwordAgain = mPasswordAgain.getText().toString();

        return password.equals(passwordAgain)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordAgain);
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

}

package com.shritechsoft.stud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shreyaspatil.MaterialDialog.AbstractDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class studentRegister extends AppCompatActivity {

    EditText e_email;
    EditText e_password, c_password;
    Button signup;
    String Eemail, Epassword, Ecpassword;
    private MaterialDialog mProgressDialog;

    private static final String RegisterUrl = "http://sbm.visionitsoftware.in/userRegistation/registration.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        e_email = findViewById(R.id.email);
        e_password = findViewById(R.id.password);
        c_password = findViewById(R.id.cpassword);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSuccessRegister();
            }
        });

    }

    private void onSuccessRegister() {
        initialize();
        if (!validate()) {
            Toast.makeText(studentRegister.this, "Signup Failed", Toast.LENGTH_SHORT).show();
        } else {
            registerUser();
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (Eemail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Eemail).matches()) {
            e_email.setError("Please Enter Valid Email Address");
            valid = false;
        }
        if (Epassword.compareTo(Ecpassword) != 0) {
            c_password.setError("Password is not match ");
            valid = false;
        }
        return valid;
    }

    private void registerUser() {


        final String email = e_email.getText().toString().trim().toLowerCase();
        final String password = e_password.getText().toString().trim().toLowerCase();


        Log.e("email", email);
        Log.e("password", password);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RegisterUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.e("RESPONSE", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        showDialog("Login Status", success, R.raw.success, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                startActivity(new Intent(studentRegister.this, Login.class));
                                dialogInterface.dismiss();
                            }
                        });
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    signup.setVisibility(View.VISIBLE);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showDialog("Login Status", error.toString(), R.raw.success, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // on click
                                dialogInterface.dismiss();
                            }
                        });
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("password", password);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        showProgressDialog("Registering");
    }

    private void initialize() {
        Eemail = e_email.getText().toString().trim();
        Epassword = e_password.getText().toString().trim();
        Ecpassword = c_password.getText().toString().trim();
    }


    private void showDialog(String title, String message, @RawRes int animationRes, AbstractDialog.OnClickListener onClickListener) {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setAnimation(animationRes)
                .setCancelable(false)
                .setPositiveButton("OK", onClickListener)
                .build();

        mDialog.show();
    }

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .setTitle(message)
                    .setAnimation(R.raw.loading)
                    .build();
            mProgressDialog.getAnimationView().setPadding(64, 64, 64, 64);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

}

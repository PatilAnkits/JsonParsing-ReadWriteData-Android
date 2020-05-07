package com.shritechsoft.stud;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class Login extends AppCompatActivity {
    private static final String LOGIN_URL = "http://sbm.visionitsoftware.in/userRegistation/login.php";
    EditText email;
    EditText password;
    Button login,reg;
    SessionManager sessionManager;
    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        setContentView(R.layout.activity_login);
        email =findViewById(R.id.uname);
        password=findViewById(R.id.pass);
        login = findViewById(R.id.login);
        reg = findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(email.getText().toString(), password.getText().toString());
            }
        });

    }
    private  void  login(final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.e("RESPONSE", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    String id = jsonObject.getString("id");
                    sessionManager.createSession(email,id);
                    if(success.equals("1")){
                        showDialog("Hurrah!", message, R.raw.success, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                startActivity(new Intent(Login.this,Nav.class));
                                dialogInterface.dismiss();
                            }
                        });
                    } else {
                        showDialog("Oops!", message, R.raw.error, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // startActivity(new Intent(login.this,start.class));
                                dialogInterface.dismiss();
                            }
                        });
                    }
                }catch(JSONException e){
                    e.printStackTrace();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String , String> params =new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        showProgressDialog("Authenticating");
    }


    private void showDialog(String title, String message, @RawRes int animationRes, AbstractDialog.OnClickListener onClickListener) {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setAnimation(animationRes)
                .setCancelable(false)
                .setPositiveButton("OK",onClickListener)
                .build();

        mDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mDialog.show();
    }
    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .setTitle(message)
                    .setAnimation(R.raw.loading)
                    .build();
        }
        mProgressDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}

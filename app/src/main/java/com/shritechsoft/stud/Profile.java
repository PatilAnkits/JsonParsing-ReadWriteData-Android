package com.shritechsoft.stud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shreyaspatil.MaterialDialog.AbstractDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    SessionManager sessionManager;
    CircleImageView profilePhoto;
    EditText name, email, mobile_no, street, city, state, country, postalcode, firstname, lastname;
    private Menu action;
    Button save, edit;
    Uri strPhoto;
    private Bitmap bitmap;
    String getId;
    private static String URL_READ = "http://sbm.visionitsoftware.in/userRegistation/read.php";
    private static String URL_EDIT = "http://sbm.visionitsoftware.in/userRegistation/edit.php";
    private static String URL_UPLOAD = "http://sbm.visionitsoftware.in/userRegistation/uploadimage.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        mobile_no = findViewById(R.id.mobile_no);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        save = findViewById(R.id.save);
        country = findViewById(R.id.country);
        postalcode = findViewById(R.id.postalCode);
        profilePhoto = findViewById(R.id.profilephoto);
        edit = findViewById(R.id.edit);

        postalcode.setFocusableInTouchMode(false);
        lastname.setFocusableInTouchMode(false);
        firstname.setFocusableInTouchMode(false);
        country.setFocusableInTouchMode(false);
        email.setFocusableInTouchMode(false);
        mobile_no.setFocusableInTouchMode(false);
        street.setFocusableInTouchMode(false);
        city.setFocusableInTouchMode(false);
        state.setFocusableInTouchMode(false);

        country.setFocusable(false);
        postalcode.setFocusable(false);
        email.setFocusable(false);
        street.setFocusable(false);
        city.setFocusable(false);
        mobile_no.setFocusable(false);
        lastname.setFocusable(false);
        firstname.setFocusable(false);
        state.setFocusable(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email.setFocusableInTouchMode(true);
                mobile_no.setFocusableInTouchMode(true);
                street.setFocusableInTouchMode(true);
                city.setFocusableInTouchMode(true);
                state.setFocusableInTouchMode(true);
                country.setFocusableInTouchMode(true);
                postalcode.setFocusableInTouchMode(true);
                firstname.setFocusableInTouchMode(true);
                lastname.setFocusableInTouchMode(true);

                email.setFocusable(true);
                street.setFocusable(true);
                city.setFocusable(true);
                firstname.setFocusable(true);
                lastname.setFocusable(true);
                mobile_no.setFocusable(true);
                state.setFocusable(true);
                country.setFocusable(true);
                postalcode.setFocusable(true);
            }
        });
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email.setFocusableInTouchMode(true);
                getUserDetails();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
                saveEditDeatils();

                firstname.setFocusableInTouchMode(false);
                lastname.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                mobile_no.setFocusableInTouchMode(false);
                street.setFocusableInTouchMode(false);
                city.setFocusableInTouchMode(false);
                state.setFocusableInTouchMode(false);
                country.setFocusableInTouchMode(false);
                postalcode.setFocusableInTouchMode(false);
                firstname.setFocusableInTouchMode(false);
                lastname.setFocusableInTouchMode(false);

                firstname.setFocusable(false);
                lastname.setFocusable(false);
                email.setFocusable(false);
                street.setFocusable(false);
                city.setFocusable(false);
                mobile_no.setFocusable(false);
                state.setFocusable(false);
                country.setFocusable(false);
                postalcode.setFocusable(false);
                Toast.makeText(Profile.this, "Details Updated Successfully ...1", Toast.LENGTH_SHORT).show();
            }
        });
        HashMap<String, String> user = sessionManager.getUserDetails();
        getId = user.get(sessionManager.ID);
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
    }

    private void getUserDetails() {
        // Progess Bar
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");
                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);


                                    String strEmail = object.getString("email").trim();
                                    String strMobile_no = object.getString("mobile_no").trim();
                                    String strStreet = object.getString("street").trim();
                                    String strCity = object.getString("city").trim();
                                    String strCounry = object.getString("country").trim();
                                    String strPostalCode = object.getString("postalcode").trim();
                                    String strState = object.getString("state").trim();
                                    String strFirstname = object.getString("firstname").trim();
                                    String strLastname = object.getString("lastname").trim();
                                    strPhoto = Uri.parse(object.getString("photo").trim());


                                    firstname.setText(strFirstname);
                                    lastname.setText(strLastname);
                                    email.setText(strEmail);
                                    mobile_no.setText(strMobile_no);
                                    street.setText(strStreet);
                                    city.setText(strCity);
                                    postalcode.setText(strPostalCode);
                                    country.setText(strCounry);
                                    state.setText(strState);
                                    profilePhoto.setImageURI(strPhoto);
                                    Picasso.with(Profile.this).load(strPhoto).into(profilePhoto);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Profile.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Profile.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        action = menu;
        action.findItem(R.id.action_edit).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                name.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);

                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.action_edit).setVisible(false);
                action.findItem(R.id.action_save).setVisible(false);

            case R.id.action_save:
                saveEditDeatils();
                action.findItem(R.id.action_edit).setVisible(false);
                action.findItem(R.id.action_save).setVisible(false);

                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                mobile_no.setFocusableInTouchMode(false);
                street.setFocusableInTouchMode(false);
                city.setFocusableInTouchMode(false);
                state.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);
                street.setFocusable(false);
                city.setFocusable(false);
                mobile_no.setFocusable(false);
                state.setFocusable(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveEditDeatils() {

        final String email = this.email.getText().toString().trim();
        final String mobile_no = this.mobile_no.getText().toString().trim();
        final String street = this.street.getText().toString().trim();
        final String city = this.city.getText().toString().trim();
        final String country = this.country.getText().toString().trim();
        final String postalcode = this.postalcode.getText().toString().trim();
        final String firstname = this.firstname.getText().toString().trim();
        final String lastname = this.lastname.getText().toString().trim();
        final String state = this.state.getText().toString().trim();
        final String id = getId;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(Profile.this, "Success..!", Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(email, id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Toast.makeText(Profile.this,"Error..!"+e.toString(),Toast.LENGTH_SHORT).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(Profile.this,"Error..!"+error.toString(),Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("street", street);
                params.put("city", city);
                params.put("country", country);
                params.put("postalcode", postalcode);
                params.put("state", state);
                params.put("mobile_no", mobile_no);
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadPicture(getId, getStringImage(bitmap));
        }
    }

    private void UploadPicture(final String id, final String photo) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....!");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(Profile.this, "Profile Updated Successfullly", Toast.LENGTH_SHORT).show();
                                getUserDetails();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Profile.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
                            getUserDetails();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                        getUserDetails();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("photo", photo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);


        return encodedImage;
    }
}

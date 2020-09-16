package kingapawlowska.com.repairyourpc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {

    protected EditText login;
    private EditText password;
    private EditText email;
    protected String enteredLogin;
    private EditText firstName;
    private EditText lastName;
    private EditText streetName;
    private EditText streetNumber;
    private EditText zipCode;
    private EditText city;
    private EditText phoneNumber;

    private final String serverUrl = "http://localhost/repair_your_pc/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = (EditText) findViewById(R.id.etRegLogin);
        password = (EditText) findViewById(R.id.etRegPassword);
        email = (EditText) findViewById(R.id.etRegEmail);
        firstName = (EditText) findViewById(R.id.etRegFirstName);
        lastName = (EditText) findViewById(R.id.etRegLastName);
        streetName = (EditText) findViewById(R.id.etRegStreetName);
        streetNumber = (EditText) findViewById(R.id.etRegStreetNumber);
        zipCode = (EditText) findViewById(R.id.etRegZipCode);
        city = (EditText) findViewById(R.id.etRegCity);
        phoneNumber = (EditText) findViewById(R.id.etRegPhone);

        Button signUpButton = (Button) findViewById(R.id.btnRegRegister);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredLogin = login.getText().toString();
                String enteredPassword = password.getText().toString();
                String enteredEmail = email.getText().toString();
                String enteredFirstName = firstName.getText().toString();
                String enteredLastName = lastName.getText().toString();
                String enteredStreetName = streetName.getText().toString();
                String enteredStreetNumber = streetNumber.getText().toString();
                String enteredZipCode = zipCode.getText().toString();
                String enteredCity = city.getText().toString();
                String enteredPhoneNumber = phoneNumber.getText().toString();

                if (enteredLogin.equals("") || enteredPassword.equals("") || enteredEmail.equals("") ||
                        enteredFirstName.equals("") || enteredLastName.equals("") ||
                        enteredStreetName.equals("") || enteredStreetNumber.equals("") ||
                        enteredZipCode.equals("") || enteredCity.equals("") || enteredPhoneNumber.equals("")) {

                    login.setError("field is required!");
                    password.setError("field is required!");
                    email.setError("field is required!");
                    firstName.setError("field is required!");
                    lastName.setError("field is required!");
                    streetName.setError("field is required!");
                    streetNumber.setError("field is required!");
                    zipCode.setError("field is required!");
                    city.setError("field is required!");
                    phoneNumber.setError("field is required!");


                    Toast.makeText(RegisterActivity.this, "Fields must be filled", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredLogin.length() <= 1 || enteredPassword.length() <= 1) {
                    Toast.makeText(RegisterActivity.this, "Login or password length must be greater than one", Toast.LENGTH_LONG).show();
                    return;
                }
                // request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, enteredLogin, enteredPassword, enteredEmail, enteredFirstName, enteredLastName,
                        enteredStreetName, enteredStreetNumber, enteredZipCode, enteredCity, enteredPhoneNumber);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("login", params[1]));
                nameValuePairs.add(new BasicNameValuePair("password", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs.add(new BasicNameValuePair("firstName", params[4]));
                nameValuePairs.add(new BasicNameValuePair("lastName", params[5]));
                nameValuePairs.add(new BasicNameValuePair("streetName", params[6]));
                nameValuePairs.add(new BasicNameValuePair("streetNumber", params[7]));
                nameValuePairs.add(new BasicNameValuePair("zipCode", params[8]));
                nameValuePairs.add(new BasicNameValuePair("city", params[9]));
                nameValuePairs.add(new BasicNameValuePair("phoneNumber", params[10]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                System.out.println("Returned Json object " + jsonResult.toString());

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if (result.equals("") || result == null) {
                Toast.makeText(RegisterActivity.this, "Server connection failed", Toast.LENGTH_LONG).show();
                return;
            }
            int jsonResult = returnParsedJsonObject(result);
            if (jsonResult == 0) {
                Toast.makeText(RegisterActivity.this, "Invalid login or password or email", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("LOGIN", enteredLogin);
                intent.putExtra("MESSAGE", "You have been successfully Registered");
                startActivity(intent);
            }
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }

    private int returnParsedJsonObject(String result) {

        JSONObject resultObject = null;
        int returnedResult = 0;
        try {
            resultObject = new JSONObject(result);
            returnedResult = resultObject.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResult;
    }
}

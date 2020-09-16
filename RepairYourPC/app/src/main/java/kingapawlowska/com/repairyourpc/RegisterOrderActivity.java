package kingapawlowska.com.repairyourpc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class RegisterOrderActivity extends ActionBarActivity {

    private final String serverUrl = "http://localhost/repair_your_pc/include/order.php";

    private String enteredLogin;
    private String login;
    private String repairIdentifier;

    private EditText manufacturer;
    private Spinner kindOfHardware;
    private EditText model;
    private Spinner service;
    private Spinner operatingSystem;
    private EditText description;
    private EditText link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        String loggedUser = intentBundle.getString("LOGIN");
        enteredLogin = loggedUser;
        login = enteredLogin;

        manufacturer = (EditText) findViewById(R.id.etRegOrdManufacturer);
        kindOfHardware = (Spinner) findViewById(R.id.spinRegOrdKindOfHardware);
        model = (EditText) findViewById(R.id.etRegOrdModel);
        service = (Spinner) findViewById(R.id.spinRegOrdService);
        operatingSystem = (Spinner) findViewById(R.id.spinRegOrdOS);
        description = (EditText) findViewById(R.id.etRegOrdDescription);
        link = (EditText) findViewById(R.id.etRegOrdLink);

        Button registerOrderButton = (Button) findViewById(R.id.btnRegOrdRegisterOrder);
        registerOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredManufacturer = manufacturer.getText().toString();
                String enteredKindOfHardware = kindOfHardware.getSelectedItem().toString();
                String enteredModel = model.getText().toString();
                String enteredService = service.getSelectedItem().toString();
                String enteredOperatingSystem = operatingSystem.getSelectedItem().toString();
                String enteredDescription = description.getText().toString();
                String enteredLink = link.getText().toString();

                repairIdentifier = repairIdentifierBuilder(login);

                if (enteredLogin.equals("") || repairIdentifier.equals("") || enteredDescription.equals("") ||
                        enteredKindOfHardware.equals("") || enteredService.equals("") || enteredOperatingSystem.equals("")) {

                    //repairID.setError( "RepairID is required!" );
                    //password.setError( "RepairID is required!" );
                    //email.setError( "RepairID is required!" );

                    Toast.makeText(RegisterOrderActivity.this, "Fields must be filled", Toast.LENGTH_LONG).show();
                    return;
                }

                // request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, login, repairIdentifier, enteredManufacturer, enteredKindOfHardware, enteredModel, enteredService, enteredOperatingSystem, enteredDescription, enteredLink);

            }
        });

        Button backButton = (Button) findViewById(R.id.btnRegOrdBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });
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
                nameValuePairs.add(new BasicNameValuePair("repairIdentifier", params[2]));
                nameValuePairs.add(new BasicNameValuePair("manufacturer", params[3]));
                nameValuePairs.add(new BasicNameValuePair("kindOfHardware", params[4]));
                nameValuePairs.add(new BasicNameValuePair("model", params[5]));
                nameValuePairs.add(new BasicNameValuePair("service", params[6]));
                nameValuePairs.add(new BasicNameValuePair("operatingSystem", params[7]));
                nameValuePairs.add(new BasicNameValuePair("description", params[8]));
                nameValuePairs.add(new BasicNameValuePair("link", params[9]));
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
                Toast.makeText(RegisterOrderActivity.this, "Server connection failed", Toast.LENGTH_LONG).show();
                return;
            }
            int jsonResult = returnParsedJsonObject(result);
            if (jsonResult == 0) {
                Toast.makeText(RegisterOrderActivity.this, "Failed to register order. Check text in fields", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Intent intent = new Intent(RegisterOrderActivity.this, LoginActivity.class);
                intent.putExtra("LOGIN", enteredLogin);
                intent.putExtra("MESSAGE", "You have been successfully added new order");
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

    private String repairIdentifierBuilder(String login) {

        String repairIdentifierString = "";

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Poland"));

        int year = c.get(Calendar.YEAR);
        String yearString = year + "";
        StringBuilder sb = new StringBuilder(yearString);
        //sb.deleteCharAt(0);
        sb.delete(0, 2);
        String yearCut = sb.toString();

        int month = c.get(Calendar.MONTH) + 1;
        String monthString = month + "";
        String monthZero = "" + monthString;
        if (month < 10) {
            StringBuilder sbm = new StringBuilder(monthString);
            sbm.insert(0, "0");
            monthZero = sbm.toString();
        }

        int day = c.get(Calendar.DAY_OF_MONTH);
        String dayString = day + "";
        String dayZero = "" + dayString;
        if (day < 10) {
            StringBuilder sbd = new StringBuilder(dayString);
            sbd.insert(0, "0");
            dayZero = sbd.toString();
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        String hourString = hour + "";
        String hourZero = "" + hourString;
        if (hour < 10) {
            StringBuilder sbh = new StringBuilder(hourString);
            sbh.insert(0, "0");
            hourZero = sbh.toString();
        }

        int minute = c.get(Calendar.MINUTE);
        String minuteString = minute + "";
        String minuteZero = "" + minuteString;
        if (minute < 10) {
            StringBuilder sbmin = new StringBuilder(minuteString);
            sbmin.insert(0, "0");
            minuteZero = sbmin.toString();
        }

        int second = c.get(Calendar.SECOND);
        String secondString = second + "";
        String secondZero = "" + secondString;
        if (second < 10) {
            StringBuilder sbs = new StringBuilder(secondString);
            sbs.insert(0, "0");
            secondZero = sbs.toString();
        }

        repairIdentifierString = login + "" + yearCut + "" + monthZero + "" + dayZero + "" + hourZero + "" + minuteZero + "" + secondZero;

        return repairIdentifierString;
    }
}

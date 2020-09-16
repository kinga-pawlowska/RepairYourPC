package kingapawlowska.com.repairyourpcforemployee;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends ActionBarActivity implements AsyncResponse {

    final String LOG = "DetailActivity";

    TextView tvDetRepairIdentifier, tvDetRepairStatus, tvDetEstimatedCost, tvDetPaid;
    TextView tvDetManufacturer, tvDetKindOfHardware, tvDetModel, tvDetService, tvDetOperatingSystem;
    TextView tvDetDescription, tvDetLink;

    private final String serverUrlRemoveOrder = "http://127.0.0.1/repair_your_pc/include/removeOrder.php";
    private final String serverUrlReadClientInfo = "http://127.0.0.1/repair_your_pc/include/readClientInfo.php";

    private Order order;
    private ArrayList<User> userList;
    //private List<User> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order = (Order) getIntent().getSerializableExtra("order");

        tvDetRepairIdentifier = (TextView) findViewById(R.id.tvDetRepairIdentifier);
        tvDetRepairStatus = (TextView) findViewById(R.id.tvDetRepairStatus);
        tvDetEstimatedCost = (TextView) findViewById(R.id.tvDetEstimatedCost);
        tvDetPaid = (TextView) findViewById(R.id.tvDetPaid);
        tvDetManufacturer = (TextView) findViewById(R.id.tvDetManufacturer);
        tvDetKindOfHardware = (TextView) findViewById(R.id.tvDetKindOfHardware);
        tvDetModel = (TextView) findViewById(R.id.tvDetModel);
        tvDetService = (TextView) findViewById(R.id.tvDetService);
        tvDetOperatingSystem = (TextView) findViewById(R.id.tvDetOperatingSystem);
        tvDetDescription = (TextView) findViewById(R.id.tvDetDescription);
        tvDetLink = (TextView) findViewById(R.id.tvDetLink);

        if (order != null) {
            tvDetRepairIdentifier.setText(order.repairIdentifier);
            tvDetRepairStatus.setText(order.repairStatus);
            tvDetEstimatedCost.setText(order.estimatedCost);
            tvDetPaid.setText(order.paid);

            tvDetManufacturer.setText(order.manufacturer);
            tvDetKindOfHardware.setText(order.kindOfHardware);
            tvDetModel.setText(order.model);
            tvDetService.setText(order.service);
            tvDetOperatingSystem.setText(order.operatingSystem);
            tvDetDescription.setText("" + order.description);
            tvDetLink.setText(order.link);
        }

        Button clientInfoButton = (Button) findViewById(R.id.btnDetClientInfo);
        clientInfoButton.setEnabled(true);


        clientInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncDataClassReadClientInfo asyncRequestObjectRead = new DetailActivity.AsyncDataClassReadClientInfo();
                asyncRequestObjectRead.execute(serverUrlReadClientInfo, order.login);

                /*
                Intent intent = new Intent(DetailActivity.this, ClientInfoActivity.class);
                intent.putExtra("LOGIN", order.login);
                intent.putExtra("PHONENUMBER", userList.get(0).firstName);
                startActivity(intent);
                finish();
                */


            }
        });


        Button backButton = (Button) findViewById(R.id.btnDetBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button removeOrderButton = (Button) findViewById(R.id.btnDetRemoveOrder);
        removeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncDataClassRemoveOrder asyncRequestObject = new AsyncDataClassRemoveOrder();
                asyncRequestObject.execute(serverUrlRemoveOrder, order.repairIdentifier, order.login);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("ifRemoved", "yes");
                returnIntent.putExtra("ifRemovedData", order);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

                //Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                //startActivity(intent);
                //onRestart();
                //finish();
            }
        });

        Button changeButton = (Button) findViewById(R.id.btnDetChange);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(DetailActivity.this, ChangeActivity.class);
                in.putExtra("order", (Serializable) order);
                startActivity(in);
            }
        });
    }

    @Override
    public void processFinish(String s) {

    }

    private class AsyncDataClassRemoveOrder extends AsyncTask<String, Void, String> {

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
                nameValuePairs.add(new BasicNameValuePair("repairIdentifier", params[1]));
                nameValuePairs.add(new BasicNameValuePair("login", params[2]));
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
            int jsonResult = returnParsedJsonObject(result);
            if (jsonResult == 0) {
                Toast.makeText(DetailActivity.this, "Failed to remove the order", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Toast.makeText(DetailActivity.this, "Order has been removed", Toast.LENGTH_LONG).show();

                return;
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

        private int returnParsedJsonObject(String result) {

            JSONObject resultObject = null;
            int returnedResult = 0;
            try {
                resultObject = new JSONObject(result);
                returnedResult = resultObject.getInt("removed");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
    }

    private class AsyncDataClassReadClientInfo extends AsyncTask<String, Void, String> {

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

            userList = new JsonConverter<User>().toArrayList(result, User.class);
            //System.out.println(userList.get(0).firstName);

            Intent intent = new Intent(DetailActivity.this, ClientInfoActivity.class);

            intent.putExtra("login", userList.get(0).login);
            intent.putExtra("email", userList.get(0).email);
            intent.putExtra("firstName", userList.get(0).firstName);
            intent.putExtra("lastName", userList.get(0).lastName);
            intent.putExtra("streetName", userList.get(0).streetName);
            intent.putExtra("streetNumber", userList.get(0).streetNumber);
            intent.putExtra("zipCode", userList.get(0).zipCode);
            intent.putExtra("city", userList.get(0).city);
            intent.putExtra("phoneNumber", userList.get(0).phoneNumber);

            startActivity(intent);
            finish();

            //TUTAJ

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

}

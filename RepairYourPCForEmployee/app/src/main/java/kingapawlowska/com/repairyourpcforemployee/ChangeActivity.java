package kingapawlowska.com.repairyourpcforemployee;

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
import java.util.List;

public class ChangeActivity extends ActionBarActivity {

    final String LOG = "ChangeActivity";

    private final String serverUrlUpdateOrder = "http://127.0.0.1/repair_your_pc/include/updateOrder.php";

    Spinner spinChaRepairStatus, spinChaPaid;
    EditText etChaEstimatedCost;

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order = (Order) getIntent().getSerializableExtra("order");

        spinChaRepairStatus = (Spinner) findViewById(R.id.spinChaRepairStatus);
        etChaEstimatedCost = (EditText) findViewById(R.id.etChaEstimatedCost);
        spinChaPaid = (Spinner) findViewById(R.id.spinChaPaid);

        Button backButton = (Button) findViewById(R.id.btnChaBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button updateButton = (Button) findViewById(R.id.btnChaUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changedRepairStatus = spinChaRepairStatus.getSelectedItem().toString();
                String changedEstimatedCost = etChaEstimatedCost.getText().toString();
                String changedPaid = spinChaPaid.getSelectedItem().toString();

                AsyncDataClassUpdateOrder asyncRequestObject = new AsyncDataClassUpdateOrder();
                asyncRequestObject.execute(serverUrlUpdateOrder, order.repairIdentifier, changedRepairStatus, changedEstimatedCost, changedPaid);

                Intent i = new Intent(ChangeActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, RESULT_OK);
            }
        });
    }

    private class AsyncDataClassUpdateOrder extends AsyncTask<String, Void, String> {

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
                nameValuePairs.add(new BasicNameValuePair("repairStatus", params[2]));
                nameValuePairs.add(new BasicNameValuePair("estimatedCost", params[3]));
                nameValuePairs.add(new BasicNameValuePair("paid", params[4]));
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
                Toast.makeText(ChangeActivity.this, "Failed to update order", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Toast.makeText(ChangeActivity.this, "Order has been updated", Toast.LENGTH_LONG).show();

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
                returnedResult = resultObject.getInt("updated");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
    }

}

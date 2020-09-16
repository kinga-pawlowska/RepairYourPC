package kingapawlowska.com.repairyourpc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends ActionBarActivity {

    final String LOG = "DetailActivity";

    TextView tvDetRepairIdentifier, tvDetRepairStatus, tvDetEstimatedCost, tvDetPaid;
    TextView tvDetManufacturer, tvDetKindOfHardware, tvDetModel, tvDetService, tvDetOperatingSystem;
    TextView tvDetDescription, tvDetLink;

    private final String serverUrlRemoveOrder = "http://localhost/repair_your_pc/include/removeOrder.php";
    private final String serverUrlPayForOrder = "http://localhost/repair_your_pc/include/payForOrder.php";

    private Order order;

    // ------------------- FOR PAYMENT: ------------------------------------------------------------
    TextView m_response;
    PayPalConfiguration m_configuration;
    // the id is the link to the paypal account we have to create an app and get its id
    String m_paypalClientId = "Ad2_MHP2lR3ggPwJR9IDLnPCaQWDCz8tPDrEVhtxbgDJoM-Ga_wYUj2AACsAiZYIk45MBgR6CNsYovrZ";
    Intent m_service;
    int m_paypalRequestCode = 999; // or any number
    // ---------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
            tvDetEstimatedCost.setText(order.estimatedCost + " $");
            tvDetPaid.setText(order.paid);

            tvDetManufacturer.setText(order.manufacturer);
            tvDetKindOfHardware.setText(order.kindOfHardware);
            tvDetModel.setText(order.model);
            tvDetService.setText(order.service);
            tvDetOperatingSystem.setText(order.operatingSystem);
            tvDetDescription.setText("" + order.description);
            tvDetLink.setText(order.link);
        }

        // =========================================================================================
        Button btnDetPay = (Button) findViewById(R.id.btnDetPay);
        if (order.paid.equals("YES")) {
            btnDetPay.setEnabled(false);
            TextView tvDetResponse = (TextView) findViewById(R.id.tvDetResponse);
            tvDetResponse.setText("You have already paid for this repair");
        } else {
            if (order.estimatedCost.equals("PENDING")) {
                btnDetPay.setEnabled(false);
                TextView tvDetResponse = (TextView) findViewById(R.id.tvDetResponse);
                tvDetResponse.setText("The order is in the process of verification...");
            } else {
                btnDetPay.setEnabled(true);
                TextView tvDetResponse = (TextView) findViewById(R.id.tvDetResponse);
                tvDetResponse.setText("Press the button above to make a payment");
            }
        }

        btnDetPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int estimatedCostInt = Integer.parseInt(order.estimatedCost);
                pay(view, estimatedCostInt);
            }
        });
        m_response = (TextView) findViewById(R.id.tvDetResponse);

        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // sandbox for test, production for real
                .clientId(m_paypalClientId);

        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration); // configuration above
        startService(m_service); // paypal service, listening to calls to paypal app

        // =========================================================================================

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

    }

    void pay(View view, int estimatedCost) {
        /*PayPalPayment payment = new PayPalPayment(new BigDecimal(10), "USD", "Test payment with Paypal",
                PayPalPayment.PAYMENT_INTENT_SALE); */
        PayPalPayment payment = new PayPalPayment(new BigDecimal(estimatedCost), "USD", "Make payment with Paypal",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, m_paypalRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == m_paypalRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                // we have to confirm that the payment worked to avoid fraud
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirmation != null) {
                    String state = confirmation.getProofOfPayment().getState();

                    if (state.equals("approved")) // if the payment worked, the state equals approved
                    {
                        m_response.setText("payment approved");
                        Button btnDetPay = (Button) findViewById(R.id.btnDetPay);
                        btnDetPay.setEnabled(false);
                        tvDetPaid.setText("YES");

                        AsyncDataClassPaidForOrder asyncRequestObject = new AsyncDataClassPaidForOrder();
                        asyncRequestObject.execute(serverUrlPayForOrder, order.repairIdentifier);
                    } else
                        m_response.setText("error in the payment");
                } else
                    m_response.setText("confirmation is null");
            }
        }
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

    private class AsyncDataClassPaidForOrder extends AsyncTask<String, Void, String> {

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
                Toast.makeText(DetailActivity.this, "Failed to accept this payment. Check your connection.", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Toast.makeText(DetailActivity.this, "Order has been accepted", Toast.LENGTH_LONG).show();

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
                returnedResult = resultObject.getInt("paid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
    }

}



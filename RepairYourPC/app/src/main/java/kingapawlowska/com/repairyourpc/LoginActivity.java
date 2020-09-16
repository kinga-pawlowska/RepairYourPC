package kingapawlowska.com.repairyourpc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
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

import static kingapawlowska.com.repairyourpc.R.id.btnLogDeleteAccount;
import static kingapawlowska.com.repairyourpc.R.id.btnLogLogout;
import static kingapawlowska.com.repairyourpc.R.id.btnLogPlaceAnOrder;
import static kingapawlowska.com.repairyourpc.R.id.btnLogRefresh;

public class LoginActivity extends ActionBarActivity implements AsyncResponse {

    final String LOG = "LoginActivity";

    private ArrayList<Order> orderList;
    private ListView lvOrder;
    private FunDapter<Order> adapter;

    private final String serverUrlCancel = "http://localhost/repair_your_pc/include/cancelPHP.php";
    private final String serverUrlReadOrders = "http://localhost/repair_your_pc/include/readOrders.php";

    //protected String enteredLogin;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        String loggedUser = intentBundle.getString("LOGIN");
        final String enteredLogin = intentBundle.getString("LOGIN");

        final String LoginText = loggedUser;
        loggedUser = capitalizeFirstCharacter(loggedUser);
        String message = intentBundle.getString("MESSAGE");

        TextView loginFirstName = (TextView) findViewById(R.id.tvLogFirstName);
        TextView successMessage = (TextView) findViewById(R.id.tvLogMessage);
        loginFirstName.setText(loggedUser);
        successMessage.setText(message);


        // wyświetlanie: ***************************************************************************
        //PostResponseAsyncTask taskRead = new PostResponseAsyncTask(LoginActivity.this, this);
        //taskRead.execute(serverUrlReadOrders, enteredLogin);

        AsyncDataClassRead asyncRequestObjectRead = new LoginActivity.AsyncDataClassRead();
        asyncRequestObjectRead.execute(serverUrlReadOrders, LoginText);

        lvOrder = (ListView) findViewById(R.id.lvLogOrders);
        registerForContextMenu(lvOrder);

        //******************************************************************************************

        Button cancelButton = (Button) findViewById(btnLogDeleteAccount);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView testRepairID = (TextView)findViewById(R.id.testRepairID);
                //testRepairID.setText(repairIDText);

                AsyncDataClass asyncRequestObject = new LoginActivity.AsyncDataClass();
                asyncRequestObject.execute(serverUrlCancel, LoginText);

                //Intent returnIntent = new Intent();
                //setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
        });

        Button logoutButton = (Button) findViewById(btnLogLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button placeAnOrderButton = (Button) findViewById(btnLogPlaceAnOrder);
        placeAnOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterOrderActivity.class);
                intent.putExtra("LOGIN", enteredLogin);
                startActivity(intent);
                finish();
            }
        });

        Button refreshButton = (Button) findViewById(btnLogRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void processFinish(String s) {
        orderList = new JsonConverter<Order>().toArrayList(s, Order.class);

        BindDictionary<Order> dict = new BindDictionary<Order>();
        dict.addStringField(R.id.tvListRepairIdentifier, new StringExtractor<Order>() {
            @Override
            public String getStringValue(Order order, int position) {
                return order.repairIdentifier;
            }
        });

        dict.addStringField(R.id.tvListCreatedAt, new StringExtractor<Order>() {
            @Override
            public String getStringValue(Order order, int position) {
                return order.created_at;
            }
        });

        dict.addStringField(R.id.tvListUpdatedAt, new StringExtractor<Order>() {
            @Override
            public String getStringValue(Order order, int position) {
                return order.updated_at;
            }
        });
    }

    private class AsyncDataClassRead extends AsyncTask<String, Void, String> implements AdapterView.OnItemClickListener {

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

            orderList = new JsonConverter<Order>().toArrayList(result, Order.class);

            BindDictionary<Order> dict = new BindDictionary<Order>();
            dict.addStringField(R.id.tvListRepairIdentifier, new StringExtractor<Order>() {
                @Override
                public String getStringValue(Order order, int position) {
                    return order.repairIdentifier;
                }
            });

            dict.addStringField(R.id.tvListCreatedAt, new StringExtractor<Order>() {
                @Override
                public String getStringValue(Order order, int position) {
                    return order.created_at;
                }
            });

            dict.addStringField(R.id.tvListUpdatedAt, new StringExtractor<Order>() {
                @Override
                public String getStringValue(Order order, int position) {
                    return order.updated_at;
                }
            });

            /*

            dict.addStringField(R.id.tvListDescription, new StringExtractor<Order>() {
                @Override
                public String getStringValue(Order order, int position) {
                    return order.description;
                }
            });
            */
            // TYLKO SAME ELEMENTY LISTY, nie klasy i parsowania !!!

            adapter = new FunDapter<>(LoginActivity.this, orderList, R.layout.layout_list, dict);
            lvOrder.setAdapter(adapter);
            lvOrder.setOnItemClickListener(this);

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

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Order selectedOrder = orderList.get(position);
            if (selectedOrder.repairIdentifier != null) {
                Intent in = new Intent(LoginActivity.this, DetailActivity.class);
                in.putExtra("order", (Serializable) selectedOrder);
                startActivity(in);
            }
        }
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
                Toast.makeText(LoginActivity.this, "Failed to remove the account", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonResult == 1) {
                Toast.makeText(LoginActivity.this, "Account has been removed", Toast.LENGTH_LONG).show();
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
                returnedResult = resultObject.getInt("cancelled");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private String capitalizeFirstCharacter(String textInput) {
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }
}

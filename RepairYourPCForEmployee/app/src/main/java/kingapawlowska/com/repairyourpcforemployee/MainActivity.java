package kingapawlowska.com.repairyourpcforemployee;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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

import static kingapawlowska.com.repairyourpcforemployee.R.id.btnMainRefresh;

public class MainActivity extends ActionBarActivity implements AsyncResponse {

    final String LOG = "MainActivity";

    private ArrayList<Order> orderList;
    private ListView lvOrder;
    private FunDapter<Order> adapter;

    private final String serverUrlReadAllOrders = "http://127.0.0.1/repair_your_pc/include/readAllOrders.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncDataClassRead asyncRequestObjectRead = new MainActivity.AsyncDataClassRead();
        asyncRequestObjectRead.execute(serverUrlReadAllOrders);

        lvOrder = (ListView) findViewById(R.id.lvMainOrders);
        registerForContextMenu(lvOrder);

        Button refreshButton = (Button) findViewById(btnMainRefresh);
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

            adapter = new FunDapter<>(MainActivity.this, orderList, R.layout.layout_list, dict);
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
                Intent in = new Intent(MainActivity.this, DetailActivity.class);
                in.putExtra("order", (Serializable) selectedOrder);
                startActivity(in);
            }
        }
    }


}

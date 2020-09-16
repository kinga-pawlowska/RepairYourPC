package kingapawlowska.com.repairyourpcforemployee;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClientInfoActivity extends ActionBarActivity {

    private Order order;
    private User user;

    TextView tvCliLogin, tvCliEmail, tvCliFirstName, tvCliLastName, tvCliStreetName,
            tvCliStreetNumber, tvCliZipCode, tvCliCity, tvCliPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvCliLogin = (TextView) findViewById(R.id.tvCliLogin);
        tvCliEmail = (TextView) findViewById(R.id.tvCliEmail);
        tvCliFirstName = (TextView) findViewById(R.id.tvCliFirstName);
        tvCliLastName = (TextView) findViewById(R.id.tvCliLastName);
        tvCliStreetName = (TextView) findViewById(R.id.tvCliStreetName);
        tvCliStreetNumber = (TextView) findViewById(R.id.tvCliStreetNumber);
        tvCliZipCode = (TextView) findViewById(R.id.tvCliZipCode);
        tvCliCity = (TextView) findViewById(R.id.tvCliCity);
        tvCliPhoneNumber = (TextView) findViewById(R.id.tvCliPhoneNumber);

        final Intent intent = getIntent();
        final Bundle intentBundle = intent.getExtras();
        //String phoneNumber = intentBundle.getString("phoneNumber");

        tvCliLogin.setText(intentBundle.getString("login"));
        tvCliEmail.setText(intentBundle.getString("email"));
        tvCliFirstName.setText(intentBundle.getString("firstName"));
        tvCliLastName.setText(intentBundle.getString("lastName"));
        tvCliStreetName.setText(intentBundle.getString("streetName"));
        tvCliStreetNumber.setText(intentBundle.getString("streetNumber"));
        tvCliZipCode.setText(intentBundle.getString("zipCode"));
        tvCliCity.setText(intentBundle.getString("city"));
        tvCliPhoneNumber.setText(intentBundle.getString("phoneNumber"));

        Button backButton = (Button) findViewById(R.id.btnCliBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button callToButton = (Button) findViewById(R.id.btnCliCall);
        callToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + intentBundle.getString("phoneNumber")));
                startActivity(callIntent);
            }
        });

    }


}

package tz.co.fasthub.evoucher.voucher;

import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import javax.inject.Inject;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;
import timber.log.Timber;
import tz.co.fasthub.evoucher.EVoucherFragmentActivity;
import tz.co.fasthub.evoucher.Printer;
import tz.co.fasthub.evoucher.R;

public class VoucherStatusActivity extends EVoucherFragmentActivity {

    @BindView(R.id.tv_voucher_status)
    protected TextView statusTextView;

    @Inject
    protected AsyncHttpClient client;

    @Inject
    protected Printer printer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_status);

        submit();
    }


    private void submit() {
        String customerID = getIntent().getStringExtra("customerID");
        String productCode = getIntent().getStringExtra("productCode");
        String productCost = getIntent().getStringExtra("productCost");
        String businessPassword = getIntent().getStringExtra("businessPassword");

        String url = "http://evoucher.fasthub.co.tz:9092/voucher/create";
        RequestParams params = new RequestParams();
        params.put("customerID", customerID);
        params.put("productCode", productCode);
        params.put("productCost", productCost);
        params.put("businessPassword", businessPassword);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                showStatus(statusCode, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showStatus(statusCode, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                showStatus(statusCode, null);
            }
        });
    }

    private void showStatus(int statusCode, JSONObject response) {

        switch (statusCode) {
            case HttpURLConnection.HTTP_OK:
                statusTextView.setText("Success");
                try {
                    printReceipt(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Timber.w(e,"Failed to get information from JSON response");
                }
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                statusTextView.setText("Rejected");
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                statusTextView.setText("Server Error");
                break;
            default:
                statusTextView.setText("Failed to access server!");
        }

    }


    private void printReceipt(JSONObject response) throws JSONException {
        if (response == null) return;

        StringBuilder content = new StringBuilder()
                .append(String.format("%12s%8s%12s\n", "","eVoucher",""))
                .append(String.format("%8s%16s%8s\n", "","IFAKARA MOROGORO",""))
                .append(String.format("%8s%2s%22s\n", "TIME",":",response.getString("time")))
                .append(" ------------------------------ \n")
                .append(String.format("%8s%2s%22s\n", "FARMER",":",response.getString("customer")))
                .append(String.format("%8s%2s%22s\n", "SUPPLIER",":",response.getString("supplier")))
                .append(String.format("%8s%2s%22s\n", "PRODUCT",":",response.getString("product")))
                .append(String.format("%8s%2s%18s TZS\n", "COST",":",response.getString("productCost")))
                .append(String.format("%8s%2s%22s\n", "CONTRIB",":",response.getString("contribution")))
                .append(String.format("%8s%2s%22s\n", "REF",":",response.getString("reference")))
                .append(" ############################## \n");

        Timber.d(content.toString());
        printer.print(content.toString(),this);
    }

}

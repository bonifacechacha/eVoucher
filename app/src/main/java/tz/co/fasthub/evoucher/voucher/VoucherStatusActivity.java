package tz.co.fasthub.evoucher.voucher;

import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
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
        });
    }

    private void showStatus(int statusCode, JSONObject response) {

        switch (statusCode) {
            case HttpURLConnection.HTTP_OK:
                statusTextView.setText("Success");
                printReceipt(response);
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


    private void printReceipt(JSONObject response) {
        if (response == null) return;


        String customerID = getIntent().getStringExtra("customerID");
        String productCode = getIntent().getStringExtra("productCode");
        String productCost = getIntent().getStringExtra("productCost");


        StringBuilder content = new StringBuilder()
                .append(System.out.format("%12s%8s%12s\n", "","eVoucher",""))
                .append(System.out.format("%8s%16s%8s\n", "","IFAKARA MOROGORO",""))
                .append(" ------------------------------ \n")
                .append(System.out.format("%8s%2s%22s\n", "FARMER",":",customerID))
                .append(System.out.format("%8s%2s%22s\n", "SUPPLIER",":",""))
                .append(System.out.format("%8s%2s%22s\n", "PRODUCT",":",productCode))
                .append(System.out.format("%8s%2s%18s TZS\n", "COST",":",productCost))
                .append(System.out.format("%8s%2s%22s\n", "CONTRIB",":",""))
                .append(System.out.format("%8s%2s%22s\n", "REF",":",""))
                .append(" ############################## \n");

        Timber.d(content.toString());
        printer.print(content.toString(),this);
    }

}

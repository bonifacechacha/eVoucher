package tz.co.fasthub.evoucher.voucher.step;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Bus;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;
import tz.co.fasthub.evoucher.EVoucherWizardStep;
import tz.co.fasthub.evoucher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductCodeCaptureFragment extends NumberFormStepFragment {


    @ContextVariable
    private String productCode;

    @ContextVariable
    private String customerID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        textView.setText(R.string.label_enter_product_code);

        return view;
    }

    @Override
    protected void onNext() {
            productCode = editText.getText().toString();
    }

}


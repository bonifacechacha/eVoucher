package tz.co.fasthub.evoucher.voucher.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.codepond.wizardroid.persistence.ContextVariable;

import butterknife.BindView;
import tz.co.fasthub.evoucher.EVoucherWizardStep;
import tz.co.fasthub.evoucher.R;

/**
 * Created by bonifacechacha on 3/27/17.
 */

public class ProductCostCaptureFragment extends NumberFormStepFragment {

    @ContextVariable
    private String productCode;

    @ContextVariable
    private String productCost;

    @ContextVariable
    private String customerID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
       textView.setText(R.string.label_enter_product_cost);

        return view;
    }

    @Override
    protected void onNext() {
        productCost = editText.getText().toString();
    }
}
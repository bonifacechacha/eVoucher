package tz.co.fasthub.evoucher.voucher.step;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.codepond.wizardroid.persistence.ContextVariable;

import butterknife.BindView;
import tz.co.fasthub.evoucher.EVoucherWizardStep;
import tz.co.fasthub.evoucher.R;
import tz.co.fasthub.evoucher.voucher.VoucherStatusActivity;

/**
 * Created by bonifacechacha on 3/27/17.
 */

public class BusinessPasswordCaptureFragment extends NumberFormStepFragment{

    @ContextVariable
    private String productCost;

    @ContextVariable
    private String productCode;

    @ContextVariable
    private String businessPassword;

    @ContextVariable
    private String customerID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

       textView.setText(R.string.label_enter_bussiness_password);
       editText.setInputType(InputType.TYPE_CLASS_NUMBER |
               InputType.TYPE_TEXT_VARIATION_PASSWORD);

        return view;
    }

    @Override
    protected void onNext() {
        businessPassword = editText.getText().toString();

        Intent i = new Intent(getActivity(), VoucherStatusActivity.class);

        i.putExtra("customerID",customerID);
        i.putExtra("productCode",productCode);
        i.putExtra("productCost",productCost);
        i.putExtra("businessPassword",businessPassword);

        startActivity(i);
    }
}

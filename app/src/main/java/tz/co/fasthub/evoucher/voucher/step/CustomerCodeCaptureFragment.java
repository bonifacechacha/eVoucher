package tz.co.fasthub.evoucher.voucher.step;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.List;

import butterknife.BindView;
import tz.co.fasthub.evoucher.EVoucherWizardStep;
import tz.co.fasthub.evoucher.R;


/**
 * A fragment for the step of capturing customer id
 */
public class CustomerCodeCaptureFragment extends NumberFormStepFragment {

    @ContextVariable
    private String customerID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        textView.setText(R.string.label_enter_customer_id);

        return view;
    }


    @Override
    protected void onNext() {
        customerID = editText.getText().toString();
    }

}

package tz.co.fasthub.evoucher.voucher.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.codepond.wizardroid.persistence.ContextVariable;

import butterknife.BindView;
import tz.co.fasthub.evoucher.EVoucherWizardStep;
import tz.co.fasthub.evoucher.R;

/**
 * Created by bonifacechacha on 3/27/17.
 */

public class NumberFormStepFragment extends EVoucherWizardStep {

    @BindView(R.id.et_value)
    protected EditText editText;

    @BindView(R.id.tv_value)
    protected TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_form, container, false);
        bind(view);

        editText.addTextChangedListener(new TextValidator(editText) {
            @Override
            public void validate(TextView textView, String text) {
                if (text != null && !text.isEmpty()) notifyCompleted();
                else notifyIncomplete();

                textView.requestFocus();
            }
        });

        return view;
    }

}

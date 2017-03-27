package tz.co.fasthub.evoucher;

import android.os.Bundle;
import android.view.View;

import org.codepond.wizardroid.WizardStep;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bonifacechacha on 3/26/17.
 */

public abstract class EVoucherWizardStep extends WizardStep {

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void bind(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onExit(int exitCode) {
        switch (exitCode) {
            case WizardStep.EXIT_NEXT:
                onNext();
                break;
            case WizardStep.EXIT_PREVIOUS:
                onPrevious();
                break;
        }
    }

    protected void onPrevious() {
    }

    protected void onNext() {
    }
}

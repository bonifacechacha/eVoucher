package tz.co.fasthub.evoucher.voucher;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

import tz.co.fasthub.evoucher.voucher.step.BusinessPasswordCaptureFragment;
import tz.co.fasthub.evoucher.voucher.step.CustomerCodeCaptureFragment;
import tz.co.fasthub.evoucher.voucher.step.ProductCodeCaptureFragment;
import tz.co.fasthub.evoucher.voucher.step.ProductCostCaptureFragment;


/**
 * A host fragment for voucher transaction
 */
public class VoucherWizard extends BasicWizardLayout {

    @Override
    public WizardFlow onSetup() {

        return new WizardFlow
                .Builder()
                .addStep(CustomerCodeCaptureFragment.class,true)
                .addStep(ProductCodeCaptureFragment.class,true)
                .addStep(ProductCostCaptureFragment.class,true)
                .addStep(BusinessPasswordCaptureFragment.class,true)
                .create();
    }


    @Override
    public void onWizardComplete() {
        super.onWizardComplete();
        getActivity().finish();
    }
}

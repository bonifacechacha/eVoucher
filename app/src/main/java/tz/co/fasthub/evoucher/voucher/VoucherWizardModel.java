package tz.co.fasthub.evoucher.voucher;

import android.content.Context;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.NumberPage;
import com.tech.freak.wizardpager.model.PageList;
import com.tech.freak.wizardpager.model.SingleFixedChoicePage;

import tz.co.fasthub.evoucher.R;


public class VoucherWizardModel extends AbstractWizardModel {

    public VoucherWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {

        return new PageList(
                new NumberPage(this, mContext.getString(R.string.label_enter_customer_id)).setRequired(true),
                new SingleFixedChoicePage(this, mContext.getString(R.string.label_select_product))
                        .setChoices(
                                "Seed",
                                "Basal Fertilizer",
                                "Top Dressing Fertilizer" ,
                                "Herbicide",
                                "Insecticide"
                        )
                        .setRequired(true),
                new NumberPage(this, mContext.getString(R.string.label_enter_product_cost)).setRequired(true),
                new NumberPage(this, mContext.getString(R.string.label_enter_bussiness_password)).setRequired(true)
        );
    }
}

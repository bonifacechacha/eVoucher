package tz.co.fasthub.evoucher.voucher;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;
import timber.log.Timber;
import tz.co.fasthub.evoucher.EVoucherActivity;
import tz.co.fasthub.evoucher.Printer;
import tz.co.fasthub.evoucher.R;

public class MainActivity extends EVoucherActivity implements PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    @Inject
    protected AbstractWizardModel mWizardModel;

    @Inject
    protected Printer printer;

    @Inject
    protected AsyncHttpClient client;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState != null) {
//            mWizardModel.load(savedInstanceState.getBundle("model"));
//        }


        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip
                .setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
                    @Override
                    public void onPageStripSelected(int position) {
                        position = Math.min(mPagerAdapter.getCount() - 1,
                                position);
                        if (mPager.getCurrentItem() != position) {
                            mPager.setCurrentItem(position);
                        }
                    }
                });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    submit();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    private void submit() {

        final ProgressDialog submissionDialog = new ProgressDialog(this);
        submissionDialog.setIndeterminate(true);
        submissionDialog.setCancelable(false);
        submissionDialog.setCanceledOnTouchOutside(false);
        submissionDialog.setMessage("Please wait ...");
        submissionDialog.show();

        RequestParams params = new RequestParams();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            if (getApplicationContext().getString(R.string.label_enter_customer_id).equals(page.getTitle()))
                params.put("customerID", page.getData().getString(Page.SIMPLE_DATA_KEY));
            else if (getApplicationContext().getString(R.string.label_select_product).equals(page.getTitle()))
                params.put("productCode", page.getData().getString(Page.SIMPLE_DATA_KEY));
            else if (getApplicationContext().getString(R.string.label_enter_product_cost).equals(page.getTitle()))
                params.put("productCost", page.getData().getString(Page.SIMPLE_DATA_KEY));
            else if (getApplicationContext().getString(R.string.label_enter_bussiness_password).equals(page.getTitle()))
                params.put("businessPassword", page.getData().getString(Page.SIMPLE_DATA_KEY));
        }

        String url = "http://evoucher.fasthub.co.tz:9092/voucher/create";

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    printReceipt(response);
                } catch (JSONException e) {
                    String error = "Failed to print receipt content from server\n" + e.getMessage();
                    Timber.w(e, error);
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                }

                submissionDialog.dismiss();
                showResult(statusCode, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                submissionDialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showResult(statusCode, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                submissionDialog.dismiss();
                super.onFailure(statusCode, headers, responseString, throwable);
                showResult(statusCode, null);
            }
        });
    }

    private void showResult(int statusCode, JSONObject response) {

        String status = getStatus(statusCode);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(status);


        if (response != null)
            alertDialog.setMessage(response.optString("message"));

        if(statusCode == HttpURLConnection.HTTP_OK){
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    restartWizard();
                }
            });
        }else{
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getApplicationContext().getString(R.string.button_restart), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartWizard();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getApplicationContext().getString(R.string.button_close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.show();
    }

    private String getStatus(int statusCode) {
        switch (statusCode) {
            case HttpURLConnection.HTTP_OK:
                return "Success";
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                return "Rejected";
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return "Server Error";
            default:
                return "Failed to access server!";
        }
    }

    private void restartWizard() {
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }


    private void printReceipt(JSONObject response) throws JSONException {
        if (response == null) return;
        String receiptContent = createReceipt(response);
        if (receiptContent != null) printer.print(receiptContent, this);
    }

    private String createReceipt(JSONObject response) throws JSONException {
        StringBuilder content = new StringBuilder()
                .append(String.format("%12s%8s%12s\n", "", "eVoucher", ""))
                .append(String.format("%8s%16s%8s\n", "", "IFAKARA MOROGORO", ""))
                .append(String.format("%8s%2s%22s\n", "TIME", ":", response.getString("time")))
                .append(" ------------------------------ \n")
                .append(String.format("%8s%2s%22s\n", "FARMER", ":", response.getString("customer")))
                .append(String.format("%8s%2s%22s\n", "SUPPLIER", ":", response.getString("supplier")))
                .append(String.format("%8s%2s%22s\n", "PRODUCT", ":", response.getString("product")))
                .append(String.format("%8s%2s%18s TZS\n", "COST", ":", response.getString("productCost")))
                .append(String.format("%8s%2s%22s\n", "CONTRIB", ":", response.getString("contribution")))
                .append(String.format("%8s%2s%22s\n", "REF", ":", response.getString("reference")))
                .append(" ############################## \n");
        return content.toString();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
        // review
        // step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview ? R.string.review
                    : R.string.next);
            mNextButton
                    .setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
                    true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton
                .setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}

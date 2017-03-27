package tz.co.fasthub.evoucher;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.squareup.otto.Bus;
import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by bonifacechacha on 3/26/17.
 */

public class EVoucherFragmentActivity extends FragmentActivity {

    @Inject
    protected Bus eventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.bind(this);
    }
}

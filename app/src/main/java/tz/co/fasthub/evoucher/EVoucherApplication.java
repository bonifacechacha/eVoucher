package tz.co.fasthub.evoucher;

import android.app.Application;

/**
 * Created by bonifacechacha on 3/26/17.
 */

public class EVoucherApplication  extends Application {

    private static EVoucherApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AndroidModule androidModule = new AndroidModule();
        EVoucherModule eVoucherModule = new EVoucherModule();
        //Perform injection
        Injector.init(this, new Object[]{androidModule, eVoucherModule});
        // Injector.inject(this);
    }

    public static EVoucherApplication getInstance() {
        return instance;
    }
}
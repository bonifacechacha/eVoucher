package tz.co.fasthub.evoucher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tz.co.fasthub.evoucher.util.PostFromAnyThreadBus;
import tz.co.fasthub.evoucher.voucher.VoucherStatusActivity;
import tz.co.fasthub.evoucher.voucher.step.CustomerCodeCaptureFragment;
import tz.co.fasthub.evoucher.voucher.MainActivity;
import tz.co.fasthub.evoucher.voucher.step.ProductCodeCaptureFragment;
import tz.co.fasthub.evoucher.voucher.VoucherWizard;

/**
 * @author Boniface Chacha
 * @email boniface.chacha@niafikra.com,bonifacechacha@gmail.com
 * @date 6/25/15
 */
@Module(
        complete = false,
        library = true,
        injects = {

                //application
                EVoucherApplication.class,
                //activities
                MainActivity.class,
                VoucherStatusActivity.class,
                //fragments
                CustomerCodeCaptureFragment.class,
                ProductCodeCaptureFragment.class,
                VoucherWizard.class
        }
)
public class EVoucherModule {

    @Singleton
    @Provides
    Bus provideBus() {
        return new PostFromAnyThreadBus();
    }

    /**
     * GSON instance to use for all request  with date format set up for proper parsing.
     * <p/>
     * You can also configure GSON with different naming policies for your API.
     * Maybe your API is Rails API and all json values are lower case with an underscore,
     * like this "first_name" instead of "firstName".
     * You can configure GSON as such below.
     * <p/>
     * <p/>
     * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
     * .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
     */
    @Provides
    Gson provideGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

//    @Provides
//    @Singleton
//    SimpleLocation provideLocation(Context context) {
//        // construct a new instance of SimpleLocation
//        return new SimpleLocation(context);
//    }

    @Singleton
    @Provides
    AsyncHttpClient provideAsyncHttpClient() {
        return new AsyncHttpClient();
    }

    @Provides
    Printer providesPrinter() {
        return new Printer();
    }


}

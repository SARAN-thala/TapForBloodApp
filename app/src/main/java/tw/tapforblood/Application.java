package tw.tapforblood;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import tw.tapforblood.fragments.MyRequestsFragment;

/**
 * Created by rajeswari on 10/05/15.
 */
public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "VwigJVwni2KiQ0Mq3GvnmZdVC3m72F2AL4Z0vRQI", "zkPl9pXnUAMDjZc9cq5bHADpxQsbCYypwpw62QW1");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}

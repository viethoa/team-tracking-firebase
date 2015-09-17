package us.originally.teamtrack.modules.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.originally.teamtrack.controllers.LandingActivity;
import us.originally.teamtrack.controllers.LoginActivity;
import us.originally.teamtrack.controllers.MainActivity;
import us.originally.teamtrack.modules.dagger.managers.UserManager;
import us.originally.teamtrack.modules.dagger.managers.impl.UserManagerImpl;

/**
 * Created by VietHoa on 03/05/15.
 */

@Module(
        complete = false,
        library = true,
        injects = {
                UserManager.class,
                LoginActivity.class,
                LandingActivity.class,
                MainActivity.class
        }
)

public class RequestModule {

    @Provides
    @Singleton
    public UserManager providesUserManager(Context applicationContext) {
        return new UserManagerImpl(applicationContext);
    }

}

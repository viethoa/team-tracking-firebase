package us.originally.teamtrack.modules.dagger;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import us.originally.teamtrack.TeamTrackApplication;

/**
 * Created by VietHoa on 03/05/15.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                TeamTrackApplication.class
        }
)

public class AppModule {

    private WeakReference<Application> appWeakRef;

    public AppModule(Application context) {
        this.appWeakRef = new WeakReference<Application>(context);
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        if (appWeakRef != null) {
            return appWeakRef.get();
        } else {
            return null;
        }
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return new EventBus();
    }
}


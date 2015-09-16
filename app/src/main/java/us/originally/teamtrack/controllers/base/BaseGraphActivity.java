package us.originally.teamtrack.controllers.base;

import android.os.Bundle;

import com.lorem_ipsum.activities.BaseActivity;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import us.originally.teamtrack.TeamTrackApplication;
import us.originally.teamtrack.modules.dagger.RequestModule;

/**
 * Created by VietHoa on 16/09/15.
 */
public class BaseGraphActivity extends BaseActivity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TeamTrackApplication application = (TeamTrackApplication) getApplication();
        Object[] modules = getModules().toArray();
        activityGraph = application.extendScope(modules);
        activityGraph.inject(this);

        super.onCreate(savedInstanceState);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new RequestModule());
    }
}

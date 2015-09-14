package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lorem_ipsum.utils.AnimationUtils;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.BaseLoginActivity;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

public class LoginActivity extends BaseLoginActivity {

    public static final String TEAM_GROUP = "Team_Group";

    @InjectView(R.id.et_name)
    EditText etName;
    @InjectView(R.id.et_team)
    EditText etTeam;
    @InjectView(R.id.et_password)
    EditText etPassword;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        initialiseData();
        initialiseUI();
    }

    protected void initialiseData() {

    }

    protected void initialiseUI() {
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performLogin();
                }
                return false;
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //  Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_name_clear)
    protected void onBtnNameClearClicked() {
        etName.setText("");
    }

    @OnClick(R.id.btn_team_clear)
    protected void onBtnTeamClearClicked() {
        etTeam.setText("");
    }

    @OnClick(R.id.btn_password_clear)
    protected void onBtnPasswordClearClicked() {
        etPassword.setText("");
    }

    @OnClick(R.id.btn_back)
    protected void onBtnBackPressed() {
        onBackPressed();
    }

    @OnClick(R.id.btn_login)
    protected void onBtnLoginClicked() {
        performLogin();
    }

    protected void performLogin() {
        if (!validForm())
            return;

        String id = DeviceUtils.getDeviceUUID(this);
        String yourName = etName.getText().toString();
        String teamName = etTeam.getText().toString();
        String password = takePasswordMd5(etPassword.getText().toString());
        long timestemp = System.currentTimeMillis();

        UserTeamModel user = new UserTeamModel(id, yourName, 0, 0);
        TeamModel teamModel = new TeamModel(teamName, password, timestemp);

        showLoadingDialog();
        takeLoginOrSubscribe(teamModel, user);
    }

    protected String takePasswordMd5(String password) {
        MessageDigest messageDigest = null;
        try {

            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null)
            return null;

        messageDigest.reset();
        messageDigest.update(password.getBytes(Charset.forName("UTF8")));
        byte[] resultByte = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, resultByte);
        return bigInt.toString(16);
    }

    protected boolean validForm() {
        String name = etName.getText().toString();
        String team = etTeam.getText().toString();
        String password = etPassword.getText().toString();

        //Name
        if (StringUtils.isNull(name)) {
            AnimationUtils.shakeAnimationEdittext(this, etName);
            showToastErrorMessage(getString(R.string.str_login_valid_name));
            return false;
        }

        //Team
        if (StringUtils.isNull(team)) {
            AnimationUtils.shakeAnimationEdittext(this, etTeam);
            showToastErrorMessage(getString(R.string.str_login_valid_team));
            return false;
        }

        //Password
        if (StringUtils.isNull(password) || password.length() < 3) {
            AnimationUtils.shakeAnimationEdittext(this, etPassword);
            showToastErrorMessage(getString(R.string.str_login_valid_password));
            return false;
        }

        return true;
    }

    //----------------------------------------------------------------------------------------------
    //  Api helper
    //----------------------------------------------------------------------------------------------

    protected void takeLoginOrSubscribe(final TeamModel teamModel, final UserTeamModel user) {
        if (teamModel == null || user == null || StringUtils.isNull(teamModel.team_name)) {
            dismissLoadingDialog();
            return;
        }

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null) {
            dismissLoadingDialog();
            return;
        }

        ref.child(TEAM_GROUP).child(teamModel.team_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logDebug("Team: " + dataSnapshot.toString());
                dismissLoadingDialog();

                TeamModel team = null;
                try {
                    team = dataSnapshot.getValue(TeamModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (team == null) {
                    createNewTeam(teamModel, user);
                    return;
                }

                subcribe(team, teamModel, user);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dismissLoadingDialog();
                logError("Team error: " + firebaseError.getMessage());
            }
        });
    }

    protected void createNewTeam(TeamModel teamModel, UserTeamModel user) {
        if (teamModel == null || user == null)
            return;

        FireBaseAction.addNewTeam(this, teamModel, user);
        startActivity(MainActivity.getInstance(this));
        super.finish();
    }

    protected void subcribe(TeamModel firebaseTeam, TeamModel teamModel, UserTeamModel user) {
        if (teamModel == null || user == null)
            return;

        //Subscribe
        if (!teamModel.password.equals(firebaseTeam.password)) {
            AnimationUtils.shakeAnimationEdittext(this, etPassword);
            showToastErrorMessage("Invalid password !");
            return;
        }

        FireBaseAction.addNewUserToTeam(this, teamModel, user);
        startActivity(MainActivity.getInstance(this));
        super.finish();
    }
}

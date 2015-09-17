package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.utils.AnimationUtils;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.Constant;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.BaseLoginActivity;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.managers.UserManager;

public class LoginActivity extends BaseLoginActivity {

    private static final String EXTEC_LATITUDE = "latitude";
    private static final String EXTEC_LONGTITUDE = "longtitude";
    private double myLat;
    private double myLng;

    @InjectView(R.id.et_name)
    EditText etName;
    @InjectView(R.id.et_team)
    EditText etTeam;
    @InjectView(R.id.et_password)
    EditText etPassword;

    @Inject
    UserManager userManager;

    public static Intent getInstance(Context context, double lat, double lng) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(EXTEC_LATITUDE, lat);
        intent.putExtra(EXTEC_LONGTITUDE, lng);
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

    //----------------------------------------------------------------------------------------------
    //  Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {
        //Data extract
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        myLat = bundle.getDouble(EXTEC_LATITUDE);
        myLng = bundle.getDouble(EXTEC_LONGTITUDE);

        //Data Cache
        String teamName = CacheManager.getStringCacheData(Constant.CACHE_TEAM_KEY);
        if (StringUtils.isNotNull(teamName)) {
            etTeam.setText(teamName);
            etName.requestFocus();
        }

        String userName = CacheManager.getStringCacheData(Constant.CACHE_USER_NAME_KEY);
        if (StringUtils.isNotNull(userName)) {
            etName.setText(userName);
            etPassword.requestFocus();
        }
    }

    protected void initialiseUI() {
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onBtnLoginClicked();
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
        if (!validForm())
            return;

        if (!DeviceUtils.checkNetworkState(this)) {
            showToastErrorMessage("Please connect your internet!");
            return;
        }

        String id = DeviceUtils.getDeviceUUID(this);
        String yourName = etName.getText().toString();
        String teamName = etTeam.getText().toString();
        String password = takePasswordMd5(etPassword.getText().toString());
        long timeStamp = System.currentTimeMillis();

        final UserTeamModel user = new UserTeamModel(id, yourName, myLat, myLng);
        final TeamModel teamModel = new TeamModel(teamName, password, timeStamp);

        showLoadingDialog();
        userManager.loginOrSubscribe(teamModel, user, new LoginOrSubscribeCallback(teamModel, user));
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
}

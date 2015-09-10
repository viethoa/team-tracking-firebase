package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

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
import us.originally.teamtrack.models.TeamUser;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

public class LoginActivity extends BaseLoginActivity {

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

        String id = DeviceUtils.getDeviceUUID(this);
        String yourName = etName.getText().toString();
        String teamName = etTeam.getText().toString();
        String password = takePasswordMd5(etPassword.getText().toString());

        TeamUser user = new TeamUser(id, yourName, 0, 0);
        TeamModel teamModel = new TeamModel(id, teamName, password);

        boolean isLogin = FireBaseAction.takeLoginOrSubscribe(this, teamModel, user);
        if (!isLogin) {
            showToastErrorMessage("invalid password !");
            return;
        }

        startActivity(MainActivity.getInstance(this));
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

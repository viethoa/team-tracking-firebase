package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.BaseLoginActivity;

public class LoginActivity extends BaseLoginActivity {

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

    @OnClick(R.id.btn_back)
    protected void onBtnBackPressed() {
        onBackPressed();
    }

    @OnClick(R.id.btn_login)
    protected void onBtnLoginClicked() {
        if (!validForm())
            return;

        startActivity(MainActivity.getInstance(this));
    }

    protected boolean validForm() {

        return true;
    }
}

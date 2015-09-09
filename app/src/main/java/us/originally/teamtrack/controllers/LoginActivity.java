package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
    }
}

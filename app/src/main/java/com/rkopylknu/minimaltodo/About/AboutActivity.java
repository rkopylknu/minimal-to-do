package com.rkopylknu.minimaltodo.About;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import com.rkopylknu.minimaltodo.R;
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity;
import com.rkopylknu.minimaltodo.Main.MainFragment;

public class AboutActivity extends AppDefaultActivity {

    private TextView mVersionTextView;
    private String appVersion = "0.1";
    private Toolbar toolbar;
    private TextView contactMe;
    String theme;
    //    private UUID mId;
    private Application app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = getApplication();
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.DARKTHEME)) {
            Log.d("OskarSchindler", "One");
            app.setTheme(R.style.CustomStyle_DarkTheme);
        } else {
            Log.d("OskarSchindler", "One");
            app.setTheme(R.style.CustomStyle_LightTheme);
        }

        super.onCreate(savedInstanceState);
//        mId = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);

        final Drawable backArrow =
                getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        if (backArrow != null) {
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.about_layout;
    }

    @NonNull
    protected Fragment createInitialFragment() {
        return AboutFragment.newInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

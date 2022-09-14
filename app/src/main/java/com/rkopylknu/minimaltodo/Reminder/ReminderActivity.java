package com.rkopylknu.minimaltodo.Reminder;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.rkopylknu.minimaltodo.R;
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity;

public class ReminderActivity extends AppDefaultActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.reminder_layout;
    }

    @NonNull
    @Override
    protected ReminderFragment createInitialFragment() {
        return ReminderFragment.newInstance();
    }


}

package com.rkopylknu.minimaltodo.AppDefault;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rkopylknu.minimaltodo.R;

public abstract class AppDefaultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentViewLayoutRes());
        setUpInitialFragment(savedInstanceState);

    }

    private void setUpInitialFragment(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createInitialFragment())
                    .commit();
        }
    }

    @LayoutRes
    protected abstract int contentViewLayoutRes();

    @NonNull
    protected abstract Fragment createInitialFragment();
}

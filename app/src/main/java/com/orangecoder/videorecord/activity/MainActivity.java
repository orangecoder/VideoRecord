package com.orangecoder.videorecord.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.fragment.MediaFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                Fragment.instantiate(this, MediaFragment.class.getName())).commit();
    }

}

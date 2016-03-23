package com.orangecoder.videorecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.fragment.MediaFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                Fragment.instantiate(this, MediaFragment.class.getName())).commit();
    }

}

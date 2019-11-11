package com.slam.outcomehealthvideos.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.slam.outcomehealthvideos.R;
import com.slam.outcomehealthvideos.data.VideoData;
import com.slam.outcomehealthvideos.ui.main.MainFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by slam on 09/05/2019.
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener, Serializable {
    static private final String TAG = MainActivity.class.getSimpleName();

    static private final int HANDLER_MSG_NOTIFY_DATA_SET_CHANGED = 0x10000;

    AlertDialog dlgAbout = null;
    static private MainFragment _mainFragment;

//    static private Handler _handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case HANDLER_MSG_NOTIFY_DATA_SET_CHANGED:
//                    if (_mainFragment != null) {
//                        _mainFragment.notifyDataSetChanged();
//                    }
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            // On first launching the activity, attach the fragment.
            _mainFragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, _mainFragment)
                    .commitNow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _mainFragment = getFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:  // On option menu item "Settings" selected.
                Intent intent = new Intent(this, SettingsActivity.class);

                // For this to work, this MainActivity class MUST implement "Serializable"
//                intent.putExtra("MainActivity", this);

                Bundle bundle = new Bundle();
                bundle.putSerializable("MainActivity", this);
                intent.putExtras(bundle);

                startActivity(intent);
                return true;

            case R.id.action_about:     // On option menu item "About" selected.
                onCreateAboutDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the specific components in fragment_video_item is clicked.
     * (See the components which has the android:onClick="onVideoItemClicked")
     *
     * @param view
     */
    public void onVideoItemClicked(View view) {
        /*
         * The videoData object stored in the component's tag is used.
         *
         */
        VideoData videoData = (VideoData) view.getTag();
        if (videoData == null) {
            return;
        }
        Log.d(TAG, "MainActivity.onVideoItemClicked: " + videoData.title);
        getFragment().selectVideo(videoData);
    }

    /**
     * Get the fragment that is associate with this activity
     *
     * @return
     */
    public MainFragment getFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null) {
                for(int iX = fragments.size() - 1; iX >= 0; iX--){
                    Fragment fragment = fragments.get(iX);
                    if(fragment != null) {
                        if(fragment instanceof MainFragment) {
                            return (MainFragment)fragment;
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Show the video title which is currently playing on the actionBar.
     *
     * @param title
     */
    public void setAppTitleText(String title) {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * Create ans show the About dialog.
     */
    public void onCreateAboutDialog() {

        final AlertDialog.Builder dlgAboutBuilder = new AlertDialog.Builder(this);
        dlgAboutBuilder.setTitle("Options");
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View viewAboutDialog = layoutInflater.inflate(R.layout.about_dialog, null, false);

        dlgAboutBuilder.setView(viewAboutDialog);
        dlgAboutBuilder.setTitle(R.string.dialog_title_about);
        dlgAboutBuilder.setCancelable(true);
        dlgAboutBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dlgAbout.dismiss();
            }
        });
        dlgAbout = dlgAboutBuilder.create();
        dlgAbout.show();
    }

    /**
     * On clicking the email text, launch the email client and populate the
     * email address in the "To:" field
     *
     * @param view
     */
    public void onEmailClicked(View view) {
        dlgAbout.dismiss();

        Object objEmail = view.getTag();

        if (objEmail instanceof String) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { (String)objEmail });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        }
    }

    /**
     * This MainFragment.OnListFragmentInteractionListener method is not used for now.
     * It could implement some additional actions if needed.
     *
     * Note: Instead the LiveData observer is used.
     *
     * @param videoData
     */
    @Override
    public void onListFragmentInteraction(VideoData videoData) {
        Log.e(TAG, "onListFragmentInteraction() with new videoData, title = " + videoData.title);
    }

    public void notifyDataSetChanged() {
        if (true) {
            if (_mainFragment != null) {
                _mainFragment.notifyDataSetChanged();
            }
        }
        else {
//        if (_handler != null) {
//            Message message = _handler.obtainMessage(
//                HANDLER_MSG_NOTIFY_DATA_SET_CHANGED, 0, 0, null
//            );
//            _handler.sendMessage(message);
//        }
        }
    }
}

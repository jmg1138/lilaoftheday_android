package com.lilaoftheday.lilaoftheday.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lilaoftheday.lilaoftheday.R;
import com.lilaoftheday.lilaoftheday.activities.MainActivity;
import com.lilaoftheday.lilaoftheday.utilities.FragmentBoss;
import com.lilaoftheday.lilaoftheday.utilities.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment implements View.OnClickListener {

    View view;
    MainActivity mainActivity;

    long dbRecordId;
    int menuItemHome = Utilities.generateViewId();
    ImageView imageViewCatPhoto;
    int fullScreenImageResourceId;
    Dialog fullScreenImageDialog;

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_photo, container, false);

        mainActivity = (MainActivity) getActivity();

        if (mainActivity != null && mainActivity.getSupportActionBar() != null) {
            ActionBar sab = mainActivity.getSupportActionBar();
            boolean landscape = mainActivity.getResources().getBoolean(R.bool.is_landscape);
            boolean xlarge = mainActivity.screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
            if (!landscape && !xlarge) {
                sab.setDisplayHomeAsUpEnabled(true);
                sab.setDisplayShowHomeEnabled(true);
            } else {
                sab.setDisplayHomeAsUpEnabled(false);
                sab.setDisplayShowHomeEnabled(false);
            }
        }
        setHasOptionsMenu(true);
        getFragmentArguments();

        imageViewCatPhoto = (ImageView) view.findViewById(R.id.photo);
        final int imageResourceId = (int) dbRecordId;
        imageViewCatPhoto.setImageResource(imageResourceId);
        imageViewCatPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreenPhoto(imageResourceId).show();
            }
        });

        if (fullScreenImageResourceId > 0) {
            fullScreenPhoto(fullScreenImageResourceId).show();
        }

        return view;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            menu.clear(); // Clear the existing menu.
            // If not landscape, add a menu icon to retrieve the "home" fragment.
            boolean landscape = mainActivity.getResources().getBoolean(R.bool.is_landscape);
            boolean xlarge = mainActivity.screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
            if (!landscape && !xlarge) {
                Drawable iconHome = ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.ic_home_white_48dp
                );
                iconHome.setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.ColorAccent),
                        PorterDuff.Mode.SRC_ATOP
                );
                menu.add(Menu.NONE, menuItemHome, 0, R.string.actionHome)
                        .setIcon(iconHome)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }
        super.onPrepareOptionsMenu(menu);
        mainActivity.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            mainActivity.onBackPressed();
            return true;
        } else if (itemId == menuItemHome) {
            mainActivity.showGridFragment();
            FragmentBoss.topFragmentOnResume(getFragmentManager());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPause() {
        if (fullScreenImageDialog != null) {
            fullScreenImageDialog.dismiss();
            fullScreenImageDialog = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (((MainActivity) getActivity()) != null) {
            mainActivity = (MainActivity) getActivity();
            mainActivity.resurfaceView(R.id.photoContainer);
        }
        // Update the action bar title and menu.
        if (mainActivity != null && mainActivity.getSupportActionBar() != null) {
            ActionBar sab = mainActivity.getSupportActionBar();
            boolean landscape = mainActivity.getResources().getBoolean(R.bool.is_landscape);
            boolean xlarge = mainActivity.screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
            if (!landscape && !xlarge) {
                sab.setTitle(R.string.fragmentTitlePhoto);
                sab.setDisplayHomeAsUpEnabled(true);
                sab.setDisplayShowHomeEnabled(true);
            } else {
                sab.setTitle(R.string.fragmentTitleMain);
                sab.setDisplayHomeAsUpEnabled(false);
                sab.setDisplayShowHomeEnabled(false);
            }
            sab.invalidateOptionsMenu();
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public static PhotoFragment newInstance(long dbRecordID){
        PhotoFragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("dbRecordID", dbRecordID);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void getFragmentArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("dbRecordID")){
            dbRecordId = args.getLong("dbRecordID", 0);
        }
        if (args != null && args.containsKey("fullScreenImageResourceId")) {
            fullScreenImageResourceId = args.getInt("fullScreenImageResourceId", 0);
        }
    }

    public Dialog fullScreenPhoto(int imageResourceId) {

        getArguments().putInt("fullScreenImageResourceId", imageResourceId);

        final ImageView imageViewPhoto = new ImageView(getContext());
        imageViewPhoto.setImageResource(imageResourceId);
        imageViewPhoto.setTag(imageResourceId);
        RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        ImageView imageViewPrev = new ImageView(getContext());
        imageViewPrev.setImageResource(R.drawable.ic_chevron_left_white_48dp);
        FrameLayout.LayoutParams paramsPrev = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        paramsPrev.gravity = Gravity.START;
        imageViewPrev.setLayoutParams(paramsPrev);

        ImageView imageViewNext = new ImageView(getContext());
        imageViewNext.setImageResource(R.drawable.ic_chevron_right_white_48dp);
        FrameLayout.LayoutParams paramsNext = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        paramsNext.gravity = Gravity.END;
        imageViewNext.setLayoutParams(paramsNext);

        fullScreenImageDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                /*fullScreenImageDialog.onBackPressed();*/
                return true;
            }

            @Override
            public void onBackPressed() {
                getArguments().remove("fullScreenImageResourceId");
                fullScreenImageResourceId = 0;
                fullScreenImageDialog.dismiss();
                fullScreenImageDialog = null;
            }

        };

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.addView(imageViewPhoto);
        frameLayout.addView(imageViewPrev);
        frameLayout.addView(imageViewNext);

        imageViewPrev.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        // Get copy of current resource ID from arguments.
                        int i = getArguments().getInt("fullScreenImageResourceId", 0);
                        // Remove current resource ID from arguments.
                        getArguments().remove("fullScreenImageResourceId");
                        // Decrement old resource ID.
                        i--;
                        // Put new resource ID into arguments.
                        getArguments().putInt("fullScreenImageResourceId", i);
                        // Set new resource ID as image resource.
                        imageViewPhoto.setImageResource(i);
                    }
                }
        );
        imageViewNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        // Get copy of current resource ID from arguments.
                        int i = getArguments().getInt("fullScreenImageResourceId", 0);
                        // Remove current resource ID from arguments.
                        getArguments().remove("fullScreenImageResourceId");
                        // Increment old resource ID.
                        i++;
                        // Put new resource ID into arguments.
                        getArguments().putInt("fullScreenImageResourceId", i);
                        // Set new resource ID as image resource.
                        imageViewPhoto.setImageResource(i);
                    }
                }
        );

        fullScreenImageDialog.addContentView(frameLayout, params);

        return fullScreenImageDialog;

    }

}


package ru.mahovd.bignerdranch.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by mahovd on 25/03/16.
 * Controller
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}

package ru.mahovd.bignerdranch.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by mahovd on 25/03/16.
 * Controller
 * Main Activity, inherits methods (that especially important onCreate method)
 *from SingleFragmentActivity
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {



    /*Implementation of abstract method createFragment
      retrieve new  instance of the Fragment class from support v4 library
      (in our case it is the PhotoGalleryFragment)*/
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}

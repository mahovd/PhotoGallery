package ru.mahovd.bignerdranch.photogallery;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by dmitrijmahov on 24.03.16.
 * Controller
 */
public class PhotoGalleryFragment extends Fragment{

    private final static String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new FetchItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        return v;

    }

    private class FetchItemTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {

            new FlickrFetch().fetchItems();

            return  null;
        }
    }

}

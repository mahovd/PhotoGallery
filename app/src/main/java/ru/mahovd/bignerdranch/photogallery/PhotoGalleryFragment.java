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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dmitrijmahov on 24.03.16.
 * Controller
 */
public class PhotoGalleryFragment extends Fragment{

    private final static String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTextView;

        public PhotoHolder(View itemView){
            super(itemView);

            mTitleTextView = (TextView)itemView;
        }

        public void bindGalleryItem(GalleryItem item){
            mTitleTextView.setText(item.toString());
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        public void onBindViewHolder(PhotoHolder photoHolder, int position){
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        }

        public int getItemCount(){
            return mGalleryItems.size();
        }

    }

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Helps instance of the fragment not to be destroyed when configuration changes
        setRetainInstance(true);


        //Kick-off the fetching and parsing data
        new FetchItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);

        //Sets the particular type of LayoutManager
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;

    }

    private void setupAdapter(){

        //Checks whether Fragment has been attached or not
        if (isAdded()){
            //Creates and sets PhotoAdapter for our RecyclerView
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    //AsyncTask for getting and parsing JSON-data
    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

           return new FlickrFetch().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    //TODO: I have to read about memory leaks related to AsyncTask and retained Fragment
    //TODO: maybe I need to override method onDestroy



}

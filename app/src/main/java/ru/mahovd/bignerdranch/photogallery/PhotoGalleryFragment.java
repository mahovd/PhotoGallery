package ru.mahovd.bignerdranch.photogallery;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private TextView mEmptyView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private PhotoAdapter mAdapter;


    private int mCurrentPage = 0;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private static final int PAGE_SIZE = 100;

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
        FlickrFetch.setPageNum(mCurrentPage);
        new FetchItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);

        //Sets the particular type of LayoutManager
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        //mEmptyView = (TextView) v.findViewById(R.id.empty_view);

        mPhotoRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        setupAdapter();


        return v;

    }

    private void setupAdapter(){

        //Checks whether Fragment has been attached or not
        if (isAdded()){
            if(mAdapter == null){
                mAdapter = new PhotoAdapter(mItems);
                //Creates and sets PhotoAdapter for our RecyclerView
                mPhotoRecyclerView.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //AsyncTask for getting and parsing JSON-data
    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            mCurrentPage +=1;
            FlickrFetch.setPageNum(mCurrentPage);

            Log.i("PhotoGalleryFragment","doInBackground was started");

           return new FlickrFetch().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {

            //mItems = items;
            mItems.addAll(items);

            setupAdapter();
        }
    }

    //TODO: I have to read about memory leaks related to AsyncTask and retained Fragment
    //TODO: maybe I need to override method onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        new FetchItemTask().cancel(true);
    }

    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


            GridLayoutManager mLayoutManager = (GridLayoutManager) mPhotoRecyclerView.getLayoutManager();
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if(!mIsLoading && !mIsLastPage){
                if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE){

                    loadMoreItems();
                }
            }
        }
    };

    private void loadMoreItems(){

        new FetchItemTask().execute();

    }

}

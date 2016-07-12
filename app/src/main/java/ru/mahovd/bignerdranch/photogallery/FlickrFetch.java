package ru.mahovd.bignerdranch.photogallery;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahovd on 25/03/16.
 * Controller
 *
 * Contains methods for fetching and parsing data (JSON)
 */
public class FlickrFetch {

    private static final String TAG  = "FlickrFetch";
    private static final String API_KEY  = "ee2ccc95d9f1e54e82de945b8b444e96";
    private static Integer mPageNum = 1;


    //The main method for fetching data
    public byte[] getUrlBytes(String urlSpec) throws IOException{

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+ ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer,0,bytesRead);
            }

            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }

    }

    //Converts an ArrayOfBytes to String
    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public static void setmPageNum(Integer mPageNum) {
        FlickrFetch.mPageNum = mPageNum;
    }

    //Kick-off the processes of fetching and parsing data
    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/").
                    buildUpon().
                    appendQueryParameter("method", "flickr.photos.getRecent").
                    appendQueryParameter("api_key", API_KEY).
                    appendQueryParameter("format", "json").
                    appendQueryParameter("nojsoncallback", "1").
                    appendQueryParameter("extras", "url_s").
                    appendQueryParameter("per_page","100").
                    appendQueryParameter("page",mPageNum.toString())
                    .build().toString();

            String jsonString = getUrlString(url);

            Log.i(TAG, "Received JSON: " + jsonString);

            //JSONObject jsonBody = new JSONObject(jsonString);
            //parseItems(items,jsonBody);

            parseItemsGSON(items,jsonString);

        //} catch (JSONException je){
        //    Log.e(TAG,"Failed to parse JSON",je);
        } catch (IOException ioe){
            Log.e(TAG,"Failed to fetch items",ioe);
        }

        return items;

    }

    //Parses JSON to List of GalleryItems
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException{
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i=0; i<photoJsonArray.length(); i++){
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);

        }
    }


    //Alternate way to parse JSON
    private void parseItemsGSON(List<GalleryItem> items, String jsonString){

        Gson gson = new GsonBuilder().create();

        Flickr flickr = gson.fromJson(jsonString,Flickr.class);

        for(Photo p:flickr.photos.photo){
            GalleryItem item = new GalleryItem();
            item.setId(p.id);
            item.setCaption(p.title);

            if(p.url_s == null){
                continue;
            }

            item.setUrl(p.url_s);
            items.add(item);
        }

    }

    //Inner class for parsing Json via GSON
    private class Flickr{
        public Photos photos;
    }

    //Inner class for parsing Json via GSON
    private class Photos {
        public List<Photo> photo;
        private  int page;
    }

    //Inner class for parsing Json via GSON
    private class Photo{
        public String id;
        public String title;
        public String url_s;
    }

}

package me.plavikrug;

import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Vuk on 11.9.2017..
 */
public interface AsyncResponse {
    void processFinish(String[] output)  throws JSONException;
    void processFinish(Uri uri)  throws IOException;

}

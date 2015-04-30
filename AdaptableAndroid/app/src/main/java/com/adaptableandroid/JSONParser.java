package com.adaptableandroid;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Connie on 4/26/2015.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jsonObject = null;
    static String json = "";

    public JSONParser(){

    }

    public JSONObject getJSONFromURL(String url){
        // Making HTTP Request
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        Log.d("Got HTTP Request", "Success!");
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch(Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        Log.d("Grabbed the json result", "Success!");
        // try to parse the string to a JSON object
        try{
            jsonObject = new JSONObject(json);

        } catch(JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        Log.d("Returning json result", "Success!");
        // returnin JSON String
        return jsonObject;
    }

    public JSONObject makeHttpRequest(String url){
        return null;
    }



}

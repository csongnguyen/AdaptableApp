package com.adaptableandroid;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        try {
            return makeHttpGetRequest(url, "", "");
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject makeHttpPostRequest(String url, String paramTypeToCompare, String parameter1, String paramTypeToUpdate, String parameter2){
        HttpPost httpPost = new HttpPost();
        // Making HTTP Request
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);

            if(!StringUtils.stringIsEmpty(paramTypeToUpdate) && !StringUtils.stringIsEmpty(parameter2)){
                // I think this example is for posting
                List<NameValuePair> pars = new ArrayList<NameValuePair>();
                pars.add(new BasicNameValuePair(paramTypeToCompare, parameter1));
                pars.add(new BasicNameValuePair(paramTypeToUpdate, parameter2));

                httpPost.setEntity(new UrlEncodedFormEntity(pars));
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            Log.d("Got HTTP Request", "Success! It is: " + httpPost.getURI());
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("Grabbed the json result", "Success!");
        } catch(Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try to parse the string to a JSON object
        try{
            System.out.println("Printing JSONObject string= " + json);
            jsonObject = new JSONObject(json);
            Log.d("Returning json result", "Success!");
        } catch(JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // returnin JSON String
        return jsonObject;
    }

    public JSONObject makeHttpGetRequest(String url, String paramType, String parameter){
        HttpPost httpPost = new HttpPost();
        // Making HTTP Request
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);
            System.out.println("Paramtype is null: " + StringUtils.stringIsEmpty(paramType) + ", Parameter is null: " + StringUtils.stringIsEmpty(parameter));
            if(!StringUtils.stringIsEmpty(paramType) && !StringUtils.stringIsEmpty(parameter)){
                String newURL = url+ "?" + paramType + "=" + parameter;
                System.out.println("Grabbing url " + newURL);
                httpPost = new HttpPost(newURL);


                // I think this example is for posting
//                List<NameValuePair> pars = new ArrayList<NameValuePair>();
//                pars.add(new BasicNameValuePair(paramType, parameter));
//                httpPost.setEntity(new UrlEncodedFormEntity(pars));
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            Log.d("Got HTTP Request", "Success! It is: " + httpPost.getURI());
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("Grabbed the json result", "Success!");
        } catch(Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try to parse the string to a JSON object
        try{
            jsonObject = new JSONObject(json);
            Log.d("Returning json result", "Success!");
        } catch(JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            jsonObject = null;
        }

        // returnin JSON String
        return jsonObject;
    }



}

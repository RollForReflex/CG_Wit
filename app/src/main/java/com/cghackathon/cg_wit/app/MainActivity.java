package com.cghackathon.cg_wit.app;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cghackathon.cg_wit.app.JSONParser.JSONParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import ai.wit.sdk.IWitListener;
import ai.wit.sdk.Wit;

public class MainActivity extends ActionBarActivity implements IWitListener {

    private class JSONParse extends AsyncTask<String, String, String> {

        private String URL = "http://www.willfallows.com/Hackathon/entities.json";
        private Intent[] _data;

        private Intent[] GetHackathonData(){
            return _data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            String json = jParser.getJSONStringFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(String json) {
            Gson gson = new Gson();
            //_data = gson.fromJson(json, Intent[].class);
            JsonArray jsonArray = new JsonArray();
            try {
                 jsonArray = jsonArray.
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String cats = jsonArray.toString();
        }
    }

    private HackathonData responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Fragment
        Wit wit_fragment = (Wit) getSupportFragmentManager().findFragmentByTag("wit_fragment");
        if (wit_fragment != null) {
            wit_fragment.setAccessToken("SUBVK4TMRLAA6UYX2Y2A2MDPT4NSXM5Q");
        }

        JSONParse jsonParse = new JSONParse();
        jsonParse.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent GetIntentWithString(String intentName)
    {
        for(int i = 0; i < responseData.intents.size(); i++)
        {
            if(responseData.intents.get(i).name.toLowerCase() == intentName.toLowerCase())
                return responseData.intents.get(i);
        }

        return null;
    }

    // Should match the intent passed in from witDidGraspIntent and grab any entities if they exist and see if the intent has any matching entities
    // If it does, return the entity response. Otherwise, return the default response for the intent
    private String GetResponseForIntent(Intent intent){
        return new String();
    }

    @Override
    public void witDidGraspIntent(String intent, HashMap<String, JsonElement> entities, String body, double confidence, Error error) {
       ((TextView) findViewById(R.id.txtText)).setText(body);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(entities);
        ((TextView) findViewById(R.id.jsonView)).setText(Html.fromHtml("<span><b>Intent: " + intent +
                "<b></span><br/>") + jsonOutput +
                Html.fromHtml("<br/><span><b>Confidence: " + confidence + "<b></span>" + "</br><span><b>Response:</b> "
                        +  jsonOutput +  "</span>" ));

        if(intent != null && jsonOutput != null){
            GetEntityResponse(intent, jsonOutput);
        }
    }

    public String GetEntityResponse(String intent, String jsonOutput){
        if(intent.toLowerCase() == "greetings"){
            return "Hello, user";
        }
        else if(intent.toLowerCase() == "boy"){
            return "Not important";
        }
        else if(intent.toLowerCase() == "are_questions"){
            if(jsonOutput.contains("the boy")){
                return "And if I am?";
            }
            else if(jsonOutput.contains("mother")){
                return "Mother is all of us";
            }
            else{
                return "Nope";
            }
        }
        return "I didn't understand that. Try again";
    }
}

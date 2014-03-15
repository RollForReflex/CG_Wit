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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.wit.sdk.IWitListener;
import ai.wit.sdk.Wit;

public class MainActivity extends ActionBarActivity implements IWitListener {

    private class JSONParse extends AsyncTask<String, String, String> {

        private String URL = "http://www.willfallows.com/Hackathon/entities.json";
        private List<Intent> _data;

        public List<Intent> GetHackathonData(){
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
            JSONObject obj = new JSONObject();
            try {
                obj = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = obj.getJSONArray("intents");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<Intent> intentList = new ArrayList<Intent>();
            for(int i = 0 ; i < array.length() ; i++){
                try {
                    System.out.println(array.getJSONObject(i).getString("name"));
                    Intent intent = new Intent();
                    intent.name = array.getJSONObject(i).getString("name");
                    intent.generalResponse = array.getJSONObject(i).getString("generalResponse");
                    JSONArray entitiesArr = array.getJSONObject(i).getJSONArray("entities");
                    if(entitiesArr.length() > 0){

                        List<Entity> entityList = new ArrayList<Entity>();
                        for(int j = 0; j < entitiesArr.length(); j++){
                            Entity entity = new Entity();
                            entity.name = entitiesArr.getJSONObject(j).getString("name");
                            entity.response = entitiesArr.getJSONObject(j).getString("response");
                            entityList.add(entity);
                        }
                        intent.entities = entityList;
                    }

                    intentList.add(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            _data = intentList;
        }
    }

    JSONParse parseObj = new JSONParse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Fragment
        Wit wit_fragment = (Wit) getSupportFragmentManager().findFragmentByTag("wit_fragment");
        if (wit_fragment != null) {
            wit_fragment.setAccessToken("SUBVK4TMRLAA6UYX2Y2A2MDPT4NSXM5Q");
        }

        parseObj.execute();

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

    // Should match the intent passed in from witDidGraspIntent and grab any entities if they exist and see if the intent has any matching entities
    // If it does, return the entity response. Otherwise, return the default response for the intent
    private String GetResponseForIntent(String intent){
        List<Intent> data = parseObj.GetHackathonData();
        String responseStr = null;

        for(int i = 0; i < data.size(); i++){

            if(data.get(i).entities.size() > 0) {
                if (data.get(i).name == intent) {
                    responseStr = data.get(i).generalResponse;
                }
            }
            else{
                Intent curIntent = data.get(i);
                for(int j = 0; j < curIntent.entities.size(); j++){
                    if(curIntent.entities.get(j).name == intent){
                        responseStr = curIntent.entities.get(j).response;
                    }
                }
            }
        }

        if(responseStr == null){
            return "Did not find matching entity";
        }else{
            return responseStr;
        }
    }

    @Override
    public void witDidGraspIntent(String intent, HashMap<String, JsonElement> entities, String body, double confidence, Error error) {
       ((TextView) findViewById(R.id.txtText)).setText(body);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(entities);
        ((TextView) findViewById(R.id.jsonView)).setText(Html.fromHtml("<span><b>Intent: " + intent +
                "<b></span><br/>") + jsonOutput +
                Html.fromHtml("<br/><span><b>Confidence: " + confidence + "<b></span>" + "</br><span><b>Response:</b> "
                        +  GetEntityResponse(intent, jsonOutput) +  "</span>" ));
    }

    private static String readAll(Reader rd) throws IOException {

        BufferedReader reader = new BufferedReader(rd);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
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

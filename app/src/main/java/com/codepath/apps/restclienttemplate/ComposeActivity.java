package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH =  140;

    private EditText etCompose;
    private Button btnTweet;
    private TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        //Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                //TODO: Error-handling
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet is Empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet is too Long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                //If the button is tapped, then make the API call (network request) to Twitter to publish
                // the tweet (content in edit text)
                client.composeTweet(tweetContent, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("TwitterClient", "Successfully Posted Tweet!" + response.toString());
                        try {
                            Tweet tweet = Tweet.fromJson(response);
                            Intent data = new Intent();
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            //Set result code and budle data for response
                            setResult(RESULT_OK, data);
                            //closes the activity, pass data to the parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("TwitterClient", "Failed to post tweet" + responseString);
                    }
                });
            }
        });



    }
}

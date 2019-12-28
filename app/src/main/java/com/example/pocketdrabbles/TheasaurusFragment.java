package com.example.pocketdrabbles;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;


/**
 * A simple {@link Fragment} subclass.
 */
public class TheasaurusFragment extends Fragment {

    private Context mContext;
    private final String wordBaseUrl = "https://od-api.oxforddictionaries.com/api/v2/words/en-gb?q=";
    private final String thesaurusBaseUrl = "https://od-api.oxforddictionaries.com/api/v2/thesaurus/en/";
    private final String app_id = "77e93578";
    private final String app_key = "aed0ea45af306c8991327d02ee9d03bf";

    EditText word;
    Button search;
    TextView definition;
    ListView synonymsList;
    TextView example;
    String def;
    String exampleText;

    String word_id;

    public TheasaurusFragment(Context context) {
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_theasaurus, container, false);

        word = view.findViewById(R.id.word);
        search = view.findViewById(R.id.search);
        definition = view.findViewById(R.id.definition);
        definition.setMovementMethod(new ScrollingMovementMethod());
        synonymsList = view.findViewById(R.id.synonyms);
        example = view.findViewById(R.id.example);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!word.getText().toString().equals(""))
                {
                    final AsyncHttpClient client = new AsyncHttpClient();
                    final Header[] reqHeaders = new Header[3];
                    reqHeaders[0] = new Header() {
                        @Override
                        public String getName() {
                            return "app_id";
                        }

                        @Override
                        public String getValue() {
                            return app_id;
                        }

                        @Override
                        public HeaderElement[] getElements() throws ParseException {
                            return new HeaderElement[0];
                        }
                    };
                    reqHeaders[1] = new Header() {
                        @Override
                        public String getName() {
                            return "app_key";
                        }

                        @Override
                        public String getValue() {
                            return app_key;
                        }

                        @Override
                        public HeaderElement[] getElements() throws ParseException {
                            return new HeaderElement[0];
                        }
                    };
                    reqHeaders[2] = new Header() {
                        @Override
                        public String getName() {
                            return "Accept";
                        }

                        @Override
                        public String getValue() {
                            return "application/json";
                        }

                        @Override
                        public HeaderElement[] getElements() throws ParseException {
                            return new HeaderElement[0];
                        }
                    };
                    client.get(mContext, wordBaseUrl+word.getText().toString(), reqHeaders, null, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONArray results = response.getJSONArray("results");
                                def = results.getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").getJSONObject(0).getJSONArray("senses").getJSONObject(0).getJSONArray("definitions").get(0).toString();
                                word_id = results.getJSONObject(0).getString("id");

                                if(word_id.equals(""))
                                    Toast.makeText(mContext, "No word_id for "+word.getText().toString(), Toast.LENGTH_SHORT).show();
                                else {

                                    AsyncHttpClient client2 = new AsyncHttpClient();
                                    client2.get(mContext, thesaurusBaseUrl+word_id, reqHeaders, null, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            try {
                                                exampleText = response.getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").getJSONObject(0).getJSONArray("senses").getJSONObject(0).getJSONArray("examples").getJSONObject(0).getString("text");
                                                JSONArray syns = response.getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").getJSONObject(0).getJSONArray("senses").getJSONObject(0).getJSONArray("synonyms");
                                                ArrayList<String> synonyms = new ArrayList<String>();
                                                for(int i=0; i<syns.length(); i++)
                                                {
                                                    synonyms.add(syns.getJSONObject(i).getString("text"));
                                                }
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.listview_item, R.id.text, synonyms);

                                                definition.setText(def);
                                                example.setText(exampleText);
                                                synonymsList.setAdapter(adapter);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                            try {
                                                Log.e("Error", errorResponse.getString("error"));
                                                Toast.makeText(mContext, "No results", Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            try {
                                Log.e("Error", errorResponse.getString("error"));
                                Toast.makeText(mContext, "No result", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            }
        });

        return view;
    }

}

package proyectofinal.autocodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.adapter.ParticipantAddedArrayAdapter;
import proyectofinal.autocodes.adapter.ParticipantDefaultAdapter;

import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;

public class CreateGroupActivity extends Activity implements OnClickListener {

    String serverBaseUrl = "http://107.170.81.44:3002";

    private EditText mSearchField;
    private TextView mXMark;
    private ArrayList<Participant> participantSearcheableList;
    private ArrayList<Participant> participantShowableList;
    private DynamicListView mDynamicListView;
    private Button submitGroup;
    private GridView participantsAddedListView;
    private List<Participant> participantAddedList;
    private EditText groupNameToCreate;
    BaseAdapter participantDefaultAdapter;
    ParticipantAddedArrayAdapter participantAddedArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setUpImageLoader();

        groupNameToCreate = (EditText) findViewById(R.id.groupNameToCreate);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mXMark = (TextView) findViewById(R.id.search_x);
        participantsAddedListView = (GridView) findViewById(R.id.participantAddedView);
        submitGroup = (Button) findViewById(R.id.submitGroup);

        participantsAddedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                participantAddedList.get(position).setIconRes(R.string.fontello_heart_empty);
                participantAddedList.remove(position);
                ((BaseAdapter)participantsAddedListView.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
            }
        });

        getFriendList2();
        getFriendList();

        mXMark.setOnClickListener(this);

        mDynamicListView = (DynamicListView) findViewById(R.id.dynamic_listview);
        populateDynamicList();
        populateParticipantsAddedList();

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                participantShowableList.clear();
                for (Participant participant : participantSearcheableList) {
                    if (participant.getName().toLowerCase()
                            .contains(searchText.toLowerCase())) {
                        participantShowableList.add(participant);
                    }
                }

                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
            }
        });

        mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Participant participant = (Participant) adapterView.getItemAtPosition(position);
                participant.setIconRes(R.string.fontello_heart_full);
                if(!participantAddedList.contains(participant)) {
                    participantAddedList.add(participant);
                }
                ((BaseAdapter)participantsAddedListView.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
                mSearchField.setText("");
            }
        });

        submitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(participantAddedList.isEmpty()) {
                    Toast.makeText(CreateGroupActivity.this, "Debe agregar participantes al grupo " +
                            "para continuar", Toast.LENGTH_LONG).show();
                }else if("".equals(groupNameToCreate.getText().toString())) {
                    Toast.makeText(CreateGroupActivity.this, "Debe asignar un nombre al grupo " +
                            "para continuar", Toast.LENGTH_LONG).show();
                } else {
//                    createGroup(participantAddedList, groupNameToCreate.getText(), Profile.getCurrentProfile().getId());
                    finish();
                }

            }
        });
    }

    private void populateParticipantsAddedList() {
        participantAddedArrayAdapter = new ParticipantAddedArrayAdapter(this, participantAddedList);

        GridView gridView = (GridView) findViewById(R.id.participantAddedView);
        gridView.setAdapter(participantAddedArrayAdapter);
    }



    private void getFriendList2() {

        participantSearcheableList = new ArrayList<Participant>();
        participantShowableList = new ArrayList<Participant>();
        participantAddedList = new ArrayList<Participant>();
        Participant p1 = new Participant(1,"Fede","http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg",  R.string.fontello_heart_empty);
        Participant p2 = new Participant(2,"Lucas","http://pengaja.com/uiapptemplate/newphotos/profileimages/1.jpg",  R.string.fontello_heart_empty);
        Participant p3 = new Participant(3,"Hernan","http://pengaja.com/uiapptemplate/newphotos/profileimages/2.jpg",  R.string.fontello_heart_empty);
        Participant p4 = new Participant(4,"Gaby","http://pengaja.com/uiapptemplate/newphotos/profileimages/3.jpg",  R.string.fontello_heart_empty);
        Participant p5 = new Participant(5,"Otro gato","http://pengaja.com/uiapptemplate/newphotos/profileimages/4.jpg",  R.string.fontello_heart_empty);
        Participant p6 = new Participant(6,"El chavo","http://pengaja.com/uiapptemplate/newphotos/profileimages/5.jpg",  R.string.fontello_heart_empty);
        Participant p7 = new Participant(7,"La chilindrina","http://pengaja.com/uiapptemplate/newphotos/profileimages/6.jpg",  R.string.fontello_heart_empty);
        Participant p8 = new Participant(8,"Don ramon","http://pengaja.com/uiapptemplate/newphotos/profileimages/7.jpg",  R.string.fontello_heart_empty);

        participantSearcheableList.add(p1);
        participantSearcheableList.add(p2);
        participantSearcheableList.add(p3);
        participantSearcheableList.add(p4);
        participantSearcheableList.add(p5);
        participantSearcheableList.add(p6);
        participantSearcheableList.add(p7);
        participantSearcheableList.add(p8);

        participantShowableList.addAll(participantSearcheableList);

    }

    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_x:
                mSearchField.setText(null);
                break;
        }
    }

    private void populateDynamicList() {
        participantDefaultAdapter = new ParticipantDefaultAdapter(this,
                participantShowableList, false);
        mDynamicListView.setAdapter(participantDefaultAdapter);
    }

    public void getFriendList(){
        AccessToken at = AccessToken.getCurrentAccessToken();
        GraphRequest req = GraphRequest.newMyFriendsRequest(at, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                FacebookRequestError error = response.getError();
                if(error != null){
                    String err = error.getErrorMessage();
                }
                Log.e("FacebookResponse:", response.getRawResponse());
                try {
                    JSONObject obj = (JSONObject) objects.get(0);
                    Iterator<?> it = obj.keys();
                    while (it.hasNext()){
                        Participant p1 = new Participant(1,"Fede","http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg",  R.string.fontello_heart_empty);
                        Log.e("KEY", (String) it.next());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        req.executeAsync();
    }
}

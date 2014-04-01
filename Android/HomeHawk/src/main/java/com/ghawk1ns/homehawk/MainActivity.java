package com.ghawk1ns.homehawk;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
      //////////////////////////////////////////
     //          Parse Data Constants        //
    //////////////////////////////////////////
    public static final String NEW_USER = "__newUser__";
    public static final String USER = "user";
    public static final String NETWORKS = "networks";
    public static final String NETWORK_NAME = "networkName";
    public static final String NETWORK_ID = "networkId";
    //cloud code functions
    public static final String CLOUD_CREATE_NETWORK = "createNetwork";
    /////////////////////////////////////////////////

    private ParseUser user; //current user logged in system

      /////////////
     //  Views  //
    /////////////

    TextView userName;
    ListView userNetworkListView;
    ParseObjectArrayAdapter userNetworkListAdapter;

    int mStackLevel;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            //TODO: Research this
        }

        //set the current user and the name
        user = ParseUser.getCurrentUser();
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(user.getUsername());
        //Register device with new users
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            boolean newUser = bundle.getBoolean(NEW_USER);
            if(newUser){
                updateInstallation();
            }
        }

        //popluate networks
        getUsersNetworks();




    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0,R.id.logout,0,getString(R.id.logout));
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //PrefUtils.clearLoginCredentials(this);
                ParseUser.logOut();
                Intent in =  new Intent(MainActivity.this, Login.class);
                startActivity(in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
        Whenever there is a new user, or a switch of users,
        we need to update the current installation and set a pointer to the current logged in user
     */
    private void updateInstallation() {
        ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
        currentInstall.put(USER, user);
        currentInstall.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    //TODO: Set a flag, notify user device couldn't sync with user, hopefully this is very rare
                    //problem with parse, could not set the installation
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUsersNetworks() {
        ParseRelation networksRelation = user.getRelation(NETWORKS);
        ParseQuery networksQuery = networksRelation.getQuery();
        networksQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                //all good, either display networks or ask to creat a network
                if(e == null){
                    populateListOfUserNetworks((ArrayList<ParseObject>) list);
                }
                else{
                    //deal with parse exception
                    Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populateListOfUserNetworks(final ArrayList<ParseObject> list){
        userNetworkListAdapter = new ParseObjectArrayAdapter(this,list,NETWORK_NAME);
        userNetworkListView = (ListView) findViewById(R.id.networkList);
        userNetworkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //display network fragment
                String n = (String) list.get(position).get(NETWORK_ID);
                Toast.makeText(getApplicationContext(),
                        n, Toast.LENGTH_LONG)
                        .show();
            }
        });
        userNetworkListView.setAdapter(userNetworkListAdapter);
        // LoadMore button
        final Button createNetwork = new Button(this);
        createNetwork.setText("Create a new Network");
        createNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateNetworkDialogFragment(MainActivity.this);
            }
        });
        // Adding Load More button to lisview at bottom
        userNetworkListView.addFooterView(createNetwork);
    }

    //a Fragment to display a dialog to initiate the creation of a network for the user
    public class CreateNetworkDialogFragment extends DialogFragment {

        final private EditText editText;

        final private AlertDialog alertDialog;

        private Boolean canceled;

        CreateNetworkDialogFragment(Context context) {
            editText = new EditText(context);
            //give room for text edit errors
            alertDialog = buildAlertDialog(context);
            alertDialog.setOnDismissListener(this);
            alertDialog.setOnCancelListener(this);
            show();
        }

        private AlertDialog buildAlertDialog(Context context) {
            return new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.dialog_create_network))
                    .setMessage("Enter network Name:")
                    .setView(editText)
                    .setNeutralButton(context.getString(R.string.create), null)
                    .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canceled = true;
                        }
                    })
                    .create();
        }

        public void show() {
            canceled = false;
            alertDialog.show();
        }

        @Override public void onDismiss(DialogInterface dialog) {
            if(!canceled) {

                final String networkName = editText.getText().toString().trim();

                if (networkName.equals("")) {
                    editText.setError("Network must have a name");
                    show();
                } else if (networkName.length() > 15 || networkName.length() < 3) {
                    editText.setError("Network Name must be between 3 and 15 characters");
                    show();
                }
                else{
                    //try to create network
                    createNetwork(networkName);
                }

            }
        }


    }

    private void createNetwork(String networkName) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(NETWORK_NAME,networkName);
        ParseCloud.callFunctionInBackground(CLOUD_CREATE_NETWORK,params,new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject o, ParseException e) {
                if(e == null){
                    //we have a network now save it
                    ParseRelation r = user.getRelation(NETWORKS);
                    r.add(o);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                //we have a problem
                            }
                        }
                    });
                   //Repopulate the list with new network
                    userNetworkListAdapter.add(o);
                    userNetworkListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}

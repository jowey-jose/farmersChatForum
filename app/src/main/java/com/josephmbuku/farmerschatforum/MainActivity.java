package com.josephmbuku.farmerschatforum;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private  int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"You are signed out of Farmers Pamoja Chat Forum!",Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //Once the user has signed in, main activity will receive a result inform of an intent. To handle it, you must override the onActivityResult() method.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the Result_OK is true means the user has signed in successfully. If so call the displayChatMessages() method again other wise call finish() to close the app.
        if (resultCode == SIGN_IN_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Snackbar.make(activity_main,"Successfully logged in Welcome to Farmers Pamoja Chat Forum!",Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else{
                Snackbar.make(activity_main,"Log in Not successful. Try Again Later ",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

            //you must get a DatabaseReference object using the getReference() method of the FirebaseDatabase class.
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                input.setText("");
            }
        });


        //check if not signed in, then navigate to the sign in button with an intent builder class .
        if (FirebaseAuth.getInstance().getCurrentUser()== null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }

        else{
            Snackbar.make(activity_main,"Welcome" +FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
        }



        //Load Content
        displayChatMessage();
    }

    private void displayChatMessage() {
        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference())
        {
            //FirebaseListAdapter is an abstract class and has an  abstract populateView() method which must be overridden.
            //populate views of each list Item. Acts an alternative to getView() method.
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) findViewById(R.id.message_text);
                messageUser = (TextView) findViewById(R.id.message_user);
                messageTime = (TextView) findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
        listOfMessage.setAdapter(adapter);

    }
}

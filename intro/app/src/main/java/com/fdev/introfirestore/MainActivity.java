package com.fdev.introfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText mTitleEnter;
    private EditText mEnterThought;
    private Button mBtnSave , mBtnShow , mBtnUpdate , mBtnDelete;
    private TextView mTextViewRetrieve;

    //Keys
    public static final String KEY_TITLE = "title";
    public static final String KEY_THOUGHT = "thought";

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference journalCollection = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitleEnter = findViewById(R.id.edit_text_title);
        mEnterThought = findViewById(R.id.edit_text_thought);
        mBtnSave = findViewById(R.id.btn_save);
        mTextViewRetrieve = findViewById(R.id.tv_retrieve);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleEnter.getText().toString();
                String thought = mEnterThought.getText().toString();

                Journal journal = new Journal(title,thought);

                journalCollection.add(journal);


            }
        });
        mBtnShow = findViewById(R.id.btn_show);
        mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               journalCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       if(!queryDocumentSnapshots.isEmpty()){
                           Toast.makeText(MainActivity.this,"Lagi retrieve",Toast.LENGTH_LONG)
                                   .show();
                           StringBuilder journals = new StringBuilder();
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                Journal journal =documentSnapshot.toObject(Journal.class);
                                journals.append(journal.getTitle() + "\n" + journal.getThought());
                                journals.append("\n\n");
                            }
                            mTextViewRetrieve.setText(journals);
                       }else{
                           Toast.makeText(MainActivity.this,"Data kosong",Toast.LENGTH_LONG)
                                   .show();
                       }
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Fail to retrieve all data",Toast.LENGTH_LONG)
                                .show();
                   }
               });
            }
        });

        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleEnter.getText().toString();
                //String thought = mEnterThought.getText().toString();

                Map<String , Object> data = new HashMap<>();
                data.put(KEY_TITLE,title);
                //data.put(KEY_THOUGHT,thought);

//                journalCollection.document()
            }
        });

        mBtnDelete = findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //journalRef.delete();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        journalCollection.addSnapshotListener(this ,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(MainActivity.this,"Lagi retrieve",Toast.LENGTH_LONG)
                            .show();
                    StringBuilder journals = new StringBuilder();
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Journal journal =documentSnapshot.toObject(Journal.class);
                        journals.append(journal.getTitle() + "\n" + journal.getThought());
                        journals.append("\n\n");
                    }
                    mTextViewRetrieve.setText(journals);
                }else{
                    Toast.makeText(MainActivity.this,"Data kosong",Toast.LENGTH_LONG)
                            .show();
                }
            }
            }
        );
    }
}

package com.example.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyshoppinglist.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Utilities;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab_btn;
    private static DatabaseReference mDatabase ;
    private FirebaseAuth mAuth ;
    private RecyclerView recyclerView ;
    private MyViewHolder myViewHolder;
    private FirebaseRecyclerAdapter adapter;
    private TextView totalSumResult ;
    private String sttotal ;
    private String post_key ;
    private String type ;
    private String note ;
    private int amonut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        totalSumResult = findViewById(R.id.right_total_amount);

        toolbar = findViewById(R.id.home_toolbar);
        fab_btn = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recyler_home);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalamount = 0 ;
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Data mData = data.getValue(Data.class);
                    totalamount += mData.getAmount();
                    sttotal = String.valueOf(totalamount+".00");
                }
                totalSumResult.setText(sttotal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialouge();
            }
        });
    }
    private void customDialouge(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater =LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.input_data , null);

        final EditText type = view.findViewById(R.id.edit_type);
        final EditText amount = view.findViewById(R.id.amount_quantity);
        final EditText note = view.findViewById(R.id.edit_note);
        final Button btn = view.findViewById(R.id.btn_save);

        final AlertDialog dialog = mydialog.create();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                int ammint = Integer.parseInt(mAmount);

                if(TextUtils.isEmpty(mType)){
                    type.setError("Required Filed");
                    return;
                }
                if(TextUtils.isEmpty(mAmount)){
                    type.setError("Required Filed");
                    return;
                }
                if(TextUtils.isEmpty(mAmount)){
                    type.setError("Required Filed");
                    return;
                }
                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mType,ammint,mNote,date,id);
                mDatabase.child(id).setValue(data);
                Toast.makeText(getApplicationContext() , "Added" , Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
    private void updateData (){
        AlertDialog.Builder updatedialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater updateinflater = LayoutInflater.from(HomeActivity.this);
        View updateview = updateinflater.inflate(R.layout.update_inputfield ,null);
        final AlertDialog mUpdateDialog = updatedialog.create();
        mUpdateDialog.setView(updateview);
        mUpdateDialog.show();

        final EditText edt_type = updateview.findViewById(R.id.edit_type);
        final EditText edt_amount = updateview.findViewById(R.id.amount_quantity);
        final EditText edt_note = updateview.findViewById(R.id.edit_note);

        edt_type.setText(type);
        edt_type.setSelection(type.length());
        edt_amount.setText(String.valueOf(amonut));
        edt_amount.setSelection(String.valueOf(amonut).length());
        edt_note.setText(String.valueOf(note));
        edt_note.setSelection(String.valueOf(note).length());

        Button update_btn = updateview.findViewById(R.id.btn_update);
        Button delete_btn = updateview.findViewById(R.id.btn_delete);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edt_type.getText().toString().trim();
                String uAmnount = edt_amount.getText().toString().trim();
                note = edt_note.getText().toString().trim();
                amonut = Integer.parseInt(uAmnount);
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(type,amonut,note,date,post_key);
                mDatabase.child(post_key).setValue(data);
                mUpdateDialog.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                mUpdateDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        FirebaseRecyclerOptions<Data> options  = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mDatabase ,Data.class).build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_data, parent, false);

                return new MyViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Data model) {
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());

                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        type = model.getType();
                        amonut = model.getAmount();
                        note = model.getNote();
                        updateData();
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
        }

        public void setType (String type){
            TextView mType = myview.findViewById(R.id.type);
            mType.setText(type);
        }
        public void setNote (String note){
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }
        public void setDate (String date){
            TextView mNote = myview.findViewById(R.id.date);
            mNote.setText(date);
        }
        public void setAmount (int amount){
            TextView mNote = myview.findViewById(R.id.amount);
            String stam = String.valueOf(amount);
            mNote.setText(stam);
        }
    }
}

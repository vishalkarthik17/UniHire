package com.example.unihire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class shortlistCandidatesClosedJobs extends AppCompatActivity {

    EditText NumOfCandidatesEditText;
    CheckBox Name,Email,PhoneNum,Location,ResumeURL;
    String SelectedFormat;
    int numberOfCandidates;
    boolean nameChecked,emailChecked,phoneNumChecked,locationChecked,resumeURLChecked;
    RadioButton csv,xlsx;
    RadioGroup rg;
    Button shorlist;
    FirebaseAuth fAuth;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortlist_candidates_closed_jobs);
        String JOBID = getIntent().getStringExtra("JOBID");

        ref=FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();

        NumOfCandidatesEditText=findViewById(R.id.NumOfCand);
        Name=findViewById(R.id.checkBoxNameShorlist);
        Email=findViewById(R.id.checkBoxEmailShorlist);
        PhoneNum=findViewById(R.id.checkBoxPhoneShorlist);
        Location=findViewById(R.id.checkBoxLocationShorlist);
        ResumeURL=findViewById(R.id.checkBoxResumeShorlist);
        rg=findViewById(R.id.radioGroupShorlist);
        shorlist=findViewById(R.id.shortlistBtn);
        csv=findViewById(R.id.csvDownload);
        xlsx=findViewById(R.id.xlsxDownload);
        csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedFormat= (String) csv.getText();
                Toast.makeText(shortlistCandidatesClosedJobs.this, SelectedFormat, Toast.LENGTH_SHORT).show();
            }
        });

        xlsx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedFormat=(String) xlsx.getText();
                Toast.makeText(shortlistCandidatesClosedJobs.this, SelectedFormat, Toast.LENGTH_SHORT).show();
            }
        });

        shorlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfCandidates=Integer.parseInt(NumOfCandidatesEditText.getText().toString());
                nameChecked=Name.isChecked();
                emailChecked=Email.isChecked();
                phoneNumChecked=PhoneNum.isChecked();
                locationChecked=Location.isChecked();
                resumeURLChecked=ResumeURL.isChecked();
                shorlistCandidates(JOBID);
            }
        });


    }
    public void shorlistCandidates(String JOBID){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String p1=snapshot.child("Job").child(JOBID).child("Priority1").getValue().toString();
                String p2=snapshot.child("Job").child(JOBID).child("Priority2").getValue().toString();
                String p3=snapshot.child("Job").child(JOBID).child("Priority3").getValue().toString();
                int w1=Integer.parseInt(snapshot.child("Job").child(JOBID).child("Weightage1").getValue().toString());
                int w2=Integer.parseInt(snapshot.child("Job").child(JOBID).child("Weightage2").getValue().toString());
                int w3=Integer.parseInt(snapshot.child("Job").child(JOBID).child("Weightage3").getValue().toString());

                if(p1.equals("Awards/Honors")) p1="Awards Honors";
                if(p2.equals("Awards/Honors")) p2="Awards Honors";
                if(p3.equals("Awards/Honors")) p3="Awards Honors";

                calculate(snapshot, p2, JOBID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void calculate(DataSnapshot snapshot,String p,String JOBID){
        //Toast.makeText(this, "Ulla Vanchu"+snapshot.child("Job").child(JOBID).child("jobID").getValue().toString(), Toast.LENGTH_SHORT).show();
        ArrayList<Integer> p1Marks=new ArrayList<>();
        if(p.equals("Education")){
            for(DataSnapshot dataSnapshot : snapshot.child("Application").getChildren()){
                if(dataSnapshot.child("JobID").getValue().toString().equals(JOBID)){
                    ArrayList<Integer> allMarks = new ArrayList<>();
                    int maxYearEdu=Integer.MIN_VALUE;
                    int maxYearIndexEdu=0;
                    int count=0;
                    for(DataSnapshot educationKey : dataSnapshot.child("Education").getChildren()){
                        allMarks.add(Integer.parseInt(educationKey.child("Marks").getValue().toString()));
                        if(Integer.parseInt(educationKey.child("ToYear").getValue().toString()) > maxYearEdu){
                            maxYearEdu=Integer.parseInt(educationKey.child("ToYear").getValue().toString());
                            maxYearIndexEdu=count;
                        }
                        count++;
                    }
                    int score=0;
                    int restOfMarks=0,recentMarks=0;
                    for(int i=0;i<allMarks.size();i++){
                        if(i!=maxYearIndexEdu)
                            restOfMarks+=allMarks.get(i);
                        else
                            recentMarks+=allMarks.get(i);
                    }
                    score=(recentMarks/2) + ((restOfMarks/(allMarks.size()-1)))/2;
                    Toast.makeText(this, "score : "+String.valueOf(score) , Toast.LENGTH_SHORT).show();
                    p1Marks.add(score);
                }
            }
        }

        //-------------------------------------------------------

        if(p.equals("Work Experience")){
            for(DataSnapshot dataSnapshot : snapshot.child("Application").getChildren()){
                if(dataSnapshot.child("JobID").getValue().toString().equals(JOBID)){
                    for(DataSnapshot workExKey : dataSnapshot.child("Work Experience").getChildren()){

                    }

                }
            }
        }


    }

}
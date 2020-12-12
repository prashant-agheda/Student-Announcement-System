package com.example.studentannouncementsystem.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentannouncementsystem.HomepageActivity;
import com.example.studentannouncementsystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private static final int IMG_REQUEST_ID = 1;

    ImageButton profilePicture;
    EditText userName;

    EditText name;
    EditText rollNo;
    EditText address;
    EditText course;
    EditText semester;
    EditText stream;
    Button updateDetailsButton;

    ImageView displayImage;
    TextView displayName;

    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference mStorageReference;
    private StorageReference reference;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomepageActivity) getActivity()).setActionBarTitle("Edit Profile");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userName = getView().findViewById(R.id.username);

        displayImage = getView().findViewById(R.id.displayImage);
        displayName = getView().findViewById(R.id.displayName);

        name = getView().findViewById(R.id.nameInputField);
        rollNo = getView().findViewById(R.id.rollNoInputField);
        address = getView().findViewById(R.id.addressInputField);
        course = getView().findViewById(R.id.courseInputField);
        semester = getView().findViewById(R.id.semesterInputField);
        stream = getView().findViewById(R.id.streamInputField);

        updateDetailsButton = getView().findViewById(R.id.updateProfileDataButton);

        profilePicture = getView().findViewById(R.id.profilePicture);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference("Picture").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        try {
            final File localfile = File.createTempFile("prashant","jpg");
            mStorageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            profilePicture.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            profilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImage();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Name")) {
                    name.setText(snapshot.child("Name").getValue().toString());
                }

                if (snapshot.hasChild("Roll No")) {
                    rollNo.setText(snapshot.child("Roll No").getValue().toString());
                }

                if (snapshot.hasChild("Address")) {
                    address.setText(snapshot.child("Address").getValue().toString());
                } else {
                    updateData();
                }

                if (snapshot.hasChild("Course")) {
                    course.setText(snapshot.child("Course").getValue().toString());
                }

                if (snapshot.hasChild("Semester")) {
                    semester.setText(snapshot.child("Semester").getValue().toString());
                }

                if (snapshot.hasChild("Stream")) {
                    stream.setText(snapshot.child("Stream").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to Get Data", Toast.LENGTH_SHORT).show();
            }
        });

        // Updating profile detail
        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(rollNo.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Roll No", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(address.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(course.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Course", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(semester.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Semester", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stream.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Stream", Toast.LENGTH_SHORT).show();
                } else {
                    updateData();
                    Toast.makeText(getActivity(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method for sending profile Detail data to database
    public void updateData() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        databaseReference.child("Roll No").setValue(rollNo.getText().toString());
        databaseReference.child("Address").setValue(address.getText().toString());
        databaseReference.child("Course").setValue(course.getText().toString());
        databaseReference.child("Semester").setValue(semester.getText().toString());
        databaseReference.child("Stream").setValue(stream.getText().toString());
        saveInFireBase();
    }

    public void requestImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMG_REQUEST_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQUEST_ID && resultCode == RESULT_OK && data!= null && data.getData() != null){
            imageUri = data.getData();
            try {
                Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                profilePicture.setImageBitmap(bitmapImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveInFireBase()
    {
        if(imageUri != null)
        {
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            StorageReference reference = storageReference.child("Picture/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            try {
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("saved " + (int) progress + "%");
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
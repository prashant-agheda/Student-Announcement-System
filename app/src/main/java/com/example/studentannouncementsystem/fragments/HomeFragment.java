package com.example.studentannouncementsystem.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.studentannouncementsystem.HomepageActivity;
import com.example.studentannouncementsystem.R;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> mAnnouncement = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    ArrayList<String> keyList =  new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((HomepageActivity) getActivity()).setActionBarTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.announcementListView);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Announcements");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mAnnouncement);
        listView.setAdapter(arrayAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                mAnnouncement.add("Announcement : " + value);
                keyList.add(snapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
                notification();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int index = keyList.indexOf(snapshot.getKey());
                mAnnouncement.remove(index);
                keyList.remove(index);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void notification()
    {
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(),"n")
                .setContentText("Announcement")
                .setSmallIcon(R.drawable.ic_menu_send)
                .setAutoCancel(true)
                .setContentText("You have new Announcement");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
        managerCompat.notify(999 ,builder.build());
    }
}
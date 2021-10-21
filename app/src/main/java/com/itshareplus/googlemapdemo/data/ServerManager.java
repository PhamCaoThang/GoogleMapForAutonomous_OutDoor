package com.itshareplus.googlemapdemo.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import Modules.Route;

import static android.content.ContentValues.TAG;

public class ServerManager {
    private static ServerManager instance;
//    private FirebaseFirestore db;
    private FirebaseDatabase database;

    public static ServerManager getInstance() {
        if(instance==null)
            instance = new ServerManager();
        return instance;
    }
    public void putData(List<Route> routes, final IServerManagerPutData iServerManagerPutData){
        if(database==null) {
//            db = FirebaseFirestore.getInstance();
            database = FirebaseDatabase.getInstance();
        }

        for (int i = 0; i <routes.size() ; i++) {
            // Từ A-B sẽ có nhiều steps và các steps sẽ có nhiều points
            String data = "";
            for (Route routeStep : routes.get(i).steps) {
                data += "{\"lat\":" + routeStep.startLocation.latitude + "," + "\"lon\":" + routeStep.startLocation.longitude + "," + "\"angle\":" +
                        routeStep.angle + "," + "\"dis\":" + routeStep.distance.value + "}#";
//                for (LatLng pointStep:routeStep.points) {
//                    data+="_"+pointStep.latitude+"@"+pointStep.longitude;
//                }
            }
            DatabaseReference myRef = database.getReference("path").child("route" + i);
            myRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    iServerManagerPutData.OnPutDataSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iServerManagerPutData.OnPutDataFail(e.toString());
                }
            });
        }
//            data+="_"+routes.get(i).endLocation.latitude+"@"+routes.get(i).endLocation.longitude;
//            Map<String,String> map = new HashMap<>();
//            map.put("Data",data);
//            db.collection("Path").document("route"+i).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    iServerManagerPutData.OnPutDataSuccess();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    iServerManagerPutData.OnPutDataFail(e.toString());
//                }
//            });
//            }


    }

    public void getData(final IServerManagerGetData iServerManagerGetData){
//        final List<String> listData = new ArrayList<>();
//        if(db==null)
//            db = FirebaseFirestore.getInstance();
//        /**
//         * Read data bình thường
//         */
////        db.collection("Path").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if (task.isSuccessful()) {
////                    for (QueryDocumentSnapshot document : task.getResult()) {
////                        listData.add(document.get("Data").toString());
////                        Log.d(TAG, "onComplete: "+document.get("Data").toString());
////                    }
////                    iServerManagerGetData.OnGetDataSuccess(listData);
////                } else {
////                    iServerManagerGetData.OnGetDataFail( task.getException().toString());
////                }
////            }
////        });
//        /**
//         * Read data realtime
//         */
//        db.collection("Path").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments()) {
//                    listData.add(document.get("Data").toString());
//                    Log.d(TAG, "onEvent: "+document.get("Data").toString());
//                }
//                if(listData.size()>0){
//                    iServerManagerGetData.OnGetDataSuccess(listData);
//                }else {
//                    iServerManagerGetData.OnGetDataFail(e.toString());
//                }
//            }
//        });
    }


    public interface IServerManagerPutData{
        void OnPutDataSuccess();
        void OnPutDataFail(String error);
    }
    public interface IServerManagerGetData{
        void OnGetDataSuccess(List<String> data);
        void OnGetDataFail(String error);
    }

}

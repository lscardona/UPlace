package edu.unicauca.uplace.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.unicauca.uplace.R;
import edu.unicauca.uplace.activities.AddPlaceActivity;
import edu.unicauca.uplace.activities.MainActivity;
import edu.unicauca.uplace.activities.PlaceDetailActivity;
import edu.unicauca.uplace.adapters.UPlaceAdapter;
import edu.unicauca.uplace.database.DatabaseHandler;
import edu.unicauca.uplace.models.UPlaceModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPlaces#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentPlaces extends Fragment {

    RecyclerView rv_places_list;
    FloatingActionButton fab_lugar;

    public static int ADD_PLACE_ACTIVITY_REQUEST_CODE = 1;
    public static String PLACE_DETAILS = "place_details";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPlaces.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPlaces newInstance(String param1, String param2) {
        FragmentPlaces fragment = new FragmentPlaces();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPlaces() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_places, container, false);


        //Enlazando vistas
        fab_lugar = vista.findViewById(R.id.fab_lugar);
        //Declarando Recyclerview
        rv_places_list = vista.findViewById(R.id.rv_places_list);

        //Intent Lugar
        fab_lugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(vista.getContext(), AddPlaceActivity.class);
                startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE);
            }

        });


        //Obteniendo Lugares de la BD
        getPlacesFromDB();

        // Inflate the layout for this fragment
        return vista;
    }

    private void getPlacesFromDB() {
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        ArrayList<UPlaceModel> places = dbHandler.getPlaces();

        if (places.size() > 0) {
            rv_places_list.setVisibility(View.VISIBLE);
            addPlacesToRV(places);
        }
    }


    public void addPlacesToRV(ArrayList<UPlaceModel> places) {
        rv_places_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_places_list.setHasFixedSize(true);
        UPlaceAdapter placesAdapter = new UPlaceAdapter(getContext(),places);
        rv_places_list.setAdapter(placesAdapter);

        placesAdapter.setOnClickListener(new UPlaceAdapter.onClickListener() {
            @Override
            public void onClick(int position, UPlaceModel model) {
                Intent intent = new Intent(getContext(),PlaceDetailActivity.class);
                intent.putExtra(PLACE_DETAILS,model);
                startActivity(intent);
            }
        });

        EditCallback editSwipeHandler =  new EditCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                UPlaceAdapter adapter = (UPlaceAdapter) rv_places_list.getAdapter();
                adapter.notifyEditItem(getActivity(),viewHolder.getAdapterPosition(), ADD_PLACE_ACTIVITY_REQUEST_CODE);
            }
        };

        //Funcionalidad para editar item en swipe
        ItemTouchHelper editItemTouchHelper = new ItemTouchHelper(editSwipeHandler);
        editItemTouchHelper.attachToRecyclerView(rv_places_list);

        DeleteCallback deleteSwipeHandler =  new DeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                UPlaceAdapter adapter = (UPlaceAdapter) rv_places_list.getAdapter();
                adapter.removeAt(viewHolder.getAdapterPosition());

                //verificar la base de datos y actualizar recyclerview
                getPlacesFromDB();
            }
        };

        ItemTouchHelper deleteItemTouchHelper = new ItemTouchHelper(deleteSwipeHandler);
        deleteItemTouchHelper.attachToRecyclerView(rv_places_list);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getPlacesFromDB();
            }else{
                Log.e("Activity ","Actividad Cancelada o se presiono el back button");
            }

        }
    }

}
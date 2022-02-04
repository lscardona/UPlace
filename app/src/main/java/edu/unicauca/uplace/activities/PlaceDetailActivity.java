package edu.unicauca.uplace.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import edu.unicauca.uplace.R;
import edu.unicauca.uplace.models.UPlaceModel;
import edu.unicauca.uplace.utils.FragmentPlaces;

public class PlaceDetailActivity extends AppCompatActivity {


    //Views
    AppCompatImageView iv_place_image;
    TextView tv_description;
    TextView tv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);


        //Enlazar views a variables
        iv_place_image = findViewById(R.id.iv_place_image);
        tv_description = findViewById(R.id.tv_description);
        tv_location = findViewById(R.id.tv_location);

        //Crear varialble uPlaceModel
        UPlaceModel uPlaceModel = null;

        //Obtener extras del intent
        if(getIntent().hasExtra(FragmentPlaces.PLACE_DETAILS)){
            uPlaceModel = (UPlaceModel) getIntent().getSerializableExtra(FragmentPlaces.PLACE_DETAILS);
        }


        if(uPlaceModel != null){

            //crear toolbar
            Toolbar toolbar_place_details = findViewById(R.id.toolbar_place_details);
            setSupportActionBar(toolbar_place_details);
            getSupportActionBar().setTitle(uPlaceModel.title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar_place_details.setNavigationOnClickListener(view -> onBackPressed());

            //asignar informacion  del modelo a views
            iv_place_image.setImageURI(Uri.parse(uPlaceModel.image));
            tv_description.setText(uPlaceModel.description);
            tv_location.setText(uPlaceModel.location);


        }



    }


}
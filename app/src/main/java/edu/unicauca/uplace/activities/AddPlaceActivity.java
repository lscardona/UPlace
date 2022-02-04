package edu.unicauca.uplace.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.unicauca.uplace.R;
import edu.unicauca.uplace.database.DatabaseHandler;
import edu.unicauca.uplace.models.UPlaceModel;
import edu.unicauca.uplace.utils.FragmentPlaces;


public class AddPlaceActivity extends AppCompatActivity implements  View.OnClickListener  {

    //Variables [Codigos de estado para el metodo activityForResult]
    static final int GALLERY = 1;
    static final int CAMERA = 2;
    static final String IMAGE_DIRECTORY = "UPlaceImagenes";
    static final int PLACE_AUTOCOMPLETE_REQUEST_CODE =3;

    //Views []
    TextInputEditText et_title;
    TextInputEditText et_description;
    TextInputEditText et_location;
    TextInputEditText et_date;
    TextView tv_add_image;
    AppCompatImageView iv_place_image;
    Button btn_save;

    //DatePickerDialog: Para mostrar fecha
    DatePickerDialog dateSetListener;

    //Instancia de Calendar y obtener fecja actual
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);

    //guardamos una Referencia a la imagen, latitud, longitud
    private Uri saveImageToInternalStorage = null;
    private Double mLatitude = 0.0;
    private Double mLongitude = 0.0;

    //bandera para el edit state
    UPlaceModel uPlaceDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        //Enlazar vistas
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        et_location = findViewById(R.id.et_location);
        et_date = findViewById(R.id.et_date);
        tv_add_image = findViewById(R.id.tv_add_image);
        iv_place_image = findViewById(R.id.iv_place_image);
        btn_save = findViewById(R.id.btn_save);


        //Codigo Toolbar [Se agrega a la vista AddPlace y se crea el botón retroceso]
        Toolbar toolbar_add_place = findViewById(R.id.toolbar_add_place);
        setSupportActionBar(toolbar_add_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar Lugar");
        toolbar_add_place.setNavigationOnClickListener(view -> onBackPressed());

        //Obtener extras del intent [solo si existe]
        //Se agrega getSerializable para poder manejar el ArrayList
        if(getIntent().hasExtra(FragmentPlaces.PLACE_DETAILS)){
            uPlaceDetails = (UPlaceModel) getIntent().getSerializableExtra(FragmentPlaces.PLACE_DETAILS);
        }

        //Si se agregan extras al intent [Se EDITA un lugar]
        if(uPlaceDetails != null){

            //Configurar Titulo de Toolbar
            getSupportActionBar().setTitle("Editar Lugar");

            //Enlazar informacion del objeto UPlaceDetails a los views
            et_title.setText(uPlaceDetails.title);
            et_description.setText(uPlaceDetails.description);
            et_date.setText(uPlaceDetails.date);
            et_location.setText(uPlaceDetails.location);
            mLatitude = uPlaceDetails.latitude;
            mLongitude = uPlaceDetails.longitude;
            saveImageToInternalStorage = Uri.parse(uPlaceDetails.image);
            iv_place_image.setImageURI(saveImageToInternalStorage);
            btn_save.setText("ACTUALIZAR");
        }


        //Actualizar fecha en view
        updateDateInView(year,month,day);

        //Asignar Listener a et_date
        et_date.setOnClickListener(this);
        //Asignar Listener a tv_add_image
        tv_add_image.setOnClickListener(this);
        //Asignar Listener a btn_save
        btn_save.setOnClickListener(this);
        //Asignar Listener a  et_location
        et_location.setOnClickListener(this);


        //CODIGO NECESARIO PARA API GOOGLE MAPS [Crea un cliente Places]
        if(!Places.isInitialized()){
            Places.initialize(AddPlaceActivity.this, getResources().getString(R.string.google_maps_key));
            // Create a new PlacesClient instance
            PlacesClient placesClient = Places.createClient(this);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            //Se agrega la fecha
            case R.id.et_date:{
                dateSetListener = new DatePickerDialog(AddPlaceActivity.this, (datePicker, year, month, day) -> updateDateInView(year,month,day), year, month, day);
                dateSetListener.show();
                break;
            }

            //Se agrega la imagen
            case R.id.tv_add_image: {

                //Crear AlertDialog para las dos opciones de agregar imagenes
                String pictureDialogItems[] = {"Elegir Foto De La Galeria","Tomar Una Imagen Con La Camara"};
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPlaceActivity.this);
                pictureDialog.setTitle("Elige Una Opción").setItems(pictureDialogItems, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:{choosePhotoFromGallery();break;}
                                case 1:{takePhotoFromCamera();break; }
                            }
                    }
                });
                //Mostrar AlertDialog
                AlertDialog builder = pictureDialog.create();
                builder.show();
                break;
            }

            //Se pulsa el boton de GUARDAR
            case R.id.btn_save:{
                //Verificar que todos los campos esten llenos
                if(et_title.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ingrese un titulo", Toast.LENGTH_SHORT).show();
                }else if(et_description.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ingrese una descripcion", Toast.LENGTH_SHORT).show();
                } else if(et_location.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ingrese una Ubicacion", Toast.LENGTH_SHORT).show();
                } else if(saveImageToInternalStorage == null){
                    Toast.makeText(getApplicationContext(), "Por Favor Ingrese Una Imagen", Toast.LENGTH_SHORT).show();
                }else{
                    //Si todos los campos estan llenos se crea el objeto UPlaceModel
                    //UPlaceModel almacena la información ingresada por el usuario en esta actividad
                    UPlaceModel uPlaceModel = new UPlaceModel(
                            uPlaceDetails == null? 0 : uPlaceDetails.id,
                            et_title.getText().toString(),
                            saveImageToInternalStorage.toString(),
                            et_description.getText().toString(),
                            et_date.getText().toString(),
                            et_location.getText().toString(),
                            mLatitude,
                            mLongitude
                            );

                    Toast.makeText(getApplicationContext(), "Modelo creado", Toast.LENGTH_SHORT).show();

                    //[Base de datos]

                    //Se crea el handler de SQLite para realizar operaciones con la BD
                    DatabaseHandler dbHandler = new DatabaseHandler(this);


                    if(uPlaceDetails == null){
                        //Si se desea guardar un nuevo registro

                        long dbResult = dbHandler.addPlace(uPlaceModel);
                        Toast.makeText(getApplicationContext(), "Añadido a la BD "+dbResult, Toast.LENGTH_SHORT).show();

                        if(dbResult > 0 ){
                            setResult(Activity.RESULT_OK);
                            finish();
                        }

                    }else{
                        //Si se desea actualizar un registro existente
                        long dbResult = dbHandler.updatePlace(uPlaceModel);
                        Toast.makeText(getApplicationContext(), "Actualizada BD "+dbResult, Toast.LENGTH_SHORT).show();

                        if(dbResult > 0 ){
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }
                }
                break;
            }

            //Se presiona el EditText ubicación [Google Maps API]
            case R.id.et_location:{
                try{
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                            .build(this);
                    startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }catch (Exception e){
                    Log.e("img_saved_camera","Algo Fallo");
                    e.printStackTrace();
                }
            }
        }
    }

    //Codigo para actualizar fecha en la vista
    public void updateDateInView(int year, int month, int day){
        calendar.set(year,month,day);
        String format = "dd.MM.yyyy";
        SimpleDateFormat  sdf = new SimpleDateFormat(format, Locale.getDefault());
        et_date.setText(sdf.format(calendar.getTime()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    Uri contentURI = data.getData();

                    try {
                        Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap);
                        Log.e("img_saved_gallery",""+saveImageToInternalStorage);
                        iv_place_image.setImageBitmap(selectedImageBitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Fallo al cargar la imagen desde la galeria",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if(requestCode == CAMERA){
                Bitmap thumbnail  = (Bitmap) data.getExtras().get("data");
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail);
                Log.e("img_saved_camera",""+saveImageToInternalStorage);
                iv_place_image.setImageBitmap(thumbnail);
            }else if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    et_location.setText(place.getAddress());
                    mLatitude = place.getLatLng().latitude;
                    mLongitude = place.getLatLng().longitude;
                }else{
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.e("info", status.getStatusMessage());
                }

            }

        }
    }


    //Codigo para tomar una foto desde la camara
    public void takePhotoFromCamera(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    Toast.makeText(getApplicationContext(),
                            "Permisos READ/WRITE concedidos. Ahora puedes seleccionar una imagen",
                            Toast.LENGTH_SHORT).show();

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAMERA);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                showRationaleDialogForPermissions();
            }
        }).onSameThread().check();
    }

    //Elegir Foto de la galeria
    public void choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    Toast.makeText(getApplicationContext(),
                            "Permisos READ/WRITE concedidos. Ahora puedes seleccionar una imagen",
                            Toast.LENGTH_SHORT).show();

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent,GALLERY);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                showRationaleDialogForPermissions();
            }
        }).onSameThread().check();

    }

    //Mostrar dialogo cuando se acepten los permisos
    public  void showRationaleDialogForPermissions(){
        Toast.makeText(getApplicationContext(),
                "Permisos READ/WRITE no concedidos. Intenta de nuevo",
                Toast.LENGTH_SHORT).show();
    }

    //Guardar imagen en el almacenamiento del telefono
    public Uri saveImageToInternalStorage(Bitmap bitmap){
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        file = new File(file, UUID.randomUUID()+".jpg");


        try {
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return Uri.parse(file.getAbsolutePath());
    }
}
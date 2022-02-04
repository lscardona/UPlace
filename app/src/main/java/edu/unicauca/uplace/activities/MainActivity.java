package edu.unicauca.uplace.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import edu.unicauca.uplace.R;

import edu.unicauca.uplace.utils.FragmentColaboradores;
import edu.unicauca.uplace.utils.FragmentPlaces;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FragmentPlaces fragmentPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtener cardviews [Activan Los RecyclerView]
        CardView card_home = findViewById(R.id.cardView);
        CardView card_colaboradores = findViewById(R.id.cardView2);

        //Instanciar Fragment y añadirlo al fragmentManager
        fragmentPlaces = new FragmentPlaces();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragmentPlaces).commit();

        //Configurar Listeners para los Cardviews
        card_home.setOnClickListener(this);
        card_colaboradores.setOnClickListener(this);

    }


    //Evento de los botones para cambiar de fragment.
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentColaboradores fragmentColaboradores = new FragmentColaboradores();

        switch (view.getId()){
            case R.id.cardView: {
                transaction.replace(R.id.contenedorFragment,fragmentPlaces);
                break;
            }
            case R.id.cardView2:{
                transaction.replace(R.id.contenedorFragment,fragmentColaboradores);
                break;
            }
        }
        transaction.commit();
    }
}
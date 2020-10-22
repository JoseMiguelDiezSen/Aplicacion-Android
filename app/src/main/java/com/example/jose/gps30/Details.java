package com.example.jose.gps30;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jose on 02/06/2018.
 */

public class Details extends Activity{

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ctx = getApplicationContext();

    Intent intent = getIntent();
    int numeroLista = intent.getIntExtra("numLista", 0);

    TextView txtTitulo = (TextView) this.findViewById(R.id.txtTitulo);
        //txtTitulo.setText(Mark.getTitulo(numeroLista));

    TextView txtDirector = (TextView) this.findViewById(R.id.txtDirector);
        //txtDirector.setText(Mark.getDirector(numeroLista));

    ImageView imagenPelicula = (ImageView) findViewById(R.id.imagenPelicula);


        if (numeroLista == 0) {
        //imagenPelicula.setImageResource(R.drawable.alien);
            txtTitulo.setText("España");
            txtDirector.setText("Leon");

    } else if (numeroLista == 1) {
        //imagenPelicula.setImageResource(R.drawable.resplandor);
    } else if (numeroLista == 2) {
        //imagenPelicula.setImageResource(R.drawable.jurassic_park);
    } else if (numeroLista == 3) {
        //imagenPelicula.setImageResource(R.drawable.interstellar);
    } else if (numeroLista == 4) {
        //imagenPelicula.setImageResource(R.drawable.fuga_de_alcatraz);
    } else if (numeroLista == 5) {
        //imagenPelicula.setImageResource(R.drawable.marte);
    } else if (numeroLista == 6) {
        //imagenPelicula.setImageResource(R.drawable.origen);
    } else if (numeroLista == 7) {
        //imagenPelicula.setImageResource(R.drawable.gravity);
    }

}

}

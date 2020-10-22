package com.example.jose.gps30;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;

public class ListMarkers extends Activity {
    ListView list;
    Context ctx;
    int numeroLista;
    int itemPos;
    ArrayList datos = new ArrayList<Marker>();
    ArrayAdapter <String> adapter;
    Address address;
    String pais;
    String provincia;
    String localidad;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listmarkers);
        ctx = getApplicationContext();
        list = (ListView) findViewById(R.id.list);

        //Mark.removeAll(Mark);
        //Consultamos Mark guardados en la tabla
        SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);
        ArrayList<String> datos = sqLiteHelper.sacarDatos();

        //Comprobamos array
        if (datos.size() > 0) {
            StringBuffer stringBuffer1 = new StringBuffer();
            stringBuffer1.append("Nº Items en Array: " + datos.size());
            Toast.makeText(getApplicationContext(), stringBuffer1.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No tienes marcadores", Toast.LENGTH_LONG).show();
        }

        // Creamos el adaptador para convertir el array en views
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        // Añadimos el adapter al listview
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        // Comprobamos los cambios realizados en el adapter
        adapter.notifyDataSetChanged();

        //Listener para recoger un long pressed
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Toast.makeText(getApplicationContext(), "Long pressed..." + pos, Toast.LENGTH_SHORT).show();
                //Cogemos la posicion del item seleccionado
                itemPos = pos;
                StringBuffer stringBuffer1 = new StringBuffer();
                stringBuffer1.append("Pos del Item: " + itemPos);
                Toast.makeText(getApplicationContext(), stringBuffer1.toString(), Toast.LENGTH_LONG).show();
                onCreateDialog();
                return true;
            }
        });

        //Listener para recoger que item se ha presionado
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              /*Intent intent = new Intent(ctx,Details.class);
                intent.putExtra("numLista",numeroLista);
                startActivity(intent);*/
                numeroLista = position;
                StringBuffer stringBuffer1 = new StringBuffer();
                stringBuffer1.append("Pos de Item: " + numeroLista);
                Toast.makeText(getApplicationContext(), stringBuffer1.toString(), Toast.LENGTH_LONG).show();
                }
            };//Asignamos el listener al listView.
            list.setOnItemClickListener(listener);
        }

    public void onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListMarkers.this);
        builder.setTitle("¿Eliminar?");
        //builder.setMessage("¿Eliminar?");
        builder.setIcon(R.drawable.ic_papelera)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Eliminamos registro indicado de la base de Mark
                        SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);

                        //Eliminamos registro seleccionado
                        sqLiteHelper.eliminar(itemPos+1);

                        guardarArray();
                        sqLiteHelper.restoreTable();
                        cargarArray();

                        //Volvemos a coger los Mark de memoria
                        datos = sqLiteHelper.sacarDatos();
                        list = (ListView) findViewById(R.id.list);
                        // Creamos el adaptador para convertir el array en views
                        adapter = new ArrayAdapter <String>(ListMarkers.this, android.R.layout.simple_list_item_1, datos);
                        //Actualizamos listview
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        }

    public void guardarArray(){
        final SharedPreferences sharedPref = getSharedPreferences("user1",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            //Consultamos el contenido de la base de datos y se lo pasamos al ArrayList markerList
            SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);
            datos = sqLiteHelper.sacarDatos();
            //Guardamos en preferencias
            editor.putString("datos", ObjectSerializer.serialize(datos));
            Toast.makeText(getApplicationContext(),"Array guardado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Fallo al guardar", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
    }

    public void cargarArray(){
        final SharedPreferences sharedPref = getSharedPreferences("user1",Context.MODE_PRIVATE);
        try {
            //Recojo arraylist de preferencias
            datos = (ArrayList) ObjectSerializer.deserialize(sharedPref.getString("Mark", ObjectSerializer.serialize(new ArrayList())));

            StringBuffer stringBuffer1 = new StringBuffer();
            stringBuffer1.append("DATOS CARGADOS: " +datos+"\n");
            Toast.makeText(getApplicationContext(), stringBuffer1.toString(), Toast.LENGTH_LONG).show();

            //Extraer infomacion de arraylist
            for(int i = 0;i < datos.size();i++)
                {
                //pais = datos.get(i).getPais();
                //provincia = datos.get(i).getProvincia();
                //localidad = datos.get(i).getLocalidad();

                //Insertar datos en la base de datos
                //SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);
                //sqLiteHelper.insertar(pais,provincia,localidad);
                }

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Fallo al cargar", Toast.LENGTH_LONG).show();
        }
    }
}
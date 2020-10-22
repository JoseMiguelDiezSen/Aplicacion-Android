package com.example.jose.gps30;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener,OnStreetViewPanoramaReadyCallback {

    //DECLARACION DE VARIABLES
    GoogleMap mMap;
    Context ctx;
    LocationManager locationManager;
    double latitude; //Variable para almacenar la latitud
    double longitude; //Variable para almacenar la longitud
    private static final int MY_PERMISSIONS_REQUEST = 0;//Variable asignada a la solicitud de permisos
    LatLng miPosicionActual; //Recogera la posicion actual
    Location location;//Location encapsula los Mark principales de una dirección geográfica
    private EditText mSearchText;//editText de la busqueda
    Address address;
    Address address2;
    Address address3;
    StreetViewPanorama mSvpView;
    Marker marker;
    Marker marker2;
    ArrayList markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ctx = getApplicationContext();
        mSearchText = (EditText) findViewById(R.id.textoCaja);

        //markerList.removeAll(markerList);

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Ponemos el logo en la actividad principal
        ActionBar actionBar1 = getActionBar();
        actionBar1.setIcon(R.drawable.ic_logo);
        actionBar1.setDisplayShowHomeEnabled(true);
        init();

        //Cargamos la ventana de Street View
        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        //Cargamos los Mark guardados de los markers
        cargarArray();

        //Comprobamos que se carga el array
        StringBuffer stringBufferInfo = new StringBuffer();
        stringBufferInfo.append("Array cargado: \n");
        stringBufferInfo.append("Tamaño array:" + markerList.size());
        Toast.makeText(getApplicationContext(), stringBufferInfo.toString(), Toast.LENGTH_LONG).show();
       }

    public void cargarArray(){ //Cargamos el arraylist en preferencias
        final SharedPreferences sharedPref = getSharedPreferences("user",Context.MODE_PRIVATE);
        try {
            markerList = (ArrayList) ObjectSerializer.deserialize(sharedPref.getString("markerList", ObjectSerializer.serialize(new ArrayList())));
            Toast.makeText(getApplicationContext(),"Array cargado", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Fallo al cargar", Toast.LENGTH_LONG).show();
        }
    }

    public void guardarArray(){//Salvar array marketList en preferencias
        final SharedPreferences sharedPref = getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            //Consultamos el contenido de la base
            SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);
            //Se lo pasamos al ArrayList markerList
            markerList = sqLiteHelper.sacarDatos();
            //markerList --> Guardado en prefrencias
            editor.putString("markerList", ObjectSerializer.serialize(markerList));
            Toast.makeText(getApplicationContext(),"Array guardado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Fallo al guardar", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
    }

        /*if(se toca la foto)
            {
            ViewGroup.LayoutParams params = streetViewPanoramaFragment.getView().getLayoutParams();
            params.height = 900;
            params.width = 800;
            streetViewPanoramaFragment.getView().setLayoutParams(params);
            }*/

    private void init()//Método que espera a que el usuario introduzca una localizacion para ser buscada
        {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override//Pulsacion de la tecla IR , Buscar ,Hecho
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Toast.makeText(getApplicationContext(), "Buscando direccion...", Toast.LENGTH_SHORT).show();
                //LLamamos al metodo de busqueda
                getDireccion();
                return false;
                }
            });
        }

    public void getDireccion()//Método que solo se llama cuando vamos a realizar alguna busqueda
        {
        //Recogemos en la variable el texto introducido
        String direccionAbuscar = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(direccionAbuscar, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            address = list.get(0);
            Marker marker = mMap.addMarker(new MarkerOptions().draggable(true)
                    .title("Pais:" + address.getCountryName())
                    .snippet("Ciudad:" + address.getLocality())
                    .position(new LatLng(address.getLatitude(), address.getLongitude())));

            marker.showInfoWindow();
            /*Cambiar vista Street al buscar en el texto
            LatLng posicionStretView = new LatLng(address2.getLatitude(),address2.getLongitude());
            mSvpView = new StreetViewPanorama(this, new StreetViewPanoramaOptions().position(posicionStretView));
            */



            Toast.makeText(getApplicationContext(), "Direccion encontrada:\n" + address.getLocality() + "\n" + address.getAdminArea() + "\n" + address.getCountryName(), Toast.LENGTH_LONG).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_KEYS_SHORTCUT);
        } else {
            Toast.makeText(getApplicationContext(), "Direccion no encontrada ", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para mover la camara a la posicion introducida en el buscador
    public void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)//Metodo que se carga cuando el mapa esta listo
    {
        //Una vez cargado el mapa llamamos a get location para recoger lat y long
        getLocation();
        mMap = googleMap;
        //En esta vble almacenamos la localización(lat,long)
        miPosicionActual = new LatLng(latitude, longitude);
        //Añadimos un marker en la posicion solicitada
        mMap.addMarker(new MarkerOptions().position(miPosicionActual).title("Jose Is Here!!").snippet("MyPosition").alpha(0.8f)
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.yo_48x53)));

        //Movemos la camara a la posicion indicada
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicionActual));
        //Configuramos que el mapa inicie en modo normal,no satelite ni nada
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //obtenemos los botones del zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Desactivamos el seleccionador de niveles
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //Eventos de Click a la escucha
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
        } else {
            //Visibilizar mi ubicacion
            mMap.setMyLocationEnabled(true);
        }
    }

    public void getLocation() //Metodo para obtener la posicion
    {//Si no tenemos  los permisos concedidos los volvemos a solicitar
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
        } else {//Si tenemos los permisos accedemos a la localizacion
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);

            if (location != null) {//Si hay permisos y ademas tenemos la localizacion
                //Almaceno coordenadas en las vbles
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {//Si no hay Mark de posicion informamos al usuario
                Toast.makeText(getApplicationContext(), "No hay acceso a la posicion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    LocationListener locationListener = new LocationListener() //Metodo que recoge los cambios de posicion del usuario
    {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getApplicationContext(), "Actualizando posicion...", Toast.LENGTH_SHORT).show();
            //Cuando cambie la localizacion actualizamos la longitud y la latitud
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //?¿?Es necesaria esta linea¿?
            miPosicionActual = new LatLng(latitude, longitude);
        }

        @Override//nos avisará de cambios de estado.
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override//Contario al posterior, nos avisará cuando el proveedor esté activado.
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override//nos avisará cuando el proveedor se desactive por cualquier motivo.
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }
    };

    @Override//Metodo que gestiona la solicitud de permisos del usuario
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // Si la solicitud es cancelada el resultado del array estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    Toast.makeText(getApplicationContext(), "Permiso concedido.Gracias", Toast.LENGTH_SHORT).show();
                } else {
                    // Persmiso denegado
                    Toast.makeText(getApplicationContext(), "Debes aceptar los permisos", Toast.LENGTH_SHORT).show();
                }
                //getLocation();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)//Oversize menu
    {// Inflate the Menu; Esto añade items en la action bar, si esta presente
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override //Opciones en Oversize menu
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        //Abrimos activity con lisview de los marcadores guardados
        if (id == R.id.listMarkers) {
            Intent intent2 = new Intent(getApplicationContext(),ListMarkers.class);
            startActivity(intent2);
        }

        //Lugares curiosos
        if (id == R.id.lugares_curiosos) {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng( -27.585490, -66.305299))
                    .title("Marcas Extrañas en la tierra")
                    .snippet("Area Administrativa: ")
                    .alpha(1.0f)
                    //.icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.yo_48x53)));

            marker2 = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng( -97.585490, -36.305299))
                    //.title("Pais :" + address3.getCountryName())
                    //.snippet("Area Administrativa: " + address3.getAdminArea())
                    .alpha(1.0f)
                    .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));

        }

        //Seleccion de mapa
        if (id == R.id.ajusteMapa) {
            final CharSequence[] items = {"Hibrido", "Satelite", "Tierra", "Personalizado"};
            //Abrimos una ventana emergente
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("Elige el tipo de mapa");
            builder.setIcon(R.drawable.ic_map);
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

    public void onClick(DialogInterface dialog, int item) {
         //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
               if (item == 0) {
                   mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
               }

               if (item == 1) {
                   mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               }

               if (item == 2) {
                   mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
               }

               if (item == 3) {
                   mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.mapstyle));
               }
         }
    });

            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        //Opcion Salir
        if (id == R.id.salir) {
            finish();
            Toast.makeText(getApplicationContext(), "Vuelve pronto!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        double lat = point.latitude;
        double lng = point.longitude;

        //Cogemos las coordenadas y se las pasamos a Geocoder para poder obtener información sobre la posicion
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        List <Address> markers = new ArrayList<>();
        try {
            markers = geo.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Marcador no añadido", Toast.LENGTH_SHORT).show();
        }

        if (markers.size() > 0) {
            address2 = markers.get(0);
            if (address2.getCountryName() != null) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("Pais :" + address2.getCountryName())
                        .snippet("Area Administrativa: " + address2.getAdminArea())
                        .alpha(1.0f)
                        .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
            Toast.makeText(getApplicationContext(), "Marcador añadido", Toast.LENGTH_SHORT).show();

            //Metemos el marker en la base de datos
            SQLiteHelper sqLiteHelper = new SQLiteHelper(ctx);
            sqLiteHelper.insertar(address2.getCountryName(),address2.getAdminArea(),address2.getLocality());

            StringBuffer stringBuffer1 = new StringBuffer();
            markerList = sqLiteHelper.sacarDatos();
            stringBuffer1.append("Nº de marcadores añadidos: " + markerList.size() + "\n");
            stringBuffer1.append("Contenido: " + marker.toString() + "\n");
            Toast.makeText(getApplicationContext(), stringBuffer1.toString(), Toast.LENGTH_LONG).show();
            }
        }
    else{
        Toast.makeText(getApplicationContext(), "Zona no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    boolean viewControls = false;
    @Override
    public void onMapClick(LatLng point) {
            RelativeLayout r = (RelativeLayout) findViewById(R.id.cajon);

            if (!viewControls)
            {
            //Ocultamos los controles:zoom, posicion ,searchBox
            r.setAlpha(0);
            mSearchText.setVisibility(View.INVISIBLE);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            viewControls = true;
            }

         else if(viewControls)
            {
            //Mostramos los controles:zoom, posicion ,searchBox
            r.setAlpha(1);
            mSearchText.setVisibility(View.VISIBLE);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            viewControls = false;
            }
        }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        //mSvpView = new StreetViewPanoramaView(this, new StreetViewPanoramaOptions().position(SAN_FRAN));
    }

    @Override
    public void onStop() {
        //Cuando la aplicacion(actividad principal) pasa a estar en segundo plano dejaremos de actualizar las coordenadas
        locationManager.removeUpdates(locationListener);
        guardarArray();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    }











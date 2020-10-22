package com.example.jose.gps30;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class Legend extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leyenda);

        ActionBar actionBar1 = getActionBar();
        actionBar1.setLogo(R.drawable.ic_action_name);
        actionBar1.setDisplayUseLogoEnabled(true);
        actionBar1.setDisplayShowHomeEnabled(true);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Pestaña 1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("General");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Jeny");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("jose");
        host.addTab(spec);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menuleyenda, menu);
        return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Volver al mapa
        if(id == R.id.retorno)
            {
            Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
            startActivity(intent);
            }
        return super.onOptionsItemSelected(item);
        }

}



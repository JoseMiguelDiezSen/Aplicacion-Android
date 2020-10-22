package com.example.jose.gps30;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper
    {
    public SQLiteHelper(Context ctx) {super(ctx, "myMarkersThirtySeven.db",null, 1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table if not exists Marcadores (id integer primary key autoincrement, pais text, area text, localidad text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists Marcadores");
        onCreate(db);
        }

    public void insertar(String pais,String areaAdmin, String localidad)
        {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("pais", pais);
        contentValues.put("area", areaAdmin);
        contentValues.put("localidad", localidad);
        database.insert("Marcadores", null, contentValues);
        }

    //Metodo para EXTRAER Mark
    public ArrayList<String> sacarDatos()
        {
         ArrayList <String> lista = new ArrayList<>();
         SQLiteDatabase database = this.getReadableDatabase();
         Cursor cursor = database.rawQuery("SELECT * FROM Marcadores", null);

         //int cont =1;

         if (cursor.moveToFirst()) {
             do {
                 int id = cursor.getInt(cursor.getColumnIndex("id"));
                 String pais = cursor.getString(cursor.getColumnIndex("pais"));
                 String areaAdminist = cursor.getString(cursor.getColumnIndex("area"));
                 String localidad = cursor.getString(cursor.getColumnIndex("localidad"));
                 lista.add("Mark " + id/*cont*/ + "\nPais: " + pais + " Estado: " + areaAdminist + " Localidad: " + localidad + "\n");
                 //cont++;
                 }
             while (cursor.moveToNext());
             }
         return lista;
         }

    public void eliminar(int id) {
         SQLiteDatabase db = this.getWritableDatabase();
         db.delete("Marcadores","id = "+id,null);
         db.close();
         }

    //Metodo para regenerar la tabla
    public void restoreTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Drop table if exists Marcadores");
        db.execSQL("Create Table if not exists Marcadores (id integer primary key autoincrement, pais text, area text, localidad text)");
        db.close();
        }


    }
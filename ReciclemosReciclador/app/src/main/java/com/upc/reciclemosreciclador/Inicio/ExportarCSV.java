package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ExportarCSV {

    public void exportar(Context context){
        float totalplastico, totalpapelcarton;

        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor filaUsuario = db.rawQuery("select nombre, apellido, asociacion_name, distrito_name from Reciclador", null);
        filaUsuario.moveToFirst();

        //GENERAR DATA
        StringBuilder REPORTE = new StringBuilder();
        REPORTE.append("Reciclador," + filaUsuario.getString(0) + " " + filaUsuario.getString(1) + "\n");
        REPORTE.append("Asociación," + filaUsuario.getString(2) + "\n");
        REPORTE.append("Distrito," + filaUsuario.getString(3) + "\n");

        REPORTE.append("\n" + "Distrito,Condominio,Plástico,Papel/Cartón");

        Cursor filsaDistrito = db.rawQuery("select distrito from Condominio", null);
        filsaDistrito.moveToFirst();

        ArrayList<String> distritos = new ArrayList<>();
        do{
            if(!distritos.contains(filsaDistrito.getString(0)))
                distritos.add(filsaDistrito.getString(0));
        }while(filsaDistrito.moveToNext());

        for (String s: distritos) {
            REPORTE.append("\n" + s);

            Cursor filaCondominio = db.rawQuery("select nombre, codigo from Condominio where distrito = '" + s + "'", null);
            filaCondominio.moveToFirst();

            do{
                totalplastico = 0;
                totalpapelcarton = 0;
                REPORTE.append(" ,");
                REPORTE.append(filaCondominio.getString(0) + ",");

                Cursor filaBolsa = db.rawQuery("select codigo from Bolsa where condominio = " + filaCondominio.getInt(1), null);
                filaBolsa.moveToFirst();

                do{
                    Cursor filaProbolsa = db.rawQuery("select peso, producto from Probolsa where bolsa = " + filaBolsa.getString(0), null);
                    filaProbolsa.moveToFirst();

                    do{
                        Cursor filaProducto = db.rawQuery("select categoria from Producto where codigo = " + filaProbolsa.getString(1), null);
                        filaProducto.moveToFirst();

                        switch (filaProducto.getInt(0)){
                            case 1:
                                totalplastico = totalplastico + filaProbolsa.getInt(0);
                                break;
                            case 3:
                                totalpapelcarton = totalpapelcarton + filaProbolsa.getInt(0);
                                break;
                        }
                    }while(filaProbolsa.moveToNext());

                }while(filaBolsa.moveToNext());

                REPORTE.append((totalplastico/1000.0) + " kg," + (totalpapelcarton/1000.0) + " kg \n");

            }while(filaCondominio.moveToNext());
        }

        try{
            //saving the file into device
            FileOutputStream out = context.openFileOutput("REPORTE-" + filaUsuario.getString(0) + " " + filaUsuario.getString(1) + ".csv", Context.MODE_PRIVATE);
            out.write((REPORTE.toString()).getBytes());
            out.close();

            //exporting
            File filelocation = new File(context.getFilesDir(), "REPORTE-" + filaUsuario.getString(0) + " " + filaUsuario.getString(1) + ".csv");
            Uri path = FileProvider.getUriForFile(context, "com.upc.reciclemosreciclador.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);

            List<ResolveInfo> resolvedIntentActivities = context.getPackageManager().queryIntentActivities(fileIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "REPORTE-" + filaUsuario.getString(0) + " " + filaUsuario.getString(1));
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            context.startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

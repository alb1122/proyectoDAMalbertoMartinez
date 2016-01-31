package com.org.alb.ikaslast;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.org.alb.ikaslast.MainActivity.alias;
import static com.org.alb.ikaslast.MainActivity.grupòId;
import static com.org.alb.ikaslast.MainActivity.gtemp;

;

/**
 * Created by alb on 11/01/2016. este es dialog para mostrar, elegir y crear nuevos grupos
 */


public class mDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    String[] listitems = {"Imposible Conectar al server..."};
    String nuevoGrupo="";

    ListView mylist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Elija un Grupo donde competir...");
        View view = inflater.inflate(R.layout.grupos_layout, container, false);
        try {
            GruposParse gp= new GruposParse();
            List<String> lg=gp.recuperarGrupos();
            if (lg.size()!=0){
            listitems=new String[lg.size()+1];
                listitems[0]="+ Crear nuevo Grupo";
                for(int i=0; i<lg.size(); i++){

                    listitems[i+1]= lg.get((i));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mylist = (ListView) view.findViewById(R.id.list);



        return view;
    }

    private void guardarRegistro() {

        for (int x=0;x<listitems.length;x++){
            if (listitems[x].equalsIgnoreCase(nuevoGrupo)){
                Toast.makeText(getActivity().getBaseContext(),"Ese grupo ya existe", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }

        try {

            ParseObject fiGrupo = new ParseObject("Grupos");


            fiGrupo.put("NombreGrupo", nuevoGrupo);

            gtemp=nuevoGrupo;
            fiGrupo.saveInBackground();

        }catch (Exception e) {
            Toast.makeText(getActivity().getBaseContext(),"No se pudo guardar el registro", Toast.LENGTH_SHORT)
                    .show();
        }
        ((MainActivity)getActivity()).actualizarAppBar();
        dismiss();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==0){
            Toast.makeText(getActivity(), listitems[position], Toast.LENGTH_SHORT)
                    .show();
            crearGrupoDialog();
            return;
        }
        Toast.makeText(getActivity(), listitems[position], Toast.LENGTH_SHORT)
                .show();
        String gp=gtemp;
        gtemp="Anonimo";
        gtemp=listitems[position];
        //guardarRegistro();
        if (alias==null){
            ((MainActivity)getActivity()).introducirAlias();
        }else if (gp.equalsIgnoreCase(gtemp)==false){
            Toast.makeText(getActivity(), "grupo modificado...", Toast.LENGTH_SHORT)
                    .show();
            actualizarGrupo();
        }
        ((MainActivity)getActivity()).mostrarDatos();

        dismiss();
    }
    public void actualizarGrupo(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Usuarios");

// Retrieve the object by id
        query.getInBackground(grupòId, new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    gameScore.put("Grupo", gtemp);

                    gameScore.saveInBackground();

                }
            }
        });
        Toast.makeText(getActivity(), "grupo actualizado...", Toast.LENGTH_SHORT)
                .show();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adaptador=new ArrayAdapter<String>(getActivity(),R.layout.fila_layout,R.id.txtitem,listitems);

        mylist.setAdapter(adaptador);

        mylist.setOnItemClickListener(this);

    }
    //por su propio nombre se define
    public void crearGrupoDialog(){
        final EditText input = new EditText(((MainActivity)getActivity()));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
      //  alertDialog.setView(input);
        new AlertDialog.Builder(((MainActivity)getActivity()))

                .setTitle("Crear Grupo")
                .setMessage("Introduzca nombre del nuevo Grupo")
                        // .setView(input)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable editable = input.getText();
                        nuevoGrupo=editable.toString();


                        guardarRegistro();





                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.

                    }
                }).show();

    }
}

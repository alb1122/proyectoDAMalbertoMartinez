package com.org.alb.ikaslast;

import com.org.alb.ikaslast.logger.Log;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alb on 11/01/2016. clase para manejar los grupos
 */
public class GruposParse {

    List<String> lr;

    public GruposParse() throws ParseException {
        lr = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Grupos");
        // query.whereContains("Grupo","?");
        query.selectKeys(Arrays.asList("NombreGrupo"));
        List<ParseObject> listaParse = query.find();
        // lr = query.find();
        if (listaParse.size() != 0) {
            Log.d("score", "Nº " + listaParse.size() + " Grupos");
            for (int x = 0; x < listaParse.size(); x++) {
               // lr.add(listaParse.get(x).getString("Grupo"));
                if (!lr.contains(listaParse.get(x).getString("NombreGrupo"))) {
                    lr.add(listaParse.get(x).getString("NombreGrupo"));

                }
            }
        }


    }


    public List<String> recuperarGrupos() {


        return lr;
    }
}
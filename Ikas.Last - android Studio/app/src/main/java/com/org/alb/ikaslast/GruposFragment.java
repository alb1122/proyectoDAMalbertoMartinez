package com.org.alb.ikaslast;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by alb on 10/01/2016.
 */
public class GruposFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.grupos_layout,container,false);
        String[] gruposEjemplos={"claseA","claseB","Curro"};
        ArrayAdapter<String> adaptador=new ArrayAdapter<String>(getActivity(),R.layout.fila_layout,R.id.txtitem,gruposEjemplos);
        setListAdapter(adaptador);
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewGroup viewGroup=(ViewGroup) v;
        TextView txt=(TextView)viewGroup.findViewById(R.id.txtitem);
        Toast.makeText(getActivity(),txt.getText().toString(),Toast.LENGTH_LONG).show();

    }
}

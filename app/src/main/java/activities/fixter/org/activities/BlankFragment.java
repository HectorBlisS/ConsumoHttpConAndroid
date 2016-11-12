package activities.fixter.org.activities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    public BlankFragment() {
        // Required empty public constructor
    }

    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    } //onCreate

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.refresh2){
            consumoTask task = new consumoTask();
            task.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_blank, container, false);

        String[] data = {
                "BlisS",
                "Héctor",
                "Héctor BlisS",
                "Narcicismo",
                "porque",
                "ps",
                "yolo",
                "mijos",
                "t(*_*t)"
        };

        List<String> fakeData = new ArrayList<String>(Arrays.asList(data));

        //definimos el adapter
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.el_item,R.id.elTexto,fakeData);

        //Inflamos la lista
        ListView listView = (ListView) rootView.findViewById(R.id.laLista);
        listView.setAdapter(adapter);

        //retornamos elr rootView (inflar el fragment)
        return rootView;
    }

    public class consumoTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            Log.v("Mensajito: ","Comenzando");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStringResponse = null;

            try{
                //el url de nuestra API o servidor
                URL url = new URL("https://agile-thicket-30819.herokuapp.com/api/vacantes/");

                //Abrimos la coneccion con el servidor
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Leemos la respuesta del servidor
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                //esto se encarga de leer la respuesta  ue se encuentr en el inputStream
                reader = new BufferedReader(new InputStreamReader(inputStream));

                //Aqui leemos linea por linea
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                //seguridad
                if (buffer.length() == 0){
                    return null;
                }
                 //alfin tenemos la respuesta hay que guardrla en un string
                jsonStringResponse = buffer.toString();
                Log.v("Respuesta: ",jsonStringResponse);

            }catch (IOException e){
                Log.e("error","Error ",e);
                return null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch(final IOException e){
                        Log.e("error","Error cerrando",e);
                    }
                }
            } //finally

            //Aqui vamos a parsear la respuesta del servidor
            try{
                return getDataFromJson(jsonStringResponse);
            }catch(JSONException e){
                Log.e("error",e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }

        private String[] getDataFromJson(String jsonStringResponse)
        throws JSONException{
             //Declaramos variables globales para no hardcodear
            final String FIELDS = "fields";
            final String VACANTE = "puesto_solicitante";

            //convertimos y parseamos
            JSONArray todo = new JSONArray(jsonStringResponse);
            //lista final vacia
            String[] resultStrs = new String[todo.length()];

            //hago una iteración para conseguir lo que quiero #mujeres
            for (int i=0;i<todo.length();i++){

                JSONObject elemento = todo.getJSONObject(i);
                JSONObject fields = elemento.getJSONObject(FIELDS);
                String vacante = fields.getString(VACANTE);

                resultStrs[i] = vacante;
            }

            return resultStrs;
        } //parser

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                adapter.clear();
                adapter.addAll(result);
            }
        }
    } //task

}

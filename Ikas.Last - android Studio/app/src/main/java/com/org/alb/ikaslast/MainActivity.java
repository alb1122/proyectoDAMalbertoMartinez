package com.org.alb.ikaslast;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.plus.Plus;
import com.org.alb.ikaslast.logger.Log;
import com.org.alb.ikaslast.logger.LogView;
import com.org.alb.ikaslast.logger.LogWrapper;
import com.org.alb.ikaslast.logger.MessageOnlyLogFilter;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView timerValue;
    private TextView tiempoActividad;
    private TextView usuarioTextView;
    private TextView datosTextView;
    private TextView datosGruposTextView;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    Calendar chrono;
    long mTime, thisTime;
    float tiempoActividad_minutos;
    static long timeInMilliseconds = 0L;
    static long timeSwapBuff = 0L;
    static long updatedTime = 0L;
    boolean comenzar = false;
    boolean continuar = true;
    //fecha android para parse
    static long ahora;
    static boolean nuevo = false;
    Date fecha;
    String fechaR;
    boolean res = false;
    static long millis;
    //VARIABLES GOOGLE API
    public static final String TAG = "IKAS.Api";
    private static final int REQUEST_OAUTH = 1;
    private static TextView tvDistancia;

    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;
    static float distanciaRecorrida = 0;
    private Toolbar toolbar;
    private LogView logView = null;
    private ScrollView scrollView;
    static int horaR = 0, minR = 0, secR = 0;
    static String grupo;
    static String grupòId;
    MenuItem menuItem,menuItem2;
    static String gtemp, usuario, alias;
    private ImageView img;

    //este metodo es para volver al oncreate... asi el registro de metros no falla tanto, tambien lo uso para deterner animacion y aplicación
    public void actualizarAppBar() {

        this.recreate();

    }


    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.fitnessc);
        view = getCurrentFocus();
        img = (ImageView) findViewById(R.id.img);
        chrono = Calendar.getInstance();
        thisTime = chrono.getTimeInMillis();
        grupo = gtemp;
        usuarioTextView = (TextView) findViewById(R.id.tvUsuario);
        datosTextView = (TextView) findViewById(R.id.tvDatos);
        datosGruposTextView = (TextView) findViewById(R.id.tvDatos2);
        tiempoActividad = (TextView) findViewById(R.id.tvTiempo);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.play);

        fab.clearAnimation();

        //comportamiento del FabButton para iniciar la captura de datos, con sus booleanas
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!comenzar) {
                    chrono = Calendar.getInstance();
                    mTime = chrono.getTimeInMillis();
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    fab.setImageResource(R.drawable.stop);
                    toolbar.setSubtitle("" + chrono.get(Calendar.DAY_OF_MONTH) + "/" + (chrono.get(Calendar.MONTH) + 1) + "/" + chrono.get(Calendar.YEAR) + " Inicio: " + chrono.get(Calendar.HOUR_OF_DAY) + "H" + chrono.get(Calendar.MINUTE) + "m");
                    customHandler.postDelayed(updateTimerThread, 1000);
                    comenzar = true;
                    distanciaRecorrida = 0;
                    tvDistancia.setText("...esperando actividad");


                } else {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Terminar Session")
                            .setMessage("esta seguro de Terminar actividad?")

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // deal with the editable


                                    comenzar = false;
                                    fab.setImageResource(R.drawable.play);
                                    customHandler.removeCallbacks(updateTimerThread);
                                    mClient.disconnect();
                                    continuar = true;
                                    actualizarAppBar();

                                    guardarRegistro();


                                    distanciaRecorrida = 0;

                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                    continuar = true;
                                    Log.i(TAG, "continuar= " + continuar);
                                }
                            }).show();


                    Log.i(TAG, "continuar= " + continuar);


                }


            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!comenzar) {
                   // timerValue.setText("Pulse Play");
                    distanciaRecorrida = 0;
                    tvDistancia.setText("Actividad detenida...");
                    continuar = true;
                }
                return true;
            }
        });

        tvDistancia = (TextView) findViewById(R.id.tvDistancia);
        initializeLogging();
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        //aqui la creacion del cliente de la Api y su conexion
        buildFitnessClient();
        mClient.connect();
    }
        //muestra los datos recuperados de parse el alias y el grupo
    public void mostrarDatos() {
        // toolbar.setSubtitle("Alias: " + alias + " Grupo: " + gtemp);
        datosTextView.setText("Alias: " + alias);
        datosGruposTextView.setText("Grupo: " + gtemp);
    }
    //comprueba si el cliente tiene Alias asignado o es incorrecto
    public boolean comprobarAlias() {

        if (alias.length() < 3) {

            Toast.makeText(getApplicationContext(), "el debe de ser mayor de 3 caracteres...", Toast.LENGTH_SHORT)
                    .show();
            introducirAlias();
            res = false;
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("alias", alias);
            ParseCloud.callFunctionInBackground("aliasUsuario", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean aBoolean, ParseException e) {
                    if (e == null) {
                        if (!aBoolean) {
                            Toast.makeText(getApplicationContext(), "alias correctamente guardado... ", Toast.LENGTH_SHORT)
                                    .show();
                            guardarDatosUsuario();
                            res = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "el alias ya existe  Elija otro ", Toast.LENGTH_SHORT)
                                    .show();
                            introducirAlias();
                            res = false;
                        }

                    }

                }

           /* @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    String u=String.valueOf(parseObjects.size());
                    // ratings is 4.5
                    // String usu = parseObjects.get(0).getString("NombreUsuario");
                    Toast.makeText(getApplicationContext(), u, Toast.LENGTH_SHORT)
                            .show();
                    if (parseObjects.size()==0){
                        preguntarElegirGrupo();
                        // introducirAlias();
                        nuevo=true;

                    }else {
                        nuevo=false;
                        alias=parseObjects.get(0).getString("Alias");
                        gtemp=parseObjects.get(0).getString("Grupo");
                        grupòId=parseObjects.get(0).getObjectId();
                        mostrarDatos();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudieron recuperar datos registro", Toast.LENGTH_SHORT)
                            .show();
                }
            }*/


            });
        }


        return res;
    }
    //alertdialog para introducir datos
    public void introducirAlias() {
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        //  alertDialog.setView(input);
        new AlertDialog.Builder(MainActivity.this)

                .setTitle("Alias de usuario")
                .setMessage("Introduzca Alias del Usuario")
                        // .setView(input)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable editable = input.getText();
                        alias = editable.toString().toLowerCase();

                        ;

                        Toast.makeText(getApplicationContext(), alias, Toast.LENGTH_LONG)
                                .show();


                        if (nuevo) {
                            if (comprobarAlias() == true) {
                                Toast.makeText(getApplicationContext(), "estoy aqui", Toast.LENGTH_SHORT)
                                        .show();
                                guardarDatosUsuario();
                            }

                        }


                        mostrarDatos();


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                        alias = usuario;
                        mostrarDatos();
                        if (nuevo) {
                            guardarDatosUsuario();
                        }

                    }
                }).show();

    }

    //guardar datos del registro, OJO no actualizado , creo nuevo registro para tener distintas sesiones del mismo dia (si, es mas complejo, mas facil hubiera sido actualizar)
    public void guardarDatosUsuario() {
        try {

            ParseObject datosUser = new ParseObject("Usuarios");

            datosUser.put("NomUsuario", usuario);
            datosUser.put("Grupo", gtemp);
            datosUser.put("Alias", alias.toLowerCase());
            datosUser.saveInBackground();
            Toast.makeText(getApplicationContext(), "datosUser guardadado ok: " + " user: " + usuario + " Grupo:" + gtemp + " Alias: " + alias, Toast.LENGTH_LONG)
                    .show();
            nuevo = false;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No se pudo guardar el registro " + e.getMessage() + usuario + " " + grupo + " " + alias, Toast.LENGTH_SHORT)
                    .show();
        }


    }
    //recupero datos, llamo a una funcion del parse.cloud para ello
    public void recuperarDatos() {
        if (gtemp == null) {

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("usuario", usuario);
            ParseCloud.callFunctionInBackground("datosUsuario", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        String u = String.valueOf(parseObjects.size());

                        Toast.makeText(getApplicationContext(), u, Toast.LENGTH_SHORT)
                                .show();
                        if (parseObjects.size() == 0) {
                            preguntarElegirGrupo();
                            // introducirAlias();
                            nuevo = true;

                        } else {
                            nuevo = false;
                            alias = parseObjects.get(0).getString("Alias");
                            gtemp = parseObjects.get(0).getString("Grupo");
                            grupòId = parseObjects.get(0).getObjectId();
                            mostrarDatos();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudieron recuperar datos registro", Toast.LENGTH_SHORT)
                                .show();
                    }
                }


            });

        } else {
            mostrarDatos();
        }



    }

    //si el cliente no esta en un grupo...
    private void preguntarElegirGrupo() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Elegir Grupo")
                .setMessage("Desea Eligir un grupo")

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        activarMenu();


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                        gtemp = "Anonimo";
                        grupo = gtemp;
                        Log.i(TAG, "No elige grupoo ");
                        String titulo = (String) toolbar.getTitle();
                        toolbar.setTitle(titulo + " " + grupo);
                        datosGruposTextView.setText("Grupo: " + grupo);
                        if (alias == null) {
                            introducirAlias();
                        }
                    }
                }).show();


    }

    //llamada al menuItem grupos
    private void activarMenu() {


        if (menuItem != null) {
            onOptionsItemSelected(menuItem);
        }


    }
    //para guardar el registro creado , compruebo si ha recorrido distancia
    private void guardarRegistro() {

        long t = millis / (1000);//guardo segundos
        ahora = System.currentTimeMillis();
        fecha = new Date(ahora);
        // DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String salida = df.format(fecha);
        try {

            ParseObject fitRegistro = new ParseObject("FitRegistro");

            fitRegistro.put("Segundos", t);


            fitRegistro.put("Usuario", usuario);
            if (alias == null) {
                alias = usuario;
            }
            fitRegistro.put("Alias", alias);

            fitRegistro.put("Distancia", distanciaRecorrida);
            if (gtemp == null) {
                gtemp = "Anonimo";
            }
            fitRegistro.put("Grupo", gtemp);
            fitRegistro.put("FechaReg", salida);
            if (distanciaRecorrida > 0 && t > 0) {
                fitRegistro.saveInBackground();
                Toast.makeText(getApplicationContext(), "reg guardadado ok: " + " user: " + usuario + " Alias:" + alias + " Grupo:" + gtemp + " segundos: " + (t) + " dist: " + distanciaRecorrida, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "reg  no guardadado: " + " segundos: " + (t) + " dist: " + distanciaRecorrida, Toast.LENGTH_LONG)
                        .show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No se pudo guardar el registro " + e.getMessage() + usuario + grupo, Toast.LENGTH_SHORT)
                    .show();
        }


        distanciaRecorrida = 0;
    }

    // Create the Google API Client
    private void buildFitnessClient() {

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))


                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                // Log.i(TAG, "Connected!!!" + mClient.getSessionId());
                                final String accountName = Plus.AccountApi.getAccountName(mClient);
                                Log.i(TAG, "#onConnected - GoogleApiClient accountName=" + accountName);
                                usuario = accountName;

                                //  para obtener la cuenta
                                usuarioTextView.setText(accountName);
                                recuperarDatos();
                                findFitnessDataSources();


                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }

                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                        Log.i(TAG, "Attempting to resolve failed connection" + result.toString());


                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }

    // [START find_data_sources]
    private void findFitnessDataSources() {

        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)

                        // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_DERIVED)//EL DISTANCE DELTA ES TYPE_DERIVED

                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)
                                    && mListener == null) {
                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_DISTANCE_DELTA);
                                // scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            } else if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE) && mListener == null) {
                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_CUMULATIVE found!  Registering.");
                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            } else if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mListener == null) {
                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");
                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    long now;
    //recogida de dataPoints y runOnUiThread para actualizar el texview
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                if ((dataPoint.getDataSource().getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) && comenzar) {

                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value val = dataPoint.getValue(field);

                        Log.i(TAG, "Detected DataPoint field: " + field.getName());
                        Log.i(TAG, "Detected DataPoint value: " + val);
                        now = System.currentTimeMillis();
                        now += 500;
                        distanciaRecorrida = distanciaRecorrida + val.asFloat();
                        Log.i(TAG, "Distancia recorrida: " + distanciaRecorrida + " (metros)");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    tvDistancia.setText("Distancia = " + String.format("%.1f", distanciaRecorrida) + " (metros)");

                                }
                            });


                            now -= System.currentTimeMillis();
                            Log.i(TAG, "now animation: " + now);
                            img.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((AnimationDrawable) img.getBackground()).start();
                                }
                            });





                    }
                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(5, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");

                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }

    // cronometro para mostrar sus datos
    private Runnable updateTimerThread = new Runnable() {

        public void run() {


            final long start = mTime;
            chrono = Calendar.getInstance();
            long stopTime = chrono.getTimeInMillis();
            long millisT = stopTime - (start);
            millis = millisT;
            int seconds = (int) (millisT / 1000);
            float t = seconds;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hour = minutes / 60;
            minutes = minutes % 60;

            tiempoActividad.setText("" + String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            minR = minutes;
            secR = seconds;
            horaR = hour;



            customHandler.postDelayed(this, 1000);


        }


    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_settings);

        menuItem2 = menu.findItem(R.id.web);
        
        return true;
    }
    //dialogo para elejir grupo
    public void showDialog(View view) {

        FragmentManager manager = getFragmentManager();

        mDialogFragment dialog = new mDialogFragment();

        dialog.show(manager, "Elija un Grupo");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {


            showDialog(view);

        //link a la WEB
        } else if (id == R.id.web) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ikaslast.parseapp.com"));
            startActivity(browserIntent);
        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();

                }
            }
        }
    }



    private void unregisterFitnessDataListener() {
        if (mListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mClient,
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }
    //recogida del log
    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        //logView = (LogView) findViewById(R.id.sample_logview);

        //logView.setBackgroundColor(Color.CYAN);

        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");

    }
}

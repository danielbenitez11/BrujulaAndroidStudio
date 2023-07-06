package com.example.pmdm_t4_brujula;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor acelerometro, magnometro;
    private TextView gradosText;
    private TextView direccion;
    private ImageView rosaDeLosVientos;

    //Utilizamos estos array para realizar bien la medida de la brujula
    private float[] ultimoAcelerometro = new float[3];
    private float[] ultimoMagnometro = new float[3];
    private float[] rotacion = new float[9];
    private float[] orientacion = new float[9];


    //Validar los array conpletos del acelerometro
    boolean acelerometroarray = false;
    boolean magnometroarray = false;

    long tiempoExacto = 0;
    private float grados = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Creamos los sensores
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        gradosText = this.findViewById(R.id.grados);
        direccion = this.findViewById(R.id.orientacion);
        rosaDeLosVientos = this.findViewById(R.id.rosa);


        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        //Copia los valores del array del acelerometro y el magnometro para validar los datos almacenados
        if (sensorEvent.sensor == acelerometro) {
            System.arraycopy(sensorEvent.values, 0, ultimoAcelerometro, 0, sensorEvent.values.length);
            acelerometroarray = true;

        }
        if (sensorEvent.sensor == magnometro) {
            System.arraycopy(sensorEvent.values, 0, ultimoMagnometro, 0, sensorEvent.values.length);
            magnometroarray = true;

        }

        if (acelerometroarray && magnometroarray && System.currentTimeMillis() - grados > 360) {
            //Obtenemos el array del acelemetro y del magnometro para obtener la rotacion con el SensorManager
            SensorManager.getRotationMatrix(rotacion, null, ultimoAcelerometro, ultimoMagnometro);
            SensorManager.getOrientation(rotacion, orientacion);

            //Calculamos la posicion del objeto primero en radianes y lo pasamos a grados
            float posicionRadianes = orientacion[0];
            float posicionGrados = (float) Math.toDegrees(posicionRadianes);
            posicionGrados = (posicionGrados + 360) % 360;//Mide hasta 360º

            gradosText.setText((int) posicionGrados + "º");
            direccionesBasicas(posicionGrados);

            //Establecemos la animacion de rotacion
            RotateAnimation rotacion = new RotateAnimation(grados, -posicionGrados, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotacion.setDuration(250);
            rotacion.setFillAfter(true);
            rosaDeLosVientos.setAnimation(rotacion);

            //Mide los grados exactos
            grados = -posicionGrados;
            tiempoExacto = System.currentTimeMillis();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    //Segun la posicion de los grados calculada estableceremos una direccion especifica
    public void direccionesBasicas(float posicionGrados) {
        if (posicionGrados >= 0 && posicionGrados < 30) {
            direccion.setText("N");
        } else if (posicionGrados > 30 && posicionGrados < 60) {
            direccion.setText("NE");
        } else if (posicionGrados > 60 && posicionGrados < 90) {
            direccion.setText("E");
        } else if (posicionGrados > 120 && posicionGrados < 150) {
            direccion.setText("SE");
        } else if (posicionGrados > 150 && posicionGrados < 210) {
            direccion.setText("S");
        } else if (posicionGrados > 210 && posicionGrados < 240) {
            direccion.setText("SW");
        } else if (posicionGrados > 240 && posicionGrados < 300) {
            direccion.setText("W");
        } else if (posicionGrados > 300 && posicionGrados < 330) {
            direccion.setText("NW");
        }
    }
}
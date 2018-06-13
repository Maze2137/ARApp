
package com.lycha.example.augmentedreality;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.lang.StrictMath.abs;

public class CameraViewActivity extends Activity implements
        SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener{

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraviewOn = false;
    private AugmentedPOI mPoi;

    private double mAzimuthReal = 0;
    private double mAzimuthTeoretical = 0;
    private static double AZIMUTH_ACCURACY = 5;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    private List<String> POInames = new ArrayList<>();
    private List<String> POIdesc = new ArrayList<>();
    private List<Double> POIlat = new ArrayList<>();
    private List<Double> POIlon = new ArrayList<>();

    TextView descriptionTextView;
    ImageView pointerIcon;
    TextView descriptionText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupListeners();
        setupLayout();
        //1
        POInames.add("Metro Politechnika");
        POIdesc.add("Stacja o typowym wyglądzie lecz niestety wiecznie z nie działającymi schodami elektrycznymi i z wyjątkowymi tłumami ludzi w godzinach szczytu i nie tylko.");
        POIlat.add(52.219734);
        POIlon.add(21.015301);
        //2
        POInames.add("Gmach Główny PW");
        POIdesc.add("Organizacja sekretariatu to istne kuriozum, sama platforma jakoś działa, jednak załatwienie sobie jakiejkolwiek sprawy znajduje się gdzieś pomiędzy niemożliwym, a niemożliwym. Bardzo słaba wizytówka tak renomowanej uczelni");
        POIlat.add(52.220424);
        POIlon.add(21.010781);
        //3
        POInames.add("Tramwaj Plac Politechniki");
        POIdesc.add("Codziennie przechodząc tamtędy do pracy rzygać się chce, kiedy pod drugą wiatą widzi się kilku bezdomnych meneli zalanych w trupa. kilkudniowy smród moczu i kału jaki tam jest nie da się wytrzymać. Służby nic z tym nie robią. Niby straż miejska czasami zagląda ale tam chyba musiałby stanąć ktoś z karcherem, albo armatką wodę, żeby przepędzać tych śmierdzących, napitych, oszczanych i obsranych żuli.");
        POIlat.add(52.220106);
        POIlon.add(21.011091);
        //4
        POInames.add("Subway");
        POIdesc.add("Proszę o sałatkę , obsługa zaczyna robić kanapkę. Po upomnieniu z wyraźnym niezadowolenie zdejmuje dodatki wrzuca do pudełka w taki sposób że pol kurczaka wpada do pojemnika na odpadki skąd jest następnie wyciągnięty by znow wylądować w mojej sałatce. Po kolejnym upomnieniu uprzejma pani z obsługi postanowiła więcej już nie przykładać ręki do mojej sałatki prosząc kolegę obok o pomoc. Kolega nakładając dwie oliwki i kolejne dwa plasterki cebuli, nawet nie zapytałaby o przyprawy podaje pudełko do kasy . Dno!");
        POIlat.add(52.219743);
        POIlon.add(21.012968);
        //5
        POInames.add("Quick Point");
        POIdesc.add("Jedzenie jak na swoją cene smaczne. Szynka w zapiekance jest tą prawdziwą, a pomidor w hot dogu świeży. Tylk podejście do higieny...Na moich oczach Pani przyjęła pieniądze, pośliniła palce (żeby papierek od hot doga otworzyć), po czym ruszyła aby nimi szyneczke na zapiekanke nakładać.... i to wszystko tymi samymi brudnymi rękoma. Brud z pieniędzy, ślina pracownicy i to wszystko za 5PLN. Suuuuuper :) oby tak dalej. Gratuluje");
        POIlat.add(52.220835);
        POIlon.add(21.011116);
        //6
        POInames.add("Instytut Techniki Cieplnej PW");
        POIdesc.add("Najgorsze miejsce jakie widziałem na świecie, smród, syf , przez godzinę dwie wychudzone zaćpane cioty pseudo dresy i czterech namolnych starych dziadów, obsługa chamska i wulgarna, strach cos pic w barze bo dziadki mówili że dolewają kropli.  Uważajcie na to miejsce");
        POIlat.add(52.219837);
        POIlon.add(21.010026);
        //7
        POInames.add("Ulica Lwowska");
        POIdesc.add("Niesamowite miejsce, niesamowity obiekt. Jeżeli będziecie mieli okazję, warto zwiedzić...");
        POIlat.add(52.220381);
        POIlon.add(21.012221
        );

        setNearestPoi(); //wstepne ustawienie najbliższego poja, pozniej bedzie wywolywane z poziomu lisenerow
    }

    private void setNearestPoi(){
        int nearest = 0;
        Double tempAzValue = 0.0;
        Double PoiAzimuth = 0.0;
        ArrayList<Double> currAzimuth = new ArrayList<>();
        for(int i = 0; i < POInames.size(); i++){
            PoiAzimuth = calculateAzimuth(POIlat.get(i), POIlon.get(i));
            tempAzValue = abs(mAzimuthReal - PoiAzimuth);
            currAzimuth.add(tempAzValue);
        }
        nearest = currAzimuth.indexOf(Collections.min(currAzimuth));
        setAugmentedRealityPoint(nearest);
    }

    private void setAugmentedRealityPoint(Integer num) {
        mPoi = new AugmentedPOI(
                POInames.get(num),
                POIdesc.get(num),
                POIlat.get(num),
                POIlon.get(num)
        );
    }

    public double calculateAzimuth(Double lat, Double lon) {
        double dX = lat - mMyLatitude;
        double dY = lon - mMyLongitude;

        double phiAngle;
        double tanPhi;
        double azimuth = 0;

        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if (dX > 0 && dY > 0) { // I quater
            return azimuth = phiAngle;
        } else if (dX < 0 && dY > 0) { // II
            return azimuth = 180 - phiAngle;
        } else if (dX < 0 && dY < 0) { // III
            return azimuth = 180 + phiAngle;
        } else if (dX > 0 && dY < 0) { // IV
            return azimuth = 360 - phiAngle;
        }

        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;
        List<Double> minMax = new ArrayList<>();

        if (minAngle < 0)
            minAngle += 360;

        if (maxAngle >= 360)
            maxAngle -= 360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
        if (minAngle > maxAngle) {
            if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
                return true;
        } else {
            if (azimuth > minAngle && azimuth < maxAngle)
                return true;
        }
        return false;
    }

    private void updateDescription() {
        descriptionText.setText(mPoi.getPoiDescription());
        descriptionTextView.setText(mPoi.getPoiName());
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        setNearestPoi();
        mAzimuthTeoretical = calculateAzimuth(mPoi.getPoiLatitude(), mPoi.getPoiLongitude());
        Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        mAzimuthReal = azimuthChangedTo;
        setNearestPoi();
        mAzimuthTeoretical = calculateAzimuth(mPoi.getPoiLatitude(), mPoi.getPoiLongitude());

        pointerIcon = (ImageView) findViewById(R.id.icon);
        descriptionText = (TextView) findViewById(R.id.description);

        double minAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(0);
        double maxAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(1);

        if (isBetween(minAngle, maxAngle, mAzimuthReal)) {
            pointerIcon.setVisibility(View.VISIBLE);
            updateDescription();
            descriptionText.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);

        } else {
            pointerIcon.setVisibility(View.INVISIBLE);
            descriptionText.setText("");
            descriptionTextView.setText("");
        }
    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();
    }

    private void setupLayout() {
        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (isCameraviewOn) {
            mCamera.stopPreview();
            isCameraviewOn = false;
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                isCameraviewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraviewOn = false;
    }
}

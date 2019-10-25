package com.example.al_ghifar.efalcongcs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SettingPID extends AppCompatDialogFragment {
    private EditText setRollP, setRollI, setRollD, setPitchP, setPitchI, setPitchD, setYawP, setYawI, setYawD, setAltP, setAltI, setAltD, setPosP, setPosI, setPosD, setPosRP, setPosRI, setPosRD, setNavRP, setNavRI, setNavRD;
    TextView tvsRoll, tvsPitch, tvsYaw, tvsAlt, tvsPos;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_pid, null);

        builder.setView(view)
                .setTitle("Setting PID")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String rollp = setRollP.getText().toString();
                        String rolli = setRollI.getText().toString();
                        String rolld = setRollD.getText().toString();

                        String pitchp = setPitchP.getText().toString();
                        String pitchi = setPitchI.getText().toString();
                        String pitchd = setPitchD.getText().toString();

                        String yawp = setYawP.getText().toString();
                        String yawi = setYawI.getText().toString();
                        String yawd = setYawD.getText().toString();

                        String altp = setAltP.getText().toString();
                        String alti = setAltI.getText().toString();
                        String altd = setAltD.getText().toString();

                        String posp = setPosP.getText().toString();
                        String posi = setPosI.getText().toString();
                        String posd = setPosD.getText().toString();

                        String posRp = setPosRP.getText().toString();
                        String posRi = setPosRI.getText().toString();
                        String posRd = setPosRD.getText().toString();

                        String navRp = setNavRP.getText().toString();
                        String navRi = setNavRI.getText().toString();
                        String navRd = setNavRD.getText().toString();

                        listener.applyTexts(rollp,rolli,rolld,pitchp,pitchi,pitchd,yawp,yawi,yawd,altp,alti,altd,posp,posi,posd,posRp,posRi,posRd,navRp,navRi,navRd);
                    }
                });
        Intent intent = getActivity().getIntent();

        tvsRoll = view.findViewById(R.id.Roll);
        tvsPitch = view.findViewById(R.id.Pitch);
        tvsYaw = view.findViewById(R.id.Yaw);
        tvsAlt = view.findViewById(R.id.Altitude);
        tvsPos = view.findViewById(R.id.Pos);

        setRollP = view.findViewById(R.id.rollP);
        setRollI = view.findViewById(R.id.rolli);
        setRollD = view.findViewById(R.id.rollD);

        setPitchP = view.findViewById(R.id.pitchP);
        setPitchI = view.findViewById(R.id.pitchI);
        setPitchD = view.findViewById(R.id.pitchD);

        setYawP = view.findViewById(R.id.yawP);
        setYawI = view.findViewById(R.id.yawI);
        setYawD = view.findViewById(R.id.yawD);


        setAltP = view.findViewById(R.id.altP);
        setAltI = view.findViewById(R.id.altI);
        setAltD = view.findViewById(R.id.altD);

        setPosP = view.findViewById(R.id.posP);
        setPosI = view.findViewById(R.id.posI);
        setPosD = view.findViewById(R.id.posD);

        setPosRP = view.findViewById(R.id.posRP);
        setPosRI = view.findViewById(R.id.posRI);
        setPosRD = view.findViewById(R.id.posRD);

        setNavRP = view.findViewById(R.id.navRP);
        setNavRI = view.findViewById(R.id.navRI);
        setNavRD = view.findViewById(R.id.navRD);

        Bundle mBundle = getArguments();
        if(mBundle != null) {
            String rollP = mBundle.getString("Rollp");
            String rollI = mBundle.getString("Rolli");
            String rollD = mBundle.getString("Rolld");

            String pitchP = mBundle.getString("Pitchp");
            String pitchI = mBundle.getString("Pitchi");
            String pitchD = mBundle.getString("Pitchd");

            String yawP = mBundle.getString("Yawp");
            String yawI = mBundle.getString("Yawi");
            String yawD = mBundle.getString("Yawd");

            String altP = mBundle.getString("Altp");
            String altI = mBundle.getString("Alti");
            String altD = mBundle.getString("Altd");

            String posP = mBundle.getString("Posp");
            String posI = mBundle.getString("Posi");
            String posD = mBundle.getString("Posd");

            String posRP = mBundle.getString("PosRp");
            String posRI = mBundle.getString("PosRi");
            String posRD = mBundle.getString("PosRd");

            String navRP = mBundle.getString("NavRp");
            String navRI = mBundle.getString("NavRi");
            String navRD = mBundle.getString("NavRd");

            setRollP.setText(rollP);
            setRollI.setText(rollI);
            setRollD.setText(rollD);

            setPitchP.setText(pitchP);
            setPitchI.setText(pitchI);
            setPitchD.setText(pitchD);

            setYawP.setText(yawP);
            setYawI.setText(yawI);
            setYawD.setText(yawD);

            setAltP.setText(altP);
            setAltI.setText(altI);
            setAltD.setText(altD);

            setPosP.setText(posP);
            setPosI.setText(posI);
            setPosD.setText(posD);

            setPosRP.setText(posRP);
            setPosRI.setText(posRI);
            setPosRD.setText(posRD);

            setNavRP.setText(navRP);
            setNavRI.setText(navRI);
            setNavRD.setText(navRD);
        }

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (DialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }

    }

    public interface DialogListener{
        void applyTexts(String rollp, String rolli, String rolld, String pitchp, String pitchi, String pitchd, String yawp, String yawi, String yawd, String altp, String alti, String altd, String posp, String posi, String posd, String posRp, String posRi, String posRd, String navRp, String navRi, String navRd);
    }
}

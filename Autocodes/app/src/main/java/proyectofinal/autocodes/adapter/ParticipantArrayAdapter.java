package proyectofinal.autocodes.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.MessagePattern;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import proyectofinal.autocodes.GroupActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.util.ImageUtil;


/**
 * Created by locu on 30/7/16.
 */
public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {
    List<Participant> participants;
    List<CheckBox> checkBoxes;
    boolean isAdmin;

    public ParticipantArrayAdapter(Context context, List<Participant> participants, boolean isAdmin) {
        super(context, 0, participants);
        this.participants = participants;
        checkBoxes = new ArrayList<CheckBox>();
        this.isAdmin = isAdmin;
    }
    public void deactivateCheckboxes(){
        Log.d("TEST", "Deactivating checkboxes");
        for(CheckBox check: checkBoxes) {
            Log.d("TEST", "Deactivating checkbox");
            check.setEnabled(false);
        }
    }

    public void activateCheckboxes(){
        Log.d("TEST", "activating checkboxes");
        for(CheckBox check: checkBoxes) {
            check.setEnabled(true);
        }
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Participant participant = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_row, parent, false);
        }

        TextView driverState = (TextView) convertView.findViewById(R.id.driverState);
        participant.setDriverState(driverState);

        TextView participantName = (TextView) convertView.findViewById(R.id.participantName);
        participantName.setText(participant.getName());


        ImageView imageView = (ImageView) convertView.findViewById((R.id.user_avatar));
        Log.e(LogConstants.FACEBOOK_RESPONSE, participant.getImageUrl().toString() );
        ImageUtil.displayImage(imageView, participant.getImageUrl(), null);

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_driver);
        participant.setCheckBox(checkBox);
        if(participant.isDriver()) {
            participant.getCheckBox().setChecked(true);
            participant.getDriverState().setText("Conductor designado");
        }
        checkBox.setTag(position); //For passing the list item index
        if(participant.getGroupActive() == 1 || !isAdmin){
            checkBox.setEnabled(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkBox.isChecked()){
                    checkBox.setChecked(false);
                    for(Participant p : participants) {
                        if(p.getId().equals(participant.getId())) {
                            p.setDriver(false);
                            if(p.getDriverState()!=null){
                                p.getDriverState().setText("Participante");
                            }
                        }
                    }
                } else {
                    checkBox.setChecked(true);
                    Toast.makeText(getContext(), "Conductor designado seleccionado correctamente",Toast.LENGTH_SHORT).show();
                    for(Participant p : participants) {
                        if(p.getId().equals(participant.getId())) {
                            p.setDriver(true);
                            if(p.getDriverState()!=null){
                                p.getDriverState().setText("Conductor designado");
                            }

                        } else {
                            p.setDriver(false);
                            p.getCheckBox().setChecked(false);
                            if(p.getDriverState()!=null){
                                p.getDriverState().setText("Participante");
                            }

                        }
                    }
                }

            }
        });

        return convertView;
    }
}

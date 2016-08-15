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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import proyectofinal.autocodes.GroupActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.model.Participant;


/**
 * Created by locu on 30/7/16.
 */
public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {
    List<Participant> participants;
    List<CheckBox> checkBoxes;

    public ParticipantArrayAdapter(Context context, List<Participant> participants) {
        super(context, 0, participants);
        this.participants = participants;
        checkBoxes = new ArrayList<CheckBox>();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Participant participant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_row, parent, false);
        }

        TextView participantName = (TextView) convertView.findViewById(R.id.participantName);
        participantName.setText(participant.getName());

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_driver);
        participant.setCheckBox(checkBox);
        if(participant.isDriver()) {
            participant.getCheckBox().setChecked(true);
        }
        checkBox.setTag(position); //For passing the list item index
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox currentCheckBox = (CheckBox) view;
                if(!checkBox.isChecked()){
                    checkBox.setChecked(false);
                    for(Participant p : participants) {
                        if(p.getId().equals(participant.getId())) {
                            p.setDriver(false);
                        }
                    }
                } else {
                    checkBox.setChecked(true);
                    for(Participant p : participants) {
                        if(p.getId().equals(participant.getId())) {
                            p.setDriver(true);
                        } else {
                            p.setDriver(false);
                            p.getCheckBox().setChecked(false);
                        }
                    }
                }

            }
        });

        return convertView;
    }
}

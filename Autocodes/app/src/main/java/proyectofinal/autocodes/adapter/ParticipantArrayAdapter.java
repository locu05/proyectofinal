package proyectofinal.autocodes.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import proyectofinal.autocodes.GroupActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.model.Participant;


/**
 * Created by locu on 30/7/16.
 */
public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {

    public int currentDriver = -1;

    public ParticipantArrayAdapter(Context context, List<Participant> participants) {
        super(context, 0, participants);
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
/*
        Button goButton = (Button) convertView.findViewById(R.id.selectDriver);
        goButton.setTag(position); //For passing the list item index
        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra(AutocodesIntentConstants.PARTICIPANT_ID, String.valueOf(participant.getId()));
                getContext().startActivity(intent);
        }});
*/

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_driver);
        checkBox.setTag(position); //For passing the list item index

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentDriver = participant.getId();
                Log.e("Driver state changed:", b + " " + String.valueOf(currentDriver));

            }
        });
        return convertView;
    }
}

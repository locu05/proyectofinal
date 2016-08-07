package proyectofinal.autocodes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import proyectofinal.autocodes.GroupActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.util.ImageUtil;


/**
 * Created by locu on 30/7/16.
 */
public class ParticipantAddedArrayAdapter extends ArrayAdapter<Participant> {

    public ParticipantAddedArrayAdapter(Context context, List<Participant> participants) {
        super(context, 0, participants);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Participant participant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_added_row, parent, false);
        }

        ImageUtil.displayRoundImage((ImageView) convertView.findViewById(R.id.profilePicture), participant.getImageUrl(), null);

        return convertView;

    }
}

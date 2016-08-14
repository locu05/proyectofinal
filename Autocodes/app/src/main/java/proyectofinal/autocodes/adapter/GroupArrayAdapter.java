package proyectofinal.autocodes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import proyectofinal.autocodes.GroupActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.model.Group;


/**
 * Created by locu on 30/7/16.
 */
public class GroupArrayAdapter extends ArrayAdapter<Group> {

    public GroupArrayAdapter(Context context, List<Group> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Group group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_row, parent, false);
        }

        TextView nameGroup = (TextView) convertView.findViewById(R.id.namegroup);
        nameGroup.setText(group.getName());
        TextView groupStatus = (TextView) convertView.findViewById(R.id.groupstatuslistgroups);
        groupStatus.setText(group.getActive()==0?"Inactivo":"Activo");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra(AutocodesIntentConstants.GROUP_ID, String.valueOf(group.getId()));
                intent.putExtra(AutocodesIntentConstants.GROUP_NAME, String.valueOf(group.getName()));
                getContext().startActivity(intent);
            }
        });
       /* Button goButton = (Button) convertView.findViewById(R.id.goButton);
        goButton.setTag(position); //For passing the list item index
        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra(AutocodesIntentConstants.GROUP_ID, String.valueOf(group.getId()));
                intent.putExtra(AutocodesIntentConstants.GROUP_NAME, String.valueOf(group.getName()));
                getContext().startActivity(intent);
        }});
*/
        return convertView;
    }
}

package proyectofinal.autocodes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import proyectofinal.autocodes.R;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.util.ImageUtil;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.Collections;

public class ParticipantDefaultAdapter extends BaseAdapter implements Swappable, UndoAdapter, OnDismissCallback {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Participant> participantList;
	private boolean mShouldShowDragAndDropIcon;
	
	public ParticipantDefaultAdapter(Context context, ArrayList<Participant> participantArrayList, boolean shouldShowDragAndDropIcon) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		participantList = participantArrayList;
		mShouldShowDragAndDropIcon = shouldShowDragAndDropIcon;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return participantList.size();
	}

	@Override
	public Object getItem(int position) {
		return participantList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return participantList.get(position).getId();
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(participantList, positionOne, positionTwo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_default, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (TextView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Participant participant = participantList.get(position);

		ImageUtil.displayRoundImage(holder.image, participant.getImageUrl(), null);
		holder.text.setText(participant.getName());
		if (mShouldShowDragAndDropIcon) {
			holder.icon.setText(R.string.fontello_drag_and_drop);
		} else {
			holder.icon.setText(participant.getIconRes());
		}
		return convertView;
	}
	
	
	private static class ViewHolder {
		public ImageView image;
		public /*Roboto*/TextView text;
		public /*Fontello*/TextView icon;
	}

	@Override
	@NonNull
	public View getUndoClickView(@NonNull View view) {
		return view.findViewById(R.id.undo_button);
	}

	@Override
	@NonNull
	public View getUndoView(final int position, final View convertView,
							@NonNull final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item_undo_view,
					parent, false);
		}
		return view;
	}

	@Override
	public void onDismiss(@NonNull final ViewGroup listView,
			@NonNull final int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			remove(position);
		}
	}
	public void remove(int position) {
		participantList.remove(position);
	}
}

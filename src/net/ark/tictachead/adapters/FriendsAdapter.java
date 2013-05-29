package net.ark.tictachead.adapters;

import java.util.List;

import android.widget.ImageView;
import net.ark.tictachead.R;
import net.ark.tictachead.models.Player;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendsAdapter extends ArrayAdapter<Player> {
	public FriendsAdapter(Context context, List<Player> friends) {
		//Super
		super(context, R.layout.friend_item, friends);
		
		//Save
		m_Context	= context;
		m_Friends 	= friends;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get old item
		View ItemView = convertView;
		if (ItemView == null) {
			//Create layout inflater
			LayoutInflater Inflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (Inflater != null) ItemView = Inflater.inflate(R.layout.friend_item, parent, false);
		}
		
		//If new item exist
		if (ItemView != null) {
			//If positon okay
			if (position >= 0 && position < m_Friends.size()) {
				//Get player data
				Player Data = m_Friends.get(position);
				if (Data != null) {
					//Set name
					View LabelName = ItemView.findViewById(R.id.label_name);
					if (LabelName != null && LabelName instanceof TextView) ((TextView)LabelName).setText(Data.getName());

					//Set rating
					View LabelRating = ItemView.findViewById(R.id.label_rating);
					if (LabelRating != null && LabelRating instanceof TextView) ((TextView)LabelRating).setText("Rating: " + Data.getRating());

					//Set avatar
					View ImageAvatar = ItemView.findViewById(R.id.image_avatar);
					if (ImageAvatar != null && ImageAvatar instanceof ImageView) ((ImageView)ImageAvatar).setImageResource(Data.getResourceID());
				}
			}
		}
		
		//Return
		return ItemView;
	}
	
	//Data
	protected Context 		m_Context;
	protected List<Player>	m_Friends;
}

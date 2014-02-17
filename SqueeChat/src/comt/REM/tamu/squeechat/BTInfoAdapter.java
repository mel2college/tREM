package comt.REM.tamu.squeechat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BTInfoAdapter extends BaseAdapter {
	private ArrayList<String> NameList;
	private ArrayList<String> AddressList;
	private LayoutInflater mInflater;
	
	static class ViewHolder {
		TextView text1;
		TextView text2;
	}

	public BTInfoAdapter(Context c, ArrayList<String> Names, ArrayList<String> Addresses) {
		NameList = Names;
		AddressList = Addresses;
		
		mInflater = LayoutInflater.from(c);
	}
	
	public void updateAdapter(ArrayList<String> NewNames, ArrayList<String> NewAddresses){
		NameList.clear();
		AddressList.clear();
		NameList.addAll(NewNames);
		AddressList.addAll(NewAddresses);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return (AddressList == null) ? 0 : AddressList.size();
	}

	@Override
	public Object getItem(int position) {
		return AddressList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_2, null);
			
			holder = new ViewHolder();
			holder.text1 = (TextView)convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView)convertView.findViewById(android.R.id.text2);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
	
		holder.text1.setText(NameList.get(position));
		holder.text2.setText(AddressList.get(position));
		
		return convertView;
	}

}

package com.paibo.sniff.adapter;

import java.util.List;
import java.util.Map;

import com.paibo.sniff.R;
import com.paibo.sniff.bean.Wifi;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * wifi
 * @author jiangbing
 *	
 */
public class WifiInfoAdapter extends BaseAdapter {

	public LayoutInflater mInfalter;
	public List<Wifi> mData;
	public Context context;
	
	
	public WifiInfoAdapter(Context context, List<Wifi> mData) {
		super();
		this.context = context;
		this.mInfalter = LayoutInflater.from(context);
		this.mData = mData;
	}

	public WifiInfoAdapter() {
		super();
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		Wifi item = mData.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = mInfalter.inflate(R.layout.item_wifi_info, null);
			holder.mWifiNum = (TextView) convertView.findViewById(R.id.wifi_info_num);
			holder.mWifiName = (TextView) convertView.findViewById(R.id.wifi_info_name);
			holder.mWifiTmp = (TextView) convertView.findViewById(R.id.wifi_info_tmp);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mWifiNum.setText(position + 1 + "");
		holder.mWifiName.setText(item.getWifiName());
		holder.mWifiTmp.setText(item.getWifiTmp());
		
		holder.wifi = item;
		convertView.setTag(holder);
		return convertView;
	}
	
	
	public static class ViewHolder {
		public TextView mWifiName, mWifiTmp, mWifiNum;
		public Integer signallevel; // пе╨ег©╤х
		public Wifi wifi;
	}

}

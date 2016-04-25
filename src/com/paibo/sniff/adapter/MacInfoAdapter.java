package com.paibo.sniff.adapter;

import java.util.List;
import java.util.Map;

import com.paibo.sniff.R;
import com.paibo.sniff.bean.Mac;
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
 * mac
 * @author jiangbing
 *	
 */
public class MacInfoAdapter extends BaseAdapter {

	public LayoutInflater mInfalter;
	public List<Mac> mData;
	public Context context;
	
	
	public MacInfoAdapter(Context context, List<Mac> mData) {
		super();
		this.context = context;
		this.mInfalter = LayoutInflater.from(context);
		this.mData = mData;
	}

	public MacInfoAdapter() {
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

		Mac item = mData.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = mInfalter.inflate(R.layout.item_mac_info, null);
			holder.mMacInfoNum = (TextView) convertView.findViewById(R.id.mac_info_num);
			holder.mPhoneNum = (TextView) convertView.findViewById(R.id.phone_num);
			holder.mMac = (TextView) convertView.findViewById(R.id.mac);
			holder.mSsid = (TextView) convertView.findViewById(R.id.ssid);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mMacInfoNum.setText(position + 1 + "");
		holder.mPhoneNum.setText(" " + item.getPhoneNum());
		holder.mMac.setText(" " + item.getMac());
		holder.mSsid.setText(" " + item.getSsid());
		
		holder.mac = item;
		convertView.setTag(holder);
		return convertView;
	}
	
	
	public static class ViewHolder {
		public TextView mMacInfoNum, mPhoneNum, mMac, mSsid;
		public Integer level; // пе╨ег©╤х
		public Mac mac;
	}

}

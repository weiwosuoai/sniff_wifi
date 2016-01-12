package com.paibo.sniff.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paibo.sniff.R;
import com.paibo.sniff.bean.Device;
import com.paibo.sniff.bean.SniffWifi;
import com.paibo.sniff.utils.StringUtil;

public class WifiExpandableListViewAdapter extends BaseExpandableListAdapter {

	private Context context;
	
	private List<SniffWifi> mSniffWifiList;
	
	public WifiExpandableListViewAdapter(Context context, List<SniffWifi> sniffWiffList) {
		this.context = context;
		this.mSniffWifiList = sniffWiffList;
	}

	@Override
	public int getGroupCount() {
		return mSniffWifiList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mSniffWifiList.get(groupPosition)
				.getConnectingDevices().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSniffWifiList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mSniffWifiList.get(groupPosition)
				.getConnectingDevices().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		HeadHolder headHolder = null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.head_expandlist, null);
			headHolder = new HeadHolder();
			headHolder.mSSID = (TextView) convertView.findViewById(R.id.wifi_info_name);
			headHolder.mBSSID = (TextView) convertView.findViewById(R.id.wifi_info_tmp);
			headHolder.mSignalLevel = (ImageView) convertView.findViewById(R.id.signal_level);
			headHolder.mPwd = (ImageView) convertView.findViewById(R.id.pwd);
			headHolder.mCapabilities = (TextView) convertView.findViewById(R.id.capabilities);
			headHolder.mArrow = (ImageView) convertView.findViewById(R.id.arrow);
			headHolder.mSSIDNum = (TextView) convertView.findViewById(R.id.wifi_info_num);
			
			convertView.setTag(headHolder);
		} else {
			headHolder = (HeadHolder) convertView.getTag();
		}
		
		// set data
		SniffWifi sniffWifi = mSniffWifiList.get(groupPosition);
		
		headHolder.mSSIDNum.setText(sniffWifi.getSSIDNum() + "");
		headHolder.mSSID.setText(sniffWifi.getSSID());
		headHolder.mBSSID.setText(sniffWifi.getBSSID());
		headHolder.mCapabilities.setText(" (通过 " + sniffWifi.getCapabilities() + " 保护)");
		
		// 是否显示加锁的标识
		if (TextUtils.isEmpty(sniffWifi.getCapabilities())) {
			headHolder.mCapabilities.setVisibility(View.GONE);
		} else {
			headHolder.mCapabilities.setVisibility(View.VISIBLE);
		}
		
		// 是否显示箭头
		if (mSniffWifiList.get(groupPosition).getConnectingDevices().size() > 0) {
			headHolder.mArrow.setVisibility(View.VISIBLE);
			headHolder.mArrow.setImageResource(R.drawable.list_default);
		} else {
			headHolder.mArrow.setVisibility(View.GONE);
		}
		
		// 是否展开
		if (isExpanded) {
			headHolder.mArrow.setImageResource(R.drawable.list_pressed);
		} else {
			headHolder.mArrow.setImageResource(R.drawable.list_default);
		}
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		if (mSniffWifiList.get(groupPosition).getConnectingDevices() == null)
			return null;
		
		ItemHolder itemHolder = null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_expandlist, null);
			itemHolder = new ItemHolder();
			itemHolder.mDeviceMac = (TextView) convertView.findViewById(R.id.mac);
			
			convertView.setTag(itemHolder);
		} else {
			itemHolder = (ItemHolder) convertView.getTag();
		}
		
		// set data
		Device device = mSniffWifiList.get(groupPosition)
				.getConnectingDevices().get(childPosition);
		itemHolder.mDeviceMac.setText(device.getMac());
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	/** expandableListView的head */
	class HeadHolder {
		public TextView mSSID;
		public ImageView mSignalLevel;
		public TextView mBSSID;
		public ImageView mPwd;
		public TextView mCapabilities;
		public ImageView mArrow; // 指示icon
		public TextView mSSIDNum;
		
	}

	/** expandableListView的item */
	class ItemHolder {
		public TextView mDeviceMac;
	}
}

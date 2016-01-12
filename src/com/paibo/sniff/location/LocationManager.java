package com.paibo.sniff.location;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.paibo.sniff.base.MyApplication;
import com.paibo.sniff.bean.Location;
import com.paibo.sniff.utils.LogUtil;

/**
 * 定位功能管理者
 * 
 * @author jiangbing
 *
 */
public class LocationManager {
	
	public interface LocationListener {
		// 获取位置信息后回调
		void onReceiveLocation(Location locationInfo);
	}
	
	public static LocationManager mManager;
	
	private LocationClient mLocationClient;
	
	private BDLocationListener mListener = new MyLocationListener();
	
	private static LocationListener mLocationListener;
	
	// Constructer
	public LocationManager() {
		super();
		
		// Declare a location client
		mLocationClient = new LocationClient(MyApplication.getContext());
		// Register a listener
		mLocationClient.registerLocationListener(mListener);
		initLocation();
	}
	
	public static LocationManager getInstance(LocationListener listener) {
		if (mManager == null) {
			mManager = new LocationManager();
		}
		mLocationListener = listener;
		return mManager;
	}
	
	/**
	 * 开始获取位置信息
	 */
	public void start() {
		mLocationClient.start();
	}
	
	/**
	 * 关闭定位
	 */
	public void stop() {
		mLocationClient.stop();
	}
	
	/**
	 * 配置定位sdk参数
	 */
	private void initLocation() {
		// LocationClientOption类，用来设置定位sdk的定位方式
		LocationClientOption option = new LocationClientOption();
		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		int span = 1000;
//		// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//		option.setScanSpan(span);
		// 可选，设置是否需要地址信息，默认不需要
		option.setIsNeedAddress(true);
		// 可选，默认为false,设置是否使用gps
		option.setOpenGps(true);
		// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setLocationNotify(true);
		// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，
		// 结果类似于“在北京天安门附近”
		option.setIsNeedLocationDescribe(true);
		// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//		option.setIsNeedLocationPoiList(true);
		// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，
		// 设置是否在stop的时候杀死这个进程，默认杀死
		option.setIgnoreKillProcess(false);
		// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.SetIgnoreCacheException(false);
		// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
        	// BDLocation类封装了定位sdk的定位结果
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed()); // 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude()); // 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection()); // 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe()); // 位置语义化信息
            List<Poi> list = location.getPoiList(); // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            LogUtil.i(sb.toString());
            
            Location myLocation = new Location();
            myLocation.setLongitude(location.getLongitude());
            myLocation.setLatitude(location.getLatitude());
            myLocation.setAddress(location.getAddrStr());
            myLocation.setAddressDes(location.getLocationDescribe());
            mLocationListener.onReceiveLocation(myLocation);
        }
	}
	}

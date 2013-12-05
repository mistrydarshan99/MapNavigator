package com.tyczj.mapnavigator;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Route {
	
	private Steps stps=new Steps();
	private ArrayList<LatLng> accuratePath1=new ArrayList<LatLng>();
	private ArrayList<LatLng> accuratePath2=new ArrayList<LatLng>();
	private ArrayList<LatLng> accuratePath3=new ArrayList<LatLng>();
	private ArrayList<LatLng> path = new ArrayList<LatLng>();
	
	private ArrayList<Legs> legs = new ArrayList<Legs>();
	private String totalDuration;
	private String totalDistance;
	private LatLng startLoc;
	private LatLng endLoc;
	private LatLngBounds bounds;
	private String startAddress;
	private String endAddress;
	
	public Route(JSONObject route,int j){
		parseRoute(route,j);
	}
	
	
	private void parseRoute(JSONObject obj3,int k){
		try{
			if(!obj3.isNull("legs")){
				JSONArray leg = obj3.getJSONArray("legs");
				Log.e("leg length", ""+leg.length());
				for(int i=0; i<leg.length();i++){
					JSONObject obj = leg.getJSONObject(i);
					if(i == 0){
						if(!obj.isNull("start_address")){
							startAddress = obj.getString("start_address");
						}
						
						if(!obj.isNull("end_address")){
							endAddress = obj.getString("end_address");
						}

					}
					
					legs.add(new Legs(obj,k));
				
				}
			}
			
			if(!obj3.isNull("duration")){
				JSONArray obj = obj3.getJSONArray("duration");
				
				for(int i=0; i<obj.length();i++){
					JSONObject obj2 = obj.getJSONObject(i);
					totalDuration = obj2.getString("text");
				}
			}
			
			if(!obj3.isNull("distance")){
				JSONArray obj = obj3.getJSONArray("distance");
				
				for(int i=0; i<obj.length();i++){
					JSONObject obj2 = obj.getJSONObject(i);
					totalDistance = obj2.getString("text");
				}
			}
			
			if(!obj3.isNull("start_location")){
				JSONArray pos = obj3.getJSONArray("start_location");
				
				for(int i=0; i<pos.length();i++){
					JSONObject obj = pos.getJSONObject(i);
					startLoc = new LatLng(obj.getDouble("lat"),obj.getDouble("lng"));
				}
			}
			
			if(!obj3.isNull("end_location")){
				JSONObject pos = obj3.getJSONObject("end_location");
				endLoc= new LatLng(pos.getDouble("lat"),pos.getDouble("lng"));
				
			}
			
			if(!obj3.isNull("bounds")){
				JSONObject pos = obj3.getJSONObject("bounds");

					LatLng southWest = null;
					LatLng northEast = null;
					if(!pos.isNull("southwest")){
						JSONObject obj2 = pos.getJSONObject("southwest");
						southWest = new LatLng(obj2.getDouble("lat"),obj2.getDouble("lng"));
					}
					
					if(!pos.isNull("northeast")){
						JSONObject obj2 = pos.getJSONObject("northeast");
						northEast = new LatLng(obj2.getDouble("lat"),obj2.getDouble("lng"));
					}
					
					if(southWest != null && northEast != null){
						this.bounds = new LatLngBounds(southWest,northEast);
					}
//				}
				
				
			}
			if(!obj3.isNull("overview_polyline")){
				JSONObject poly = obj3.getJSONObject("overview_polyline");
				 decodePoly(poly.getString("points"));
				 this.accuratePath1 =stps.getPathpoins();
				 this.accuratePath2=stps.getPathpoins1();
				 this.accuratePath3=stps.getPathpoins2();
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		
	}
	
	private void decodePoly(String encoded) {
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;                 
                shift += 5;             
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;  
                shift += 5;             
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
 
            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            path.add(position);
        }
    }
	
//	private ArrayList<LatLng> decodePoly(String encoded) {
//	       ArrayList<LatLng> poly = new ArrayList<LatLng>();
//	       int index = 0, len = encoded.length();
//	       int lat = 0, lng = 0;
//	       while (index < len) {
//	           int b, shift = 0, result = 0;
//	           do {
//	               b = encoded.charAt(index++) - 63;
//	               result |= (b & 0x1f) << shift;
//	               shift += 5;
//	           } while (b >= 0x20);
//	           int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//	           lat += dlat;
//	           shift = 0;
//	           result = 0;
//	           do {
//	               b = encoded.charAt(index++) - 63;
//	               result |= (b & 0x1f) << shift;
//	               shift += 5;
//	           } while (b >= 0x20);
//	           int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//	           lng += dlng;
//
//	           LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
//	           poly.add(position);
//	       }
//	       return poly;
//	   }
	
	public String getDuration(){
		return totalDuration;
	}
	
	public ArrayList<LatLng> getPath(){
		Log.e("length of path", ""+path.size());
		return path;
	}
	
	public String getDistance(){
		return totalDistance;
	}
	
	public String getStartAddress(){
		return startAddress;
	}
	
	public String getEndAddress(){
		return endAddress;
	}
	
	public ArrayList<Legs> getLegs(){
		return legs;
	}
	
	public LatLngBounds getMapBounds(){
		return bounds;
	}
	
	public LatLng getStartLocation(){
		return startLoc;
	}
	
	public LatLng getEndLocation(){
		return endLoc;
	}
	
	
	
	/*public ArrayList<LatLng> setAccuratePath(ArrayList<LatLng> accuratePathreturn) {
		this.accuratePath1 = accuratePathreturn;
		return accuratePath1;
	}*/
	
	public ArrayList<LatLng>getaccuratepath(){
		
		Log.e("length of path", ""+accuratePath1.size());
		return accuratePath1;
	}
	
public ArrayList<LatLng>getaccuratepath11(){
		
		Log.e("length of path", ""+accuratePath1.size());
		return accuratePath2;
	}

public ArrayList<LatLng>getaccuratepath12(){
	
	Log.e("length of path", ""+accuratePath1.size());
	return accuratePath3;
}

}

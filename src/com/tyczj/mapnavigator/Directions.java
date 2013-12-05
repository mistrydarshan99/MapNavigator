package com.tyczj.mapnavigator;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

public class Directions {
	
	private ArrayList<Route> routes = new ArrayList<Route>();
	private String directions;
	

	
	public enum DrivingMode{
		DRIVING,MASS_TRANSIT,BYCICLE,WALKING
	}
	
	public enum Avoid{
		TOLLS,HIGHWAYS,NONE
	}
	
	public Directions(String directions){
		this.directions = directions;
		
		if(directions != null){
			parseDirections();
		}

	}
	
	private void parseDirections(){
		try {
			JSONObject json = new JSONObject(directions);

			
			if(!json.isNull("routes")){
				JSONArray route = json.getJSONArray("routes");
				Log.e("Route length", ""+route.length());
				for(int k=0;k<route.length(); k++){
					
					/*if(route.length()==1){
						routepath1=new ArrayList<LatLng>();
					}else if(route.length()==2){
						routepath2=new ArrayList<LatLng>();
					}*/
					
					JSONObject obj3 = route.getJSONObject(k);
					routes.add(new Route(obj3,k));
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Route> getRoutes(){
		Log.e("Route length", ""+routes.toArray());
		return routes;
	}

}

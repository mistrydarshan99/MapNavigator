package com.tyczj.mapnavigator;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Legs {
	
	private ArrayList<Steps> steps;
	
	public Legs(JSONObject leg , int y){
		steps = new ArrayList<Steps>();
		parseSteps(leg,y);
	}
	
	public ArrayList<Steps> getSteps(){
		return steps;
	}
	
	private void parseSteps(JSONObject leg,int routeno){
		try{
			if(!leg.isNull("steps")){
				JSONArray step = leg.getJSONArray("steps");
				Log.e("leg length", ""+leg.length());
				Log.e("step length", ""+step.length());
				for(int i=0; i<step.length();i++){
					JSONObject obj = step.getJSONObject(i);
					steps.add(new Steps(obj,routeno));
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}

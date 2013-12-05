package com.tyczj.mapnavigator;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

public class Navigator {
	private Context context;
	private LatLng startPosition, endPosition;
	private String mode;
	private boolean showCalc;
	private GoogleMap map;
	private Directions directions;
	private int pathColor = Color.BLUE;
	private int secondPath = Color.CYAN;
	private int thirdPath = Color.RED;
	private float pathWidth = 5;
	private OnPathSetListener listener;
	private boolean alternatives = false;
	private long arrivalTime;
	private String avoid;
	private ArrayList<Polyline> lines = new ArrayList<Polyline>();
	
	
	public Navigator(GoogleMap map, LatLng startLocation, LatLng endLocation,Context context){
		this.startPosition = startLocation;
		this.endPosition = endLocation;
		this.map = map;
		this.context=context;
		
		Steps.accuratePath.clear();
		Steps.routepath1.clear();
		Steps.routepath2.clear();
		
	}
	
	public interface OnPathSetListener{
		public void onPathSetListener(Directions directions);
	}
	
	public void setOnPathSetListener(OnPathSetListener listener){
		this.listener = listener;
	}
	
	/**
	 * Gets the starting location for the directions 
	 * 
	 */
	public LatLng getStartPoint(){
		return startPosition;
	}
	
	/**
	 * Gets the end location for the directions 
	 * 
	 */
	public LatLng getEndPoint(){
		return endPosition;
	}
	
	/**
	 * Get's driving directions from the starting location to the ending location
	 * 
	 * @param showDialog 
	 *  Set to true if you want to show a ProgressDialog while retrieving directions
	 *  @param findAlternatives
	 *  give alternative routes to the destination
	 *  
	 */
	public void findDirections(boolean showDialog,boolean findAlternatives){
		this.alternatives = findAlternatives;
		showCalc=showDialog;
		new PathCreator().execute();
	}
	
	/**
	 * Sets the type of direction you want (driving,walking,biking or mass transit). This MUST be called before getting the directions
	 * If using "transit" mode you must provide an arrival time
	 * 
	 * @param mode
	 * The type of directions you want (driving,walking,biking or mass transit)
	 * @param arrivalTime
	 * If selected mode it "transit" you must provide and arrival time (milliseconds since January 1, 1970 UTC). If arrival time is not given
	 * the current time is given and may return unexpected results.
	 */
	public void setMode(int mode, long arrivalTime,int avoid){
		switch(mode){
		
		case 0:
			this.mode = "driving";
			break;
		case 1:
			this.mode = "transit";
			this.arrivalTime = arrivalTime;
			break;
		case 2:
			this.mode = "bicycling";
			break;
		case 3:
			this.mode = "walking";
			break;
		default:
			this.mode = "driving";
			break;
		}
		
		switch(avoid){
		case 0:
			this.avoid = "tolls";
			break;
		case 1:
			this.avoid = "highways";
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get all direction information
	 * @return
	 */
	public Directions getDirections(){
		return directions;
	}
	
	/**
	 * Change the color of the path line, must be called before calling findDirections().
	 * @param firstPath
	 * Color of the first line, default color is blue.
	 * @param secondPath
	 * Color of the second line, default color is cyan
	 * @param thirdPath
	 * Color of the third line, default color is red
	 * 
	 */
	public void setPathColor(int firstPath,int secondPath, int thirdPath){
		pathColor = firstPath;
	}
	
	/**
	 * Change the width of the path line
	 * @param width
	 * Width of the line, default width is 3
	 */
	public void setPathLineWidth(float width){
		pathWidth = width;
	}
	
	private Polyline showPath(Route route,int color,int l){

		if(l==0){
			return map.addPolyline(new PolylineOptions().addAll(route.getaccuratepath()).color(color).width(pathWidth));
		}else if(l==1){
			return map.addPolyline(new PolylineOptions().addAll(route.getaccuratepath11()).color(color).width(pathWidth));
		}else{
			return map.addPolyline(new PolylineOptions().addAll(route.getaccuratepath12()).color(color).width(pathWidth));
		}
		
	}
	
	public ArrayList<Polyline> getPathLines(){
		return lines;
	}

	private class PathCreator extends AsyncTask<Void,Void,Directions>{
		private ProgressDialog pd;
		@Override
		protected void onPreExecute(){
			if(showCalc){
				pd = new ProgressDialog(context);
				pd.setMessage("Getting Directions");
//				pd.setIndeterminate(true);
				pd.show();
			}
		}

		@Override
		protected Directions doInBackground(Void... params) {
			if(mode == null){
				mode = "driving";
			}
			
		        String url = "http://maps.googleapis.com/maps/api/directions/json?"
		                + "origin=" + startPosition.latitude + "," + startPosition.longitude
		                + "&destination=" + endPosition.latitude + "," + endPosition.longitude
		                + "&sensor=false&units=metric&mode="+mode+"&alternatives="+String.valueOf(alternatives);
		        
		        if(mode.equals("transit")){
		        	if(arrivalTime > 0){
		        		url += url + "&arrival_time="+arrivalTime;
		        	}else{
		        		url += url + "&departure_time="+System.currentTimeMillis();
		        	}
		        }
		        
		        if(avoid != null){
		        	url += url+"&avoid="+avoid;
		        }
		 
		        try {
		            HttpClient httpClient = new DefaultHttpClient();
		            HttpContext localContext = new BasicHttpContext();
		            HttpPost httpPost = new HttpPost(url);
		            HttpResponse response = httpClient.execute(httpPost, localContext);
		            
		            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		            	
		            	String s = EntityUtils.toString(response.getEntity());
			            
			            return new Directions(s);
		            }
		            
		            
		            return null;
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Directions directions){
			
			if(directions != null){
				Navigator.this.directions = directions;
				for(int i=0; i<directions.getRoutes().size(); i++){
					Route r = directions.getRoutes().get(i);
					Routepoints rp=new Routepoints();
					Log.e("size of array", ""+rp.getAccuratePath());
					if(i == 0){
						lines.add(showPath(r,pathColor,i));
						
					}else if(i == 1){
						
						lines.add(showPath(r,secondPath,i));
					
						
					}else if(i == 2){
						
						lines.add(showPath(r,thirdPath,i));
						
					}
				}
				
				if(listener != null){
					listener.onPathSetListener(directions);
				}
				
			}
			
			if(showCalc && pd != null){
				pd.dismiss();
			}
		}
		
	}

}

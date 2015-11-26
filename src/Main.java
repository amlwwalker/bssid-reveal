import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Main {
	JSONObject locationObject;
 	JSONArray jsonArray;
	public Main() {
		locationObject = new JSONObject();
		 jsonArray = new JSONArray();
	}
	public static void main(String[] args) {
		Main m = new Main();
		m.getWLANbssidInfo();
		try {
			m.getGeoLocation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m.postJson();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getWLANbssidInfo(){

	     String cmd = "airport -s";
	        String wlanResults = "";
			try {

	            Process p3;
	             p3 = Runtime.getRuntime().exec(cmd);
	             p3.waitFor();
	             BufferedReader reader = new BufferedReader(new InputStreamReader(p3.getInputStream()));
	             String line = reader.readLine();
	             line = reader.readLine();
	             while(line!=null){
	                 wlanResults += line + "\n";
	                 String[] splited = line.split("\\s+");
                 
	                 JSONObject obj = new JSONObject();
	                 obj.put("ssid",splited[1]);
	                 obj.put("bssid",splited[2]);
	                 obj.put("rssi",splited[3]);
	                 obj.put("channel",splited[4]);
	                 obj.put("ht",splited[5]);
	                 obj.put("cc",splited[6]);
	                 obj.put("security",splited[7]);
	                 jsonArray.put(obj);
	                 locationObject.put("ssids", jsonArray);
	                 line = reader.readLine();
	             }
	        } catch (IOException ex) {

	            wlanResults += "Comand error\n" + ex;
	            System.out.println("There was an IO exception. " + wlanResults);

	        } catch (InterruptedException ex) {
	            wlanResults += "Command was interrupted: " + ex;
	            System.out.println("The command was interrupted.");
	        }
	}
	private void postJson() throws IOException {
		String requestString = "http://localhost:3000/receive";
		String jsonString = locationObject.toString();
		//send request
		URL url = new URL(requestString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		
		try {
			OutputStream os = conn.getOutputStream();	
			os.write(jsonString.getBytes());
			os.flush();
			int responseCode = conn.getResponseCode();
			//get result if there is one
			if(responseCode == 200) //HTTP 200: Response OK
			{
			    String result = "";
			    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    String output;
			    while((output = br.readLine()) != null)
			    {
			        result += output;
			    }
			    System.out.println("Response message: " + result);
			    JOptionPane.showMessageDialog(null, result, "Response",
	                    JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (java.net.ConnectException e) {
			JOptionPane.showMessageDialog(null, e + " by server\r\nBailing out.", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
			System.out.println("error " + e);
		}
	}
	private void getGeoLocation() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		String ip = in.readLine(); //you get the IP as a String
		String getRequest = "https://maps.googleapis.com/maps/api/browserlocation/json?browser=firefox&sensor=true&";
		for (int i = 0; i < jsonArray.length(); ++i) {
		    JSONObject rec = jsonArray.getJSONObject(i);
		    String bssid = rec.getString("bssid");
		    String ssid = rec.getString("ssid");
            String rssi = rec.getString("rssi");
            String channel = rec.getString("channel");
            String ht = rec.getString("ht");
            String cc = rec.getString("cc");
            String security = rec.getString("security");
            getRequest += "wifi=mac:" + bssid + "|ssid:" + ssid + "|ss:" + rssi + "&"; 
		}
		URL resultLocation = new URL(getRequest);
		BufferedReader locationBack = new BufferedReader(new InputStreamReader(
				resultLocation.openStream()));
		String line = locationBack.readLine(); //you get the IP as a String
		String jsonString = "";
		while(line!=null){
			jsonString += line;
			line = locationBack.readLine();
		}
	    JSONObject jsonObj = new JSONObject(jsonString);
	    JSONObject location = jsonObj.getJSONObject("location");
	    JSONObject concatData = new JSONObject();
	    concatData.put("ip", ip);
	    concatData.put("accuracy", jsonObj.getInt("accuracy"));
	    concatData.put("lat", location.getDouble("lat"));
	    concatData.put("lng", location.getDouble("lng"));
//	    System.out.println(jsonObj.getInt("accuracy"));
//	    System.out.println(location.getDouble("lat"));
//	    System.out.println(location.getDouble("lng"));
	    locationObject.put("location", concatData);
	
	}

}

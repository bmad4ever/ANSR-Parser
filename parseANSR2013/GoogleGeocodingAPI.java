package parseANSR2013;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GoogleGeocodingAPI {

	private GoogleGeocodingAPI(){}
	
	final static String base_url = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAT6YvPdKrR3AGWBPeDdd5QsbxTQCUBiwk&address=";
	
	public static String[] sendGet(String location) throws Exception {

		URL obj = new URL(base_url+URLEncoder.encode("Portugal,"+location, "UTF-8"));
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		//con.setRequestProperty( , );

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString());

		//order nex,ney,swx,swy,lx,ly;
		String[] ret = new String[6];
		int it=0;
		final String base_regex = "\".*?\"lat\" : ([\\d,\\.,-]+),.*?\"lng\" : ([\\d,\\.,-]+)";

		for(String toGet: new String[]{"northeast","southwest","location"})
		{
			Pattern pattern = Pattern.compile(toGet + base_regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				ret[it]=matcher.group(1);it++;
				ret[it]=matcher.group(2);it++;
		    }else return null;
		}
		
		return ret;
	}
}

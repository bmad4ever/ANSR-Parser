package parseANSR2013;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LDACS implements IParser{

	MODE mode;
	
	final static Pattern checkTableStartPattern = Pattern.compile("Concelho,Datahora,M,FG,Via,Km,Natureza");
	final static Pattern checkTableRowPattern = Pattern.compile("\"?.*?\"?,\"?.*?\"?,\"?.*?\"?,\"?.*?\"?,\"?.*?\"?,\"?.*?\"?,\"?.*\"?");
	final static Pattern getRowDataPattern = Pattern.compile(
			"\"?(.+?)\"?,\"?.*?\"?,\"?(.+?)\"?,\"?(.+?)\"?,\"?(.+?)\"?,"
	+"(\".+\"|.*?)"
	+		",\"?.*\"?");
	
	final static Pattern check_if_road_Km_is_valid = Pattern.compile("[\\d,\\.,\\,]+");
	
	public LDACS(MODE mode) {
		super();
		this.mode = mode;
		/*if(mode!=MODE.LDACS_EXCLUDE){
			throw new RuntimeException("Not Implemented!");
		}*/
	}

	@Override
	public void parse(String input_file_name, String output_file_name) {	
		
		List<String> relevant_tables;
		try{
		relevant_tables = Files.readAllLines(
				FileSystems.getDefault().getPath(input_file_name)
				);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//filter irrelevant data
		//ALERT: 24 hours files contain an extra empty column between FG and Via, this solution won't work for them
		ListIterator<String> it = relevant_tables.listIterator();
		boolean onTable = false;
		while(it.hasNext())
		{
			String line = it.next();
			if(!onTable){
	        Matcher m = checkTableStartPattern.matcher(line);
	        if (m.find()) onTable = true;
	        it.remove();
			}
			else{
				Matcher m = checkTableRowPattern.matcher(line);
		        if (!m.find()){
				it.remove();
				onTable = false;
				}else{
		        //check if is missing stuff
		        boolean even=true;
		        for(char c : line.toCharArray())
		        	if(c=='"')even= !even;
		        if(!even){
		        	//line = new String(line);
		        	it.remove();
		        	String rest = it.next();
		        	it.set(line+" "+rest);
		        }
				}
			}
		}
		
		//parse each element and output to file
		FileWriter output_file=null;
		try{
		output_file = new FileWriter(output_file_name);
		it = relevant_tables.listIterator();
		while(it.hasNext())
		{			
			Matcher matcher = getRowDataPattern.matcher(it.next());
			
			if(!matcher.matches()) continue;
			String zona = matcher.group(1);
			String mortos = matcher.group(2);
			String feridosgraves = matcher.group(3);
			String via = matcher.group(4);
			String km = matcher.group(5);
			km = km.replace("\"","");
			
			String LatS,LonS,LatN,LonN;
			
			switch(mode){
			case LDACS_EXCLUDE:
				if(km!=null&&check_if_road_Km_is_valid.matcher(km).matches()){
					output_file.write("--excluded to add:" +zona +" | " + mortos +" | " + feridosgraves +" | "+ via);
					continue;
				}
				break;
			case LDACS_EXCLUDE_LARGE:
				break;
			case LDACS_SEGMENT:
				break;
			case LDACS_SHARED:
				break;
			default:
				break;
			}
			output_file.write(buildFunction(zona,mortos,feridosgraves,via) + "\n");
		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
			if(output_file!=null) output_file.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	final static Pattern estnat = Pattern.compile("EN\\d");
	final static Pattern inttext = Pattern.compile("\\d+");
	private String buildFunction(String zona, String mortos, String feridosgraves, String via) {

		Matcher m = estnat.matcher(via);
		String O_VIA = via;
		if(m.find()) via = via.replace("EN", "N");
		
		m = inttext.matcher(mortos);
		if(!m.find()) return "--failed to add:" +zona +" | " + mortos +" | " + feridosgraves +" | "+ via;
		m = inttext.matcher(feridosgraves);
		if(!m.find()) return "--failed to add:" +zona +" | " + mortos +" | " + feridosgraves +" | "+ via;
		
		
		String[] res;
		try {
			res = GoogleGeocodingAPI.sendGet(zona+","+via);
			if(res==null) throw new Exception("null result");
		} catch (Exception e) {
			e.printStackTrace();
			return "--failed to add:" +zona +" | " + mortos +" | " + feridosgraves +" | "+ via;
		}
		return "select add_new_data_to_road_segments("
				/*+res[0]+","
				+res[1]+","
				+res[2]+","
				+res[3]+","*/
				+res[4]+","
				+res[5]+","
				+"'"+O_VIA+"',"
				+mortos+","
				+feridosgraves
				+");";
	}
}


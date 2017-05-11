package parseANSR2013;

public class Main {

	public static void main(String[] args) throws Exception { 
		
		if (args.length!=4) System.out.println("usage: <mode> <input file path> <output file path> <Goole API key>");

		IParser parser = (MODE.valueOf(args[0].toUpperCase())).getParser();
		parser.parse(args[1], args[2]);
		GoogleGeocodingAPI.setKey(args[3]);
	}
	
	
	
}

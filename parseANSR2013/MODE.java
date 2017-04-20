package parseANSR2013;

public enum MODE {
	
	/**will exclude accidents on long roads*/
	LDACS_EXCLUDE_LARGE{
		public IParser getParser(){return new LDACS(LDACS_EXCLUDE_LARGE);}
	},
	/**will exclude accidents that specify the Km column*/
	LDACS_EXCLUDE{
		public IParser getParser(){return new LDACS(LDACS_EXCLUDE);}
	},
	/**when the accident specifies the Km the data is distributed for all of the road segments*/
	LDACS_SHARED{
		public IParser getParser(){return new LDACS(LDACS_SHARED);}
	},
	/**when the accident */
	LDACS_SEGMENT{
		public IParser getParser(){return new LDACS(LDACS_SEGMENT);}
	};
	
	public abstract IParser getParser();
	
}

package SmartShopping.OV;

public class RepSmartList extends OVRep{

	private OVSmartList smartList;
	
	public RepSmartList(String jsonStr){
		this.smartList = new OVSmartList(jsonStr);
		
	}

	public OVSmartList getSmartList() {
		return smartList;
	}

	public void setSmartList(OVSmartList smartList) {
		this.smartList = smartList;
	}

}

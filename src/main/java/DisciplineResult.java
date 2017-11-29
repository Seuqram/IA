
public enum DisciplineResult {
	APROVADO("APROVADO"), REPROVADO("REPROVADO"), NAO_CURSOU("NAO_CURSOU");
	
private String text;
	
	DisciplineResult(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static DisciplineResult fromString(String text) {
	    for (DisciplineResult b : DisciplineResult.values()) 
	    	if (b.text.equalsIgnoreCase(text)) 
	    		return b;
	    return null;
	 }
}

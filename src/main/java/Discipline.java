import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class Discipline {
	public static enum statusEnum {
		APROVADO, REPROVADO, NAO_CURSOU;
	}
	
	private String key;
	private statusEnum result;
}

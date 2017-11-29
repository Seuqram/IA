import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Discipline {

	private String key;
	private DisciplineResult result;

	/**
	 * @param discipline
	 * @return if the given discipline is equal to this discipline
	 */
	public boolean equals(Discipline discipline) {
		if (discipline.key.equalsIgnoreCase(this.key))
			if (discipline.result.equals(this.result))
				return true;
		return false;
	}
}

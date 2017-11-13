import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SimpleAssociationRule {
	@Getter @Setter
	private Discipline keyDisciplne;
	@Getter
	private ArrayList<Discipline> associatedDisciplines;
	
	public void print() {
		associatedDisciplines.forEach(discipline -> {
			System.out.print(discipline.getKey() + ": " + discipline.getResult() + " | ");
		});
		System.out.println();
		System.out.println(keyDisciplne.getKey() + " " +keyDisciplne.getResult());
		System.out.println();
	}
}

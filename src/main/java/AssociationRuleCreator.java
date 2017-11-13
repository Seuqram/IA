import java.util.ArrayList;
import java.util.Collection;

import weka.associations.AssociationRule;
import weka.associations.Item;

public class AssociationRuleCreator {
	private static AssociationRuleCreator creator = null;
	
	private AssociationRuleCreator() {};
	
	public static AssociationRuleCreator getInstance() {
		if (creator == null)
			creator = new AssociationRuleCreator();
		return creator;
	}
	
	public SimpleAssociationRule generateRule(AssociationRule rule, String keyDisciplineName) {
		boolean premiseContainsKey = itemListContainsKeyDiscipline(rule.getPremise(), keyDisciplineName);
		boolean consequenceContainsKey = itemListContainsKeyDiscipline(rule.getConsequence(), keyDisciplineName);
		Discipline keyDiscipline = null;
		ArrayList<Discipline> disciplineList = new ArrayList<>();
		if (!premiseContainsKey) {
			if (consequenceContainsKey) {
				if (rule.getConsequence().size() == 1) {
					for (Item item : rule.getPremise()){
						Discipline discipline = createDiscipline(item);
						if (itemNameMatchesString(item, keyDisciplineName))
							keyDiscipline = discipline;
						else
							disciplineList.add(discipline);
					}
					for (Item item : rule.getConsequence()){
						Discipline discipline = createDiscipline(item);
						if (itemNameMatchesString(item, keyDisciplineName))
							keyDiscipline = discipline;
						else
							disciplineList.add(discipline);
					}
					return new SimpleAssociationRule(keyDiscipline, disciplineList);
				}
			}
		}
			return null;
	}
	
	/**
	 * @param itemList
	 * @param key
	 * @return
	 */
	private boolean itemListContainsKeyDiscipline(Collection<Item> itemList, String key) {
		for (Item item : itemList)
			if (itemNameMatchesString(item, key))
				return true;
		return false;
	}
	
	private boolean itemNameMatchesString(Item item, String key) {
		return item.getAttribute().name().equals(key);
	}
	
	private Discipline createDiscipline(Item item) {
		Discipline.statusEnum status = null;
		String name = item.getAttribute().name();
		switch (item.getItemValueAsString()) {
		case "A":
			status = Discipline.statusEnum.APROVADO;
			break;
		case "R":
			status = Discipline.statusEnum.REPROVADO;
			break;
		case "N":
			status = Discipline.statusEnum.NAO_CURSOU;
			break;

		default:
			break;
		}
		return new Discipline(name, status);
	}
}

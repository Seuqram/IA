import java.util.ArrayList;
import java.util.Collection;

import weka.associations.AssociationRule;
import weka.associations.Item;

public class AssociationRuleCreator extends RuleCreator {
	private static AssociationRuleCreator creator = null;

	private AssociationRuleCreator() {
	};

	public static AssociationRuleCreator getInstance() {
		if (creator == null)
			creator = new AssociationRuleCreator();
		return creator;
	}

	/**
	 * The method receives a rule and checks if the rule premise contains the
	 * keyDiscipline. If it does not check if the consequence contains the
	 * keyDiscipline. If it does then create a rule with all the disciplines inside
	 * the rule's premise and with the keyDiscipline as keyDiscipline of the rule
	 * 
	 * @param rule
	 * @param keyDisciplineName
	 * @return created rule or null
	 */
	public SimpleAssociationRule generateRule(AssociationRule rule, String keyDisciplineName) {
		boolean premiseContainsKey = itemListContainsKeyDiscipline(rule.getPremise(), keyDisciplineName);
		boolean consequenceContainsKey = itemListContainsKeyDiscipline(rule.getConsequence(), keyDisciplineName);
		Discipline keyDiscipline = null;
		ArrayList<Discipline> disciplineList = new ArrayList<>();
		if (!premiseContainsKey) {
			if (consequenceContainsKey) {
				if (rule.getConsequence().size() == 1) {
					for (Item item : rule.getPremise()) {
						Discipline discipline = createDiscipline(item);
						if (itemNameMatchesString(item, keyDisciplineName))
							keyDiscipline = discipline;
						else
							disciplineList.add(discipline);
					}
					for (Item item : rule.getConsequence()) {
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

	/**
	 * @param item
	 * @param key
	 * @return if the item name matches the given string
	 */
	private boolean itemNameMatchesString(Item item, String key) {
		return item.getAttribute().name().equals(key);
	}

	/**
	 * @param item
	 * @return create the discipline with the name as the item name and the result
	 *         as the item value
	 */
	private Discipline createDiscipline(Item item) {
		DisciplineResult status = getDisciplineStatus(item.getItemValueAsString());
		String name = item.getAttribute().name();
		return new Discipline(name, status);
	}

	/**
	 * @param consequence
	 * @param rules
	 * @return an ArrayList of the rules with the given consequence, if they exist,
	 *         if not return null
	 */
	public ArrayList<SimpleAssociationRule> getRulesForGivenConsequence(String consequence,
			ArrayList<AssociationRule> rules) {
		ArrayList<SimpleAssociationRule> simpleRules = new ArrayList<>();
		rules.forEach(rule -> {
			SimpleAssociationRule simpleRule = creator.generateRule(rule, consequence);
			if (simpleRule != null)
				simpleRules.add(creator.generateRule(rule, consequence));
		});
		return simpleRules;
	}

	/**
	 * @param value
	 *            a string with only one character representing the discipline
	 *            status
	 * @return the status depending on the given value
	 */
	public DisciplineResult getDisciplineStatus(String value) {
		switch (value) {
		case "A":
			return DisciplineResult.APROVADO;
		case "R":
			return DisciplineResult.REPROVADO;
		case "N":
			return DisciplineResult.NAO_CURSOU;

		default:
			return null;
		}
	}

	/**
	 * @param rules
	 * @param key
	 * @return
	 */
	public ArrayList<SimpleAssociationRule> getRulesForGivenPremisse(ArrayList<SimpleAssociationRule> rules,
			String key) {
		ArrayList<SimpleAssociationRule> result = new ArrayList<>();
		rules.forEach(simpleRule -> {
			simpleRule.getAssociatedDisciplines().forEach(discipline -> {
				if (discipline.getKey().equalsIgnoreCase(key)) {
					result.add(simpleRule);
				}
			});
		});
		return result;
	}

	public ArrayList<SimpleAssociationRule> getRulesForGivenPremisseAndResult(ArrayList<SimpleAssociationRule> rules,
			String key, DisciplineResult status) {
		Discipline givenDiscipline = new Discipline(key, status);
		ArrayList<SimpleAssociationRule> result = new ArrayList<>();
		rules.forEach(simpleRule -> {
			simpleRule.getAssociatedDisciplines().forEach(discipline -> {
				if (discipline.equals(givenDiscipline)) {
					result.add(simpleRule);
				}
			});
		});
		return result;
	}

}

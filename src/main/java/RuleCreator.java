import java.util.ArrayList;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.Item;
import weka.core.Instances;

public class RuleCreator {
	private static RuleCreator creator = null;

	public static RuleCreator getInstance() {
		if (creator == null)
			creator = new RuleCreator();
		return creator;
	}

	public Apriori getAprioriRules(Instances data) {
		Apriori apriori = new Apriori();
		apriori.setNumRules(1000000000);
		apriori.setClassIndex(data.classIndex());
		try {
			apriori.buildAssociations(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apriori;
	}

	public ArrayList<AssociationRule> getRulesForStringInConsequence(String parameter, Apriori apriori) {
		ArrayList<AssociationRule> rules = new ArrayList<>();
		for (AssociationRule rule : apriori.getAssociationRules().getRules()) {
			Boolean cons = false;
			Boolean prem = false;
			for (Item consequence : rule.getConsequence()) {
				if (itemNameMatchesGivenName(consequence, parameter)) {
					cons = true;
				}
			}
			for (Item premise : rule.getPremise()) {
				if (itemNameMatchesGivenName(premise, parameter)) {
					prem = true;
				}
			}
			if (cons != prem) {
				if (cons || prem)
					rules.add(rule);
			}
		}
		return rules;
	}

	public boolean itemNameMatchesGivenName(Item item, String name) {
		return item.getAttribute().name().equals(name);
	}
}

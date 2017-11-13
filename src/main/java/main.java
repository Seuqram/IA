 import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.Item;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class main {
	public ArrayList<Discipline> rules = new ArrayList<>();
	public static void main(String[] args) {
		String TPII = "TPII";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader("C:\\Users\\rodri\\Documents\\Book2.csv.arff"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instances data = null;
		try {
			data = new Instances(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// build associator
		Apriori apriori = new Apriori();
		apriori.setNumRules(1000000000);
		apriori.setClassIndex(data.classIndex());
		try {
			apriori.buildAssociations(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Boolean printRule = false;
		ArrayList<AssociationRule> rules = new ArrayList<>();
		Boolean addRule = false;
		for (AssociationRule rule : apriori.getAssociationRules().getRules()) {
			Boolean cons = false;
			Boolean prem = false;
			for (Item consequence : rule.getConsequence()) {
				if (itemNameMatchesGivenName(consequence, TPII)) { 
					cons = true;
				}
			}
			for (Item premise : rule.getPremise()) {
				if(itemNameMatchesGivenName(premise, TPII)) {
					prem = true;
				}
			}
			if (cons != prem) {
				if (cons || prem)
					rules.add(rule);
			}
		}
		
		AssociationRuleCreator creator = AssociationRuleCreator.getInstance();
		ArrayList<SimpleAssociationRule> simpleRules = new ArrayList<>();
		rules.forEach(rule -> {
			SimpleAssociationRule simpleRule = creator.generateRule(rule, "TPII");
			if (simpleRule != null)
				simpleRules.add(creator.generateRule(rule, "TPII"));
		});
		simpleRules.forEach(simpleRule -> simpleRule.print());
	}
	
	public static boolean itemNameMatchesGivenName(Item item, String name) {
		return item.getAttribute().name().equals("TPII");
	}
	public static void print(Object objectToPrint) {
		System.out.print(objectToPrint);
	}
	
	public static void println(Object objectToPrint) {
		System.out.println(objectToPrint);
	}

}

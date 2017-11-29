import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.print.DocFlavor.CHAR_ARRAY;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.AbstractInferrer;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.core.Instances;

@SuppressWarnings("deprecation")
public class main {
	public ArrayList<Discipline> rules = new ArrayList<>();

	public static void main(String[] args) {
		String tpiiString = "TPII";
		ArffReader reader = ArffReader.getInstance();
		RuleCreator ruleCreator = RuleCreator.getInstance();
		AssociationRuleCreator creator = AssociationRuleCreator.getInstance();
		BayesNodeManipulator nodeManipulator = BayesNodeManipulator.getInstance();

		Instances data = reader.getInstances("Book2.csv.arff");
		Apriori apriori = ruleCreator.getAprioriRules(data);
		ArrayList<AssociationRule> rules = ruleCreator.getRulesForStringInConsequence(tpiiString, apriori);
		ArrayList<SimpleAssociationRule> simpleRules = creator.getRulesForGivenConsequence(tpiiString, rules);
		BayesNet bayesNet = new BayesNet();

		BayesNode FSI = nodeManipulator.createNodeWithoutParent(bayesNet, "FSI", simpleRules);
		BayesNode OC = nodeManipulator.createNodeWithParent(bayesNet, "OC", FSI, simpleRules);
		BayesNode TPI = nodeManipulator.createNodeWithParent(bayesNet, "TPI", OC, simpleRules);
		BayesNode MB = nodeManipulator.createNodeWithParent(bayesNet, "MB", TPI, simpleRules);
		BayesNode TPD = nodeManipulator.createNodeWithParent(bayesNet, "TPD", MB, simpleRules);
		BayesNode DPW = nodeManipulator.createNodeWithParent(bayesNet, "DPW", TPD, simpleRules);
		BayesNode keyNode = nodeManipulator.createKeyDiscipline(bayesNet, "TPII", DPW, simpleRules);

		JunctionTreeAlgorithm inferer = new JunctionTreeAlgorithm();
		inferer.setNetwork(bayesNet);
		inferer.getFactory().setUseLogScale(true);

		Map<BayesNode, String> evidence = new HashMap<BayesNode, String>();
		println("ENTRE COM OS DADOS DO HISTÓRICO DO ALUNO PARA CADA MATÉRIA");
		println("OPÇÕES:");
		println("1 - APROVADO");
		println("2 - REPROVADO");
		println("3 - NAO_CURSOU");
		println("4 - NÃO PREENCHER");
		Scanner scanner = new Scanner(System.in);
		int keyNodeStatus = 0;
		for (BayesNode node : bayesNet.getNodes()) {
			print(node.getName());
			print(": ");
			int enteredInt = scanner.nextInt();
			if (node.equals(keyNode)) {
				if (enteredResultIsValid(enteredInt)) {
					keyNodeStatus = enteredInt - 1;
				}
			} else {
				if (!getEnteredResult(enteredInt).equals(""))
					evidence.put(node, getEnteredResult(enteredInt));
			}

		}
		inferer.setEvidence(evidence);

		double[] beliefsC = inferer.getBeliefs(keyNode);
		System.out.println(beliefsC[keyNodeStatus]);
	}

	public static void print(Object object) {
		System.out.print(object);
	}

	public static boolean enteredResultIsValid(int enteredInt) {
		return (enteredInt > 0 && enteredInt < 3);
	}

	public static String getEnteredResult(int enteredInt) {
		switch (enteredInt) {
		case 1:
			return "APROVADO";
		case 2:
			return "REPROVADO";
		case 3:
			return "NÃO CURSOU";
		default:
			return "";
		}
	}

	public static void println(Object object) {
		System.out.println(object);
	}

}

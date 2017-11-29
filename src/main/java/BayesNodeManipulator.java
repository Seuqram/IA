import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;

public class BayesNodeManipulator {
	private static BayesNodeManipulator manipulator = null;

	public static BayesNodeManipulator getInstance() {
		if (manipulator == null)
			manipulator = new BayesNodeManipulator();
		return manipulator;
	}

	public BayesNode createNodeWithParent(BayesNet bayesNet, String nodeName, BayesNode parentNode,
			ArrayList<SimpleAssociationRule> associationRules) {
		BayesNode newNode = bayesNet.createNode(nodeName);
		for (DisciplineResult status : DisciplineResult.values()) {
			newNode.addOutcome(status.toString());
		}
		newNode.setParents(Arrays.asList(parentNode));
		newNode.setProbabilities(getProbabilityWithParent(newNode, associationRules));
		return newNode;
	}

	public BayesNode createNodeWithoutParent(BayesNet bayesNet, String nodeName,
			ArrayList<SimpleAssociationRule> associationRules) {
		Map<DisciplineResult, Double> mapFSI = new HashMap<>();
		for (DisciplineResult status : DisciplineResult.values()) {
			mapFSI.put(status, getProbability(associationRules, nodeName, status));
		}
		BayesNode FSI = bayesNet.createNode(nodeName);
		for (DisciplineResult status : DisciplineResult.values()) {
			FSI.addOutcome(status.toString());
		}
		FSI.setProbabilities(mapFSI.get(DisciplineResult.APROVADO), mapFSI.get(DisciplineResult.REPROVADO),
				mapFSI.get(DisciplineResult.NAO_CURSOU));
		return FSI;
	}

	public BayesNode createKeyDiscipline(BayesNet bayesNet, String nodeName, BayesNode parentNode,
			ArrayList<SimpleAssociationRule> simpleRules) {
		BayesNode keyDisciplineNode = bayesNet.createNode(nodeName);
		AssociationRuleCreator creator = AssociationRuleCreator.getInstance();
		for (DisciplineResult status : DisciplineResult.values()) {
			keyDisciplineNode.addOutcome(status.toString());
		}
		keyDisciplineNode.setParents(Arrays.asList(parentNode));
		ArrayList<Double> ad = new ArrayList<>();
		for (String outcome : parentNode.getOutcomes()) {
			DisciplineResult parentOutcome = DisciplineResult.fromString(outcome);
			ArrayList<SimpleAssociationRule> a = creator.getRulesForGivenPremisseAndResult(simpleRules,
					parentNode.getName(), parentOutcome);
			for (DisciplineResult status : DisciplineResult.values()) {
				double d = 0;
				for (SimpleAssociationRule rule : a) {
					if (rule.getKeyDisciplne().getResult().equals(status)) {
						d++;
					}
				}
				ad.add(d / simpleRules.size());
			}
		}

		double[] keyDisciplineProbabilities = new double[9];
		for (int index = 0; index < 9; index++) {
			keyDisciplineProbabilities[index] = ad.get(index);
		}
		keyDisciplineNode.setProbabilities(
				getProbabilityWithParentKeyDiscipline(keyDisciplineNode.getParents().get(0), simpleRules));
		return keyDisciplineNode;
	}

	private double[] getProbabilityWithParentKeyDiscipline(BayesNode parentNode,
			ArrayList<SimpleAssociationRule> simpleRules) {
		ArrayList<Double> ad = new ArrayList<>();
		for (String outcome : parentNode.getOutcomes()) {
			DisciplineResult parentOutcome = DisciplineResult.fromString(outcome);
			ArrayList<SimpleAssociationRule> a = AssociationRuleCreator.getInstance()
					.getRulesForGivenPremisseAndResult(simpleRules, parentNode.getName(), parentOutcome);
			for (DisciplineResult status : DisciplineResult.values()) {
				double d = 0;
				for (SimpleAssociationRule rule : a) {
					if (rule.getKeyDisciplne().getResult().equals(status)) {
						d++;
					}
				}
				ad.add(d / simpleRules.size());
			}
		}

		double[] keyDisciplineProbabilities = new double[9];
		for (int index = 0; index < 9; index++) {
			keyDisciplineProbabilities[index] = ad.get(index);
		}
		return keyDisciplineProbabilities;

	}

	private double[] getProbabilityWithParent(BayesNode node, ArrayList<SimpleAssociationRule> simpleRules) {
		ArrayList<Double> aDouble = new ArrayList<>();
		AssociationRuleCreator creator = AssociationRuleCreator.getInstance();
		if (node.getParents().size() > 0) {
			for (BayesNode parent : node.getParents()) {
				for (String outcome : parent.getOutcomes()) {
					DisciplineResult parentOutcome = DisciplineResult.fromString(outcome);
					ArrayList<SimpleAssociationRule> a = creator.getRulesForGivenPremisseAndResult(simpleRules,
							parent.getName(), parentOutcome);
					for (DisciplineResult status : DisciplineResult.values()) {
						double probability = getProbability(a, node.getName(), status);
						if (Double.isNaN(probability)) {
							probability = 0;
						} else {
							BigDecimal bd = new BigDecimal(probability);
							bd = bd.setScale(2, RoundingMode.HALF_UP);
							probability = bd.doubleValue();
						}
						aDouble.add(probability);
					}
				}
			}

		}

		double[] probDouble = new double[9];
		for (int index = 0; index < aDouble.size(); index++) {
			probDouble[index] = aDouble.get(index);
		}
		return probDouble;
	}

	private double getProbability(ArrayList<SimpleAssociationRule> simpleRules, String disciplineKey,
			DisciplineResult status) {
		Discipline createdDiscipline = new Discipline(disciplineKey, status);
		ArrayList<SimpleAssociationRule> ruls = new ArrayList<>();
		simpleRules.forEach(simpleRule -> {
			simpleRule.getAssociatedDisciplines().forEach(discipline -> {
				if (discipline.equals(createdDiscipline)) {
					ruls.add(simpleRule);
				}
			});
		});
		double result = (double) ruls.size() / (double) simpleRules.size();
		return result;
	}
}

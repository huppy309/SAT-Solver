import java.util.*;

public class DPLLSolver
{
	private int modelSize;
	private int numberOfLiterals;
	private int[] assignCount;
	private ArrayList<Literal> lastGuess;
	private ArrayList<Literal> model;
	private ArrayList<Literal> workingSet;

	public DPLLSolver(int _numberOfLiterals) 
	{
		modelSize = 0;
		numberOfLiterals = _numberOfLiterals;
		assignCount = new int[numberOfLiterals];
		lastGuess = new ArrayList<Literal>();

		/* Initialize the model */
		model = new ArrayList<Literal>();

		/* Construct a working set of possible literals for the model */
		workingSet = new ArrayList<Literal>();

		for(int i = 1 ; i <= numberOfLiterals ; i++)
		{
			workingSet.add(new Literal(i, true));
		}
	}

	/* Sorted insert for adding literals to working set */
	private void addToWorkingSet(Literal guess)
	{
		for(int i = 0 ; i < workingSet.size() ; i++)
		{
			if(guess.get() < workingSet.get(i).get())
			{
				workingSet.add(i, new Literal(guess.get(), guess.getTruth()));
				return;
			}
		}
		workingSet.add(new Literal(guess.get(), guess.getTruth()));
	}

	/* Finds the truth value of Literal with value 'lit' in the model */
	private boolean truthValInModel(ArrayList<Literal> model, int lit)
	{
		for(Literal l : model)
		{
			if(l.get() == lit)
			{
				return l.getTruth();
			}
		}

		/* CODE SHOULD NOT REACH THIS POINT */
		assert(false);
		return false;
	}

	/* Unit Propagation. Returns:
			1: Unit clause exists/ Unit propagation performed
			0: No unit clause exists.
		   -1: Conflict 
		   -2: Unsat 		*/
	private int deduce(ArrayList<Clause> conjuncts)
	{
		/* Find a unit clause */
		for(Clause c : conjuncts)
		{
			ArrayList<Literal> disjuncts = c.get();

			/* Single literal unit clause */
			if(disjuncts.size() == 1)
			{
				Literal unit = disjuncts.get(0);
				if(assignCount[unit.get() - 1] == 0)
				{
					/* Add the literal assignment to the model */
					model.add(new Literal(unit.get(), unit.getTruth()));
					modelSize++;

					/* Remove literal from working set */
					for(int i = 0 ; i < workingSet.size() ; i++)
					{
						if(workingSet.get(i).get() == unit.get())
						{
							workingSet.remove(i);
							i--;
						}
					}

					/* Increment assign count for this literal */
					assignCount[unit.get() - 1]++;

					return 1;
				}
				/* Literal already assigned a value */
				else
				{
					/* Check for contradiction */
					if(!unit.getTruth() && truthValInModel(model, unit.get()))
					{
						return -1;
					}
					if(unit.getTruth() && !truthValInModel(model, unit.get()))
					{
						return -1;
					}
				}
			}
			/* Check for only one unassigned literal */
			else
			{
				/* Find how many literals are unassigned */
				int unassignedCount = 0;
				Literal unit = null;

				for(Literal lit : disjuncts)
				{
					if(assignCount[lit.get() - 1] == 0)
					{
						unassignedCount++;
						unit = lit;
					}
				}

				/* Check if it is unit clause */
				if(unassignedCount == 1)
				{
					/* Add the literal assignment to the model */
					model.add(new Literal(unit.get(), unit.getTruth()));
					modelSize++;

					/* Increment assign count for this literal */
					assignCount[unit.get() - 1]++;

					return 1;
				}
				/* Check for conflict */
				else if(unassignedCount == disjuncts.size())
				{
					/* Compute the disjunction of all disjuncts */
					boolean clauseTruth = false;
					
					for(Literal lit : disjuncts)
					{
						boolean modelVal = truthValInModel(model, lit.get());
						boolean clauseVal = lit.getTruth();
						
						if(!modelVal && !clauseVal)
						{
							clauseTruth = clauseTruth || true;
						}
						else if(modelVal && clauseVal)
						{
							clauseTruth = clauseTruth || true;
						}
						else
						{
							clauseTruth = clauseTruth || false;
						}
					}

					/* Conflict present if false */
					if(!clauseTruth)
					{
						return -1;
					}
				}
			}
		}

		/* No unit clause found */
		return 0;
	}

	/* Branching: choose the next value from the working set. Returns:
			true: Successful Pure Literal Assignment 
		    false: Unsat  	*/
	private boolean guess()
	{
		/* Check unsatisfiablity */
		if(workingSet.size() == 0)
		{
			return false;
		}
		
		/* Get next guess value from the working set */
		Literal pop = workingSet.remove(0);

		/* Add to the model */
		model.add(new Literal(pop.get(), pop.getTruth()));
		modelSize++;

		/* Increment assign count for this literal */
		assignCount[pop.get() - 1]++;

		/* Mark this guess as the most recent one */
		lastGuess.add(pop);

		return true;
	}

	/* Reconstruct the model and workingSet to the last checkpoint at the most recent guess */
	private void backTrack()
	{
		/* Restore the model and workingSet to the state at the most recent guess */
		Literal guess = null;

		while(guess == null)
		{
			guess = model.remove(modelSize - 1);

			/* The most recent guess */
			if(guess.get() == lastGuess.get(lastGuess.size() - 1).get())
			{
				if(guess.getTruth())
				{
					addToWorkingSet(guess);
				}
			}
			/* Literals deduced using unit propagation */
			else
			{
				addToWorkingSet(guess);
				guess = null;
			}

			modelSize--;
		}
	}

	/* This method performs the DPLL algorithm on the conjuncts in order to find a model */
	public ArrayList<Literal> findModel(ArrayList<Clause> conjucts)
	{
		/* Work on the model recursively until solution found */
		while(modelSize != numberOfLiterals)
		{
			/* Attempt deduction */
			int deduction = deduce(conjucts);

			/* Unit propagation not possible. Must guess a literal value */
			if(deduction == 0)
			{
				/* Perform Pure Literal Assignment. Return Unsat if no more guesses left */
				if(!guess())
				{
					return null;
				}
			}
			/* Conflict found. Must backtrack to last guess */
			else if(deduction == -1)
			{
				/* If no guess was made */
				if(lastGuess.size() == 0)
				{
					return null;
				}

				/* Restore state to most recent guess */
				backTrack();
			}
			/* Unsatisfiability confirmed */
			else if(deduction == -2)
			{
				return null;
			}
		}

		return model;
	}
}
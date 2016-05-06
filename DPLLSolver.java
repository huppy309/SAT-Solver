import java.util.ArrayList;

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
			addToWorkingSet(new Literal(i, true));
		}
	}

	/* Sorted insert for adding literals to working set */
	private void addToWorkingSet(Literal guess)
	{
		// System.out.println(guess.toString());
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

	/* Removal of literal 'unit' from working set */
	private void removeFromWorkingSet(Literal unit)
	{
		for(int i = 0 ; i < workingSet.size() ; i++)
		{
			if(workingSet.get(i).get() == unit.get())
			{
				workingSet.remove(i);
				return;
			}			
		}
	}

	/* Checks if the current assignment to the disjuncts yields 'true' */
	private boolean checkFormula(ArrayList<Literal> disjuncts)
	{
		boolean clauseTruth = false;
					
		/* Compute the disjunction of all disjuncts */			
		for(Literal lit : disjuncts)
		{
			if(assignCount[lit.get() - 1] > 0)
			{
				boolean modelVal = truthValInModel(model, lit.get());
				boolean clauseVal = lit.getTruth();
				
				if(modelVal == clauseVal)
				{
					clauseTruth = clauseTruth || true;
				}
				else
				{
					clauseTruth = clauseTruth || false;
				}
			}
		}

		return clauseTruth;
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
		   -1: Conflict 		*/
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
					removeFromWorkingSet(unit);

					/* Increment assign count for this literal */
					assignCount[unit.get() - 1]++;

					return 1;
				}
				/* Literal already assigned a value */
				else
				{
					/* Check for contradiction */
					if(unit.getTruth() != truthValInModel(model, unit.get()))
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
					if(!checkFormula(disjuncts))
					{
						/* Add the literal assignment to the model */
						model.add(new Literal(unit.get(), unit.getTruth()));
						modelSize++;

						/* Remove literal from working set */
						removeFromWorkingSet(unit);
					
						/* Increment assign count for this literal */
						assignCount[unit.get() - 1]++;

						return 1;
					}
				}
				/* Check for conflict */
				else if(unassignedCount == 0)
				{
					/* Conflict present if false */
					if(!checkFormula(disjuncts))
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
				lastGuess.remove(lastGuess.size() - 1);
				assignCount[guess.get() - 1] = 0;
				
				if(guess.getTruth())
				{
					/* DEBUGGING */
					// System.out.println(guess.toString());
					
					/* Guess the negated literal now */
					model.add(new Literal(guess.get(), false));
					modelSize++;

					/* Increment assign count for this literal */
					assignCount[guess.get() - 1]++;

					/* Mark this guess as the most recent one */
					lastGuess.add(new Literal(guess.get(), false));
				}
				else
				{
					guess = null;
				}
			}
			/* Literals deduced using unit propagation */
			else
			{
				addToWorkingSet(guess);
				assignCount[guess.get() - 1] = 0;
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
			/* DEBUGGING */
			for(Literal c : model)
				System.out.print(c.toString() + " ");
			System.out.println();

			// for(Literal c : lastGuess)
			// 	System.out.print(c.toString() + " ");
			// System.out.println();

			
			/* Attempt deduction */
			int deduction = deduce(conjucts);

			/* Unit propagation not possible. Must guess a literal value */
			if(deduction == 0)
			{
				/* Perform Pure Literal Assignment. Return Unsat if no more guesses left */
				if(!guess())
				{
					System.out.println("WORKSET EMPTIED");
					return null;
				}
			}
			/* Conflict found. Must backtrack to last guess */
			else if(deduction == -1)
			{
				/* If no guess was made */
				if(lastGuess.size() == 0)
				{
					System.out.println("NO GUESSES");
					return null;
				}

				/* Restore state to most recent guess */
				backTrack();
			}
			/* Unsatisfiability confirmed */
			else if(deduction == -2)
			{
				System.out.println("UNSATISFIABLE");
				return null;
			}
		}

		return model;
	}
}
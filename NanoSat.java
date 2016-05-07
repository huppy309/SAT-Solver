/**
 * Date 05/01/2016
 * @author Hassan Zaidi
 */

import java.util.ArrayList;

public class NanoSat
{
	private static DimacsParser parser;
	private static DPLLSolver solver;
	private static ArrayList<Clause> conjuncts;

	/* Prints the returned model from the solver */
	private static void printModel(ArrayList<Literal> model)
	{
		for(Literal lit : model)
		{
			if(lit.getTruth())
			{
				System.out.print(lit.get());
				System.out.print(" ");
			}
			else
			{
				System.out.print(lit.get() * -1);
				System.out.print(" ");
			}
		}

		System.out.println();
	}

	public static void main(String[] args)
	{
		/* Check correct argument usage:
		       args[0] = dimacs file */
		if(args.length != 1)
		{
			System.out.println("Usage: java NanoSat <file>");
			System.exit(0);
		}

		/* Initialize parser and parse input file */
		parser = new DimacsParser(args[0]);
		conjuncts = parser.parseDimacs();

		/* DEBUGGING */
		// for(Clause c : conjuncts)
		// 	System.out.println(c);

		/* Apply DPLL and return the model */
		solver = new DPLLSolver(parser.getNumLiterals());
		ArrayList<Literal> model = solver.findModel(conjuncts);

		/* Print output */
		if(model == null)
		{
			System.out.println("Unsat");
		}
		else
		{
			System.out.println("Sat\n");
			printModel(model);
		}
	}
}
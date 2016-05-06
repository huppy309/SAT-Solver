import java.util.ArrayList;

/* Defines a clause as a list of disjuncts */
public class Clause
{
	protected ArrayList<Literal> disjuncts;

	public Clause()
	{
		disjuncts = new ArrayList<Literal>();
	}

	public void addDisjunct(Literal lit)
	{
		disjuncts.add(lit);
	}

	public ArrayList<Literal> get()
	{
		return disjuncts;
	}

	/* Pretty print for a clause */
	@Override
	public String toString()
	{
		/* Construct a single string for the entire clause */
		StringBuilder clause; 

		if(disjuncts.size() == 1)
		{
			/* Pretty-print the one clause */
			clause = new StringBuilder(disjuncts.get(0).toString());
		}
		else
		{
			clause = new StringBuilder("(" + disjuncts.get(0).toString());

			/* Add the pretty-print version of each clause to the string */
			for(int i = 1 ; i < disjuncts.size() ; i++)
			{
				clause.append(" V " + disjuncts.get(i).toString());
			}

			clause.append(")");
		}
		return clause.toString();
	}
}
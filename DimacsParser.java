import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/* A class responsible for parsing files in the DIMACS format.
   The primary parseDimacs() method returns a list of clauses */
public class DimacsParser
{
	private String filename;
	private BufferedReader reader;
	private int numberOfLiterals;
	private int numberOfClauses;

	public DimacsParser(String _file)
	{
		filename = _file;
		numberOfLiterals = 0;
		numberOfClauses = 0;
	}

	public int getNumLiterals()
	{
		return numberOfLiterals;
	}

	/* Opens the file and returns a list of clauses parsed
	   from the file */
	public ArrayList<Clause> parseDimacs()
	{
		String line = "";
		String[] split;
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		boolean problemLineReached = false;

		/* Open the file */
		try
		{
			reader = new BufferedReader(new FileReader(filename));
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found! Exiting...");
			System.exit(0);
		}

		/* Read the file and catch the clauses */
		try
		{
			/* Read until the "problem" line is reached */
			while(!problemLineReached && (line = reader.readLine()) != null)
			{
				/* Debug: Output lines of input program */
				// System.out.println(line);

				/* Check if "problem" line has been reached */
				if(line.length() > 0 && line.charAt(0) == 'p')
				{
					problemLineReached = true;
				}
			}

			/* Read in the "problem" line */
			split = line.split("\\s+");

			numberOfLiterals = Integer.parseInt(split[2]);
			numberOfClauses = Integer.parseInt(split[3]);

			/* Parse all clauses */
			int clauseCounter = 0;
			boolean endOfClause = false;

			while(clauseCounter < numberOfClauses)
			{
				/* Initialize the clause */
				Clause clause = new Clause();

				/* Construct the clause */
				while(!endOfClause)
				{
					line = reader.readLine();
					split = line.split("\\s+");

					for(int j = 0 ; j < split.length ; j++)
					{
						if(!split[j].equals(""))
						{
							if(Integer.parseInt(split[j]) == 0)
							{
								endOfClause = true;
							}
							else
							{
								int literal = Integer.parseInt(split[j]);

								if(literal > 0)
								{
									clause.addDisjunct(new Literal(literal, true));
								}
								else
								{
									clause.addDisjunct(new Literal(literal * -1, false));
								}
							}
						}
					}
				}

				clauses.add(clause);
				clauseCounter++;
				endOfClause = false;
			}
		}
		catch(IOException e)
		{
			System.out.println("IOException! Exiting...");
			System.exit(0);
		}

		/* Close the file */
		try
		{
			reader.close();
		}
		catch(IOException e)
		{
			System.out.println("IOException! Exiting...");
			System.exit(0);
		}

		return clauses;
	}
}
/* Defines a literal as an integer value with a boolean truth value */
public class Literal
{
	private int lit;
	private boolean truthValue;

	public Literal(int _lit, boolean _truthValue)
	{
		lit = _lit;
		truthValue = _truthValue;
	}

	public int get()
	{
		return lit;
	}

	public boolean getTruth()
	{
		return truthValue;
	}

	public void set(boolean _truthValue)
	{
		truthValue = _truthValue;
	}

	@Override
	public String toString()
	{
		if(truthValue)
			return Integer.toString(lit);
		else
			return Integer.toString(lit * -1);
	}
}
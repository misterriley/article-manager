package common;

/**
 * @author Steve Riley
 */
public class SimpleDBCell
{
	private String m_value;

	/**
	 * @param p_value
	 */
	public SimpleDBCell(final String p_value)
	{
		m_value = p_value;
	}

	public boolean asBoolean()
	{
		return Boolean.parseBoolean(m_value);
	}

	/**
	 * @return The value of the cell parsed as a double
	 */
	public double asDouble()
	{
		return Double.parseDouble(m_value);
	}

	/**
	 * @return The value of the cell parsed as an int
	 */
	public int asInt()
	{
		return Integer.parseInt(m_value);
	}

	/**
	 * @return The String in this cell
	 */
	public String asString()
	{
		return m_value;
	}

	/**
	 * @return The value of this cell as a string
	 */
	public String getValue()
	{
		return m_value;
	}

	/**
	 * Sets the value of this cell
	 *
	 * @param p_value
	 */
	public void setValue(final String p_value)
	{
		m_value = p_value;
	}

	@Override
	public String toString()
	{
		return m_value;
	}
}

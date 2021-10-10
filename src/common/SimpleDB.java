package common;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This class is a simple database. It is structured like a spreadsheet with
 * columns.
 *
 * @author Steve Riley
 */
public class SimpleDB
{
	private final ArrayList<ArrayList<SimpleDBCell>>	m_db;
	private TreeMap<String, Integer>					m_columnMap;
	private ArrayList<String>							m_header;

	/**
	 * Creates a new, blank SimpleDB object
	 */
	public SimpleDB()
	{
		m_db = new ArrayList<>();
		m_columnMap = null;
		m_header = null;
	}

	/**
	 * Creates a new SimpleDB object with the given database and column map
	 *
	 * @param p_db
	 * @param p_columnMap
	 * @param p_header
	 */
	public SimpleDB(final ArrayList<ArrayList<SimpleDBCell>> p_db, final TreeMap<String, Integer> p_columnMap,
			final ArrayList<String> p_header)
	{
		m_db = p_db;
		m_columnMap = p_columnMap;
		m_header = p_header;
	}

	/**
	 * Adds an element to the header
	 *
	 * @param p_columnName
	 */
	public void addHeaderElement(final String p_columnName)
	{
		if (m_columnMap == null)
		{
			m_columnMap = new TreeMap<>();
		}

		if (m_header == null)
		{
			m_header = new ArrayList<>();
		}

		m_header.add(p_columnName);
		final int index = m_header.indexOf(p_columnName);
		m_columnMap.put(p_columnName, index);
	}

	/**
	 * @param p_rowIndex
	 * @param p_columnIndex
	 * @return String that is the contents of this cell
	 */
	public SimpleDBCell get(final int p_rowIndex, final int p_columnIndex)
	{
		final ArrayList<SimpleDBCell> row = m_db.get(p_rowIndex);
		return row.get(p_columnIndex);
	}

	/**
	 * @param p_rowIndex
	 * @param p_column
	 * @return String that is the contents of this cell
	 */
	public SimpleDBCell get(final int p_rowIndex, final String p_column)
	{
		final int columnIndex = m_columnMap.get(p_column);
		return get(p_rowIndex, columnIndex);
	}

	/**
	 * @param p_index
	 * @return The name of the column at the specified column index
	 */
	public String getColumnName(final int p_index)
	{
		if (hasHeader())
		{
			return m_header.get(p_index);
		}

		return null;
	}

	/**
	 * @return true if the columnMap object is not null
	 */
	public boolean hasHeader()
	{
		return m_columnMap != null;
	}

	/**
	 * @return The number of columns. First checks the size of the header, then
	 *         checks the size of the first row. If both of these are null, returns
	 *         0.
	 */
	public int numColumns()
	{
		if (hasHeader())
		{
			return m_columnMap.size();
		}

		if (m_db.isEmpty())
		{
			return 0;
		}

		final ArrayList<SimpleDBCell> firstRow = m_db.get(0);
		return firstRow.size();
	}

	/**
	 * @return The number of elements in the db object.
	 */
	public int numRows()
	{
		return m_db.size();
	}

	/**
	 * Ensures capacity for this row and column, then replaces the value of the
	 * specified location with the given value
	 *
	 * @param p_rowIndex
	 * @param p_columnIndex
	 * @param p_value
	 */
	public void set(final int p_rowIndex, final int p_columnIndex, final String p_value)
	{
		while (numRows() <= p_rowIndex)
		{
			m_db.add(new ArrayList<SimpleDBCell>());
		}

		final ArrayList<SimpleDBCell> row = m_db.get(p_rowIndex);
		while (row.size() <= p_columnIndex)
		{
			row.add(new SimpleDBCell(""));
		}

		row.get(p_columnIndex).setValue(p_value);
	}

	/**
	 * Sets the value at this location and ensures the db has capacity to hold an
	 * element at that location
	 *
	 * @param p_rowIndex
	 * @param p_column
	 * @param p_value
	 */
	public void set(final int p_rowIndex, final String p_column, final String p_value)
	{
		if (m_columnMap == null)
		{
			m_columnMap = new TreeMap<>();
		}

		if (!m_columnMap.containsKey(p_column))
		{
			addHeaderElement(p_column);
		}

		final int columnIndex = m_columnMap.get(p_column);
		set(p_rowIndex, columnIndex, p_value);
	}
}

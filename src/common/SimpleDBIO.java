package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Loads and saves SimpleDB objects
 *
 * @author Steve Riley
 */
public class SimpleDBIO
{
	private static final String DELIMITER = "\uE000";

	public static void addColumnToDB(final SimpleDB p_db, final String p_columnName, final String p_defaultValue)
	{
		p_db.addHeaderElement(p_columnName);

		for (int i = 0; i < p_db.numRows(); i++)
		{
			p_db.set(i, p_columnName, p_defaultValue);
		}
	}

	/**
	 * @param  p_file
	 * @param  p_hasHeader
	 *
	 * @return             A SimpleDB object constructed from this file
	 */
	public static SimpleDB loadFromFile(final File p_file, final boolean p_hasHeader)
	{
		final ArrayList<ArrayList<SimpleDBCell>> db = new ArrayList<>();
		TreeMap<String, Integer> columnMap = null;
		ArrayList<String> header = null;

		if (p_file != null && p_file.exists())
		{
			try (
				BufferedReader reader =
					new BufferedReader(new InputStreamReader(new FileInputStream(p_file), "UNICODE")))
			{
				boolean first = true;
				while (true)
				{
					final String line = reader.readLine();
					if (line == null)
					{
						break;
					}

					final String[] bits = line.split(DELIMITER);
					if (p_hasHeader && first)
					{
						columnMap = new TreeMap<>();
						header = new ArrayList<>();

						for (int i = 0; i < bits.length; i++)
						{
							columnMap.put(bits[i], i);
							header.add(bits[i]);
						}

						first = false;
					}
					else
					{
						final ArrayList<SimpleDBCell> rowList = new ArrayList<>();
						for (final String bit : bits)
						{
							rowList.add(new SimpleDBCell(bit));
						}
						db.add(rowList);
					}
				}
			}
			catch (final IOException ioex)
			{
				ioex.printStackTrace();
			}
		}

		return new SimpleDB(db, columnMap, header);
	}

	public static void main(final String[] p_args)
	{

	}

	/**
	 * @param p_db
	 * @param p_file
	 */
	public static void saveToFile(final SimpleDB p_db, final File p_file)
	{
		if (!p_file.exists())
		{
			try
			{
				p_file.createNewFile();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}

		try (
			final BufferedWriter writer =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(p_file), "Unicode")))
		{
			final int numColumns = p_db.numColumns();
			final int numRows = p_db.numRows();

			if (p_db.hasHeader())
			{
				String row = DELIMITER;
				for (int columnIndex = 0; columnIndex < numColumns; columnIndex++)
				{
					row += p_db.getColumnName(columnIndex);
					row += DELIMITER;
				}

				writer.write(row);
				writer.newLine();
			}

			for (int rowIndex = 0; rowIndex < numRows; rowIndex++)
			{
				String row = DELIMITER;
				for (int columnIndex = 0; columnIndex < numColumns; columnIndex++)
				{
					row += p_db.get(rowIndex, columnIndex);
					row += DELIMITER;
				}
				writer.write(row);
				writer.newLine();
			}
		}
		catch (final IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
}

package common;

import java.io.File;
import java.io.IOException;

public class ArticleData
{
	private String	m_title;
	private String	m_fileName;
	private double	m_rating;
	private int		m_numComparisons;
	private String	m_abstract;

	public ArticleData(String p_title, File p_file, double p_rating, int p_numComparisons, String p_abstract)
	{
		setTitle(p_title);
		try
		{
			m_fileName = p_file.getCanonicalPath();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setRating(p_rating);
		setAbstract(p_abstract);
	}

	public String getAbstract()
	{
		return m_abstract;
	}

	public String getFileName()
	{
		return m_fileName;
	}

	public int getNumComparisons()
	{
		return m_numComparisons;
	}

	public double getRating()
	{
		return m_rating;
	}

	public String getTitle()
	{
		return m_title;
	}

	public void incrementNumComparisons()
	{
		m_numComparisons++;
	}

	private void setAbstract(String p_abstract)
	{
		m_abstract = p_abstract;
	}

	public void setRating(double p_rating)
	{
		m_rating = p_rating;
	}

	public void setTitle(String p_title)
	{
		m_title = p_title;
	}
}

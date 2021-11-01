package common;

public class EloComparison
{
	private final ArticleData	m_articleA;
	private final ArticleData	m_articleB;
	private double				m_weight;

	public EloComparison(final ArticleData p_articleA, final ArticleData p_articleB, final double p_weight)
	{
		m_articleA = p_articleA;
		m_articleB = p_articleB;
		setWeight(p_weight);
	}

	public ArticleData getArticleA()
	{
		return m_articleA;
	}

	public ArticleData getArticleB()
	{
		return m_articleB;
	}

	public double getWeight()
	{
		return m_weight;
	}

	public void setWeight(final double p_weight)
	{
		m_weight = p_weight;
	}
}

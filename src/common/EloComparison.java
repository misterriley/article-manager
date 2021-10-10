package common;

public class EloComparison
{
	private final ArticleData m_articleA;
	private final ArticleData m_articleB;
	private double m_weight;

	public EloComparison(ArticleData p_articleA, ArticleData p_articleB, double p_weight)
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

	public void setWeight(double p_weight)
	{
		m_weight = p_weight;
	} 
}

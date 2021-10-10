package common;

public class ELO
{
	public static double getExpectedScoreForA(double p_ratingA, double p_ratingB)
	{
		return 1/(1 + Math.pow(Constants.ELO_BASE, (p_ratingB - p_ratingA)/Constants.ELO_SCALE));
	}
	
	public static double getNewRatingForA(double p_oldRatingA, double p_oldRatingB, double p_resultForA)
	{
		double expectedScoreForA = getExpectedScoreForA(p_oldRatingA, p_oldRatingB);
		return p_oldRatingA + Constants.K * (p_resultForA - expectedScoreForA);
	}
	
	public static double getMatchEntropy(double p_ratingA, double p_ratingB)
	{
		double expectedScoreForA = getExpectedScoreForA(p_ratingA, p_ratingB);
		
		if(expectedScoreForA == 0 || expectedScoreForA == 1)
		{
			return 0;
		}
		
		double expectedScoreForB = 1 - expectedScoreForA;
		
		return -1* (expectedScoreForA * Math.log(expectedScoreForA) + expectedScoreForB*Math.log(expectedScoreForB));
	}

	public static double getQScore(double p_rating, double p_bestRating)
	{
		return Math.pow(Constants.ELO_BASE, (p_rating - p_bestRating)/Constants.ELO_SCALE);
	}
	
	public static double getNoveltyAdjustedElo(ArticleData p_data)
	{
		double thisElo = p_data.getRating();
		
		if(p_data.getNumComparisons() <= Constants.NOVELTY_CEILING)
		{
			thisElo += (Constants.NOVELTY_CEILING - p_data.getNumComparisons()) * Constants.NOVELTY_ADDEND;
		}
		
		return thisElo;
	}
}
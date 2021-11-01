package common;

public class ELO
{
	public static double getExpectedScoreForA(final double p_ratingA, final double p_ratingB)
	{
		return 1 / (1 + Math.pow(Constants.ELO_BASE, (p_ratingB - p_ratingA) / Constants.ELO_SCALE));
	}

	public static double getMatchEntropy(final double p_ratingA, final double p_ratingB)
	{
		final double expectedScoreForA = getExpectedScoreForA(p_ratingA, p_ratingB);

		if (expectedScoreForA == 0 || expectedScoreForA == 1)
		{
			return 0;
		}

		final double expectedScoreForB = 1 - expectedScoreForA;

		return -1 * (expectedScoreForA * Math.log(expectedScoreForA) + expectedScoreForB * Math.log(expectedScoreForB));
	}

	public static double getNewRatingForA(
		final double p_oldRatingA,
		final double p_oldRatingB,
		final double p_resultForA)
	{
		final double expectedScoreForA = getExpectedScoreForA(p_oldRatingA, p_oldRatingB);
		return p_oldRatingA + Constants.K * (p_resultForA - expectedScoreForA);
	}

	public static double getNoveltyAdjustedElo(final ArticleData p_data)
	{
		double thisElo = p_data.getRating();

		if (p_data.getNumComparisons() <= Constants.NOVELTY_CEILING)
		{
			thisElo += (Constants.NOVELTY_CEILING - p_data.getNumComparisons()) * Constants.NOVELTY_ADDEND;
		}

		return thisElo;
	}

	public static double getQScore(final double p_rating, final double p_bestRating)
	{
		return Math.pow(Constants.ELO_BASE, (p_rating - p_bestRating) / Constants.ELO_SCALE);
	}
}
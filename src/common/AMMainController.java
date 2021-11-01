package common;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AMMainController
{
	private static MainView						m_mainView;
	private static TreeMap<String, ArticleData>	m_articles;
	private static Random						m_random;

	public static void main(final String[] p_args)
	{
		runArticleManager();
	}

	public static void openEloRandomArticle()
	{
		final double maxElo = getMaxElo(false);
		double weightSum = 0;

		for (final ArticleData data : m_articles.values())
		{
			weightSum += ELO.getQScore(data.getRating(), maxElo);
		}

		final double randomChoice = weightSum * m_random.nextDouble();

		double currentSum = 0;
		for (final ArticleData data : m_articles.values())
		{
			currentSum += ELO.getQScore(data.getRating(), maxElo);
			if (currentSum > randomChoice)
			{
				openFileWithDefaultProgram(data.getFileName());
				break;
			}
		}
	}

	public static void openFileWithDefaultProgram(final String p_fileName)
	{
		try
		{
			Desktop.getDesktop().open(new File(p_fileName));
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void runElo(final JFrame p_frame)
	{
		final ArticleData[] articles = m_articles.values().toArray(new ArticleData[] {});
		final ArrayList<EloComparison> comparisons = new ArrayList<>();
		for (int firstIndex = 0; firstIndex < articles.length; firstIndex++)
		{
			for (int secondIndex = firstIndex + 1; secondIndex < articles.length; secondIndex++)
			{
				final EloComparison ec = new EloComparison(articles[firstIndex], articles[secondIndex], 0);
				comparisons.add(ec);
			}
		}

		boolean breakLoop = false;
		while (!breakLoop)
		{
			final EloComparison selection = selectComparisonToShow(comparisons);
			final ArticleData choice = ArticleComparisonView.getChoice(p_frame, selection);
			if (choice == null)
			{
				breakLoop = true;
			}
			else
			{
				updateEloValuesAfterChoice(selection, choice);
			}

			saveModel();
		}
	}

	public static void saveModel()
	{
		ArticleIO.saveArticlesToFile(m_articles.values());
	}

	public static void scrapeADirectory(final Component p_parent)
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int result = chooser.showOpenDialog(p_parent);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			int added = 0;

			final File parentFile = chooser.getSelectedFile();

			final TreeMap<String, ArticleData> scraped = ArticleIO.scrapeArticles(parentFile, false);
			for (final String fileName : scraped.keySet())
			{
				if (!m_articles.containsKey(fileName))
				{
					m_articles.put(fileName, scraped.get(fileName));
					added++;
				}
			}

			final Path parent = Paths.get(parentFile.getAbsolutePath()).normalize().toAbsolutePath();

			int subtracted = 0;
			for (final Object fileName : m_articles.keySet().toArray())
			{
				final Path child = Paths.get((String) fileName).normalize().toAbsolutePath();
				if (child.startsWith(parent))
				{
					if (!scraped.containsKey(fileName))
					{
						m_articles.remove(fileName);
						subtracted++;
					}
				}
			}

			JOptionPane
				.showMessageDialog(p_parent, added + " new file(s) found.  " + subtracted + " old file(s) deleted.");
		}
	}

	public static void showTopArticles()
	{
		if (m_articles.isEmpty())
		{
			System.out.println("No articles.  Scrape a directory first.");
			return;
		}

		final ArticleData[] articles = m_articles.values().toArray(new ArticleData[] {});
		Arrays.sort(articles, new ArticleEloComparator());
		for (final ArticleData article : articles)
		{
			System.out.println((int) article.getRating() + "\t" + article.getTitle() + "\t" + article.getFileName());
		}
	}

	private static double calculateWeights(final ArrayList<EloComparison> p_comparisons)
	{
		double weightSum = 0;
		final double maxElo = getMaxElo(true);

		for (final EloComparison ec : p_comparisons)
		{
			final double entropy = ELO.getMatchEntropy(ec.getArticleA().getRating(), ec.getArticleB().getRating());
			final double entropyWeight = 2 * entropy / Math.log(2);

			final double adjustedEloA = ELO.getNoveltyAdjustedElo(ec.getArticleA());
			final double adjustedEloB = ELO.getNoveltyAdjustedElo(ec.getArticleB());

			final double weightA = ELO.getQScore(adjustedEloA, maxElo);
			final double weightB = ELO.getQScore(adjustedEloB, maxElo);

			ec.setWeight(entropyWeight + weightA + weightB);
			weightSum += ec.getWeight();
		}

		return weightSum;
	}

	private static double getMaxElo(final boolean p_adjustForNovelty)
	{
		double maxElo = 0;
		for (final ArticleData data : m_articles.values())
		{
			double thisElo = data.getRating();

			if (p_adjustForNovelty)
			{
				thisElo = ELO.getNoveltyAdjustedElo(data);
			}

			maxElo = Math.max(maxElo, thisElo);
		}
		return maxElo;
	}

	private static void runArticleManager()
	{
		m_random = new Random();
		m_articles = ArticleIO.loadArticlesFromFile();
		m_mainView = new MainView();
		m_mainView.makeItSo();

		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			saveModel();
		}));
	}

	private static EloComparison selectComparisonToShow(final ArrayList<EloComparison> comparisons)
	{
		final double weightSum = calculateWeights(comparisons);
		final double randSelection = m_random.nextDouble() * weightSum;

		EloComparison selection = null;
		double sumSoFar = 0;
		for (final EloComparison comparison : comparisons)
		{
			sumSoFar += comparison.getWeight();
			if (sumSoFar > randSelection)
			{
				selection = comparison;
				break;
			}
		}
		return selection;
	}

	private static void updateEloValuesAfterChoice(final EloComparison p_selection, final ArticleData p_choice)
	{
		final double outcomeA = p_choice == p_selection.getArticleA() ? 1.0 : 0.0;
		final double outcomeB = 1 - outcomeA;

		final double newARating = ELO
			.getNewRatingForA(p_selection.getArticleA().getRating(), p_selection.getArticleB().getRating(), outcomeA);
		final double newBRating = ELO
			.getNewRatingForA(p_selection.getArticleB().getRating(), p_selection.getArticleA().getRating(), outcomeB);

		p_selection.getArticleA().setRating(newARating);
		p_selection.getArticleB().setRating(newBRating);

		p_selection.getArticleA().incrementNumComparisons();
		p_selection.getArticleB().incrementNumComparisons();
	}
}

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
	private static MainView m_mainView;
	private static TreeMap<String, ArticleData> m_articles;
	private static Random m_random;
	
	public static void main(String[] p_args)
	{
		runArticleManager();
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
	
	public static void saveModel()
	{
		ArticleIO.saveArticlesToFile(m_articles.values());
	}

	public static void scrapeADirectory(Component p_parent)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(p_parent);
		if(result == JFileChooser.APPROVE_OPTION)
		{
			int added = 0;
			
			File parentFile = chooser.getSelectedFile();
			
			TreeMap<String, ArticleData> scraped = ArticleIO.scrapeArticles(parentFile, false);
			for(String fileName: scraped.keySet())
			{
				if(!m_articles.containsKey(fileName))
				{
					m_articles.put(fileName, scraped.get(fileName));
					added++;
				}
			}
			
			Path parent = Paths.get(parentFile.getAbsolutePath()).normalize().toAbsolutePath();
			
			int subtracted = 0;
			for(Object fileName: m_articles.keySet().toArray())
			{
				Path child = Paths.get((String)fileName).normalize().toAbsolutePath();
				if(child.startsWith(parent))
				{
					if(!scraped.containsKey(fileName))
					{
						m_articles.remove(fileName);
						subtracted++;
					}
				}
			}
			
			JOptionPane.showMessageDialog(p_parent, added + " new file(s) found.  " + subtracted + " old file(s) deleted.");
		}
	}

	public static void showTopArticles()
	{
		if(m_articles.isEmpty())
		{
			System.out.println("No articles.  Scrape a directory first.");
			return;
		}
		
		ArticleData[] articles = m_articles.values().toArray(new ArticleData[] {});
		Arrays.sort(articles, new ArticleEloComparator());
		for(ArticleData article: articles)
		{
			System.out.println((int)article.getRating() + "\t" + article.getTitle() + "\t" + article.getFileName());
		}
	}

	public static void runElo(JFrame p_frame)
	{
		ArticleData[] articles = m_articles.values().toArray(new ArticleData[] {});
		ArrayList<EloComparison> comparisons = new ArrayList<EloComparison>();
		for(int firstIndex = 0; firstIndex < articles.length; firstIndex++)
		{
			for(int secondIndex = firstIndex + 1; secondIndex < articles.length; secondIndex++)
			{
				EloComparison ec = new EloComparison(articles[firstIndex], articles[secondIndex], 0);
				comparisons.add(ec);
			}
		}
		
		boolean breakLoop = false;
		while(!breakLoop)
		{
			EloComparison selection = selectComparisonToShow(comparisons);
			ArticleData choice = ArticleComparisonView.getChoice(p_frame, selection);
			if(choice == null)
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

	private static void updateEloValuesAfterChoice(EloComparison p_selection, ArticleData p_choice)
	{
		double outcomeA = (p_choice == p_selection.getArticleA()) ? 1.0 : 0.0;
		double outcomeB = 1 - outcomeA;
		
		double newARating = ELO.getNewRatingForA(p_selection.getArticleA().getRating(), p_selection.getArticleB().getRating(), outcomeA);
		double newBRating = ELO.getNewRatingForA(p_selection.getArticleB().getRating(), p_selection.getArticleA().getRating(), outcomeB);
		
		p_selection.getArticleA().setRating(newARating);
		p_selection.getArticleB().setRating(newBRating);
		
		p_selection.getArticleA().incrementNumComparisons();
		p_selection.getArticleB().incrementNumComparisons();
	}

	private static EloComparison selectComparisonToShow(ArrayList<EloComparison> comparisons)
	{
		double weightSum = calculateWeights(comparisons);
		double randSelection = m_random.nextDouble() * weightSum;
		
		EloComparison selection = null;
		double sumSoFar = 0;
		for(int i = 0; i < comparisons.size(); i++)
		{
			sumSoFar += comparisons.get(i).getWeight();
			if(sumSoFar > randSelection)
			{
				selection = comparisons.get(i);
				break;
			}
		}
		return selection;
	}

	private static double calculateWeights(ArrayList<EloComparison> p_comparisons)
	{
		double weightSum = 0;	
		double maxElo = getMaxElo(true);
		
		for(EloComparison ec: p_comparisons)
		{			
			double entropy = ELO.getMatchEntropy(ec.getArticleA().getRating(), ec.getArticleB().getRating());
			double entropyWeight = 2*entropy/Math.log(2);
			
			double adjustedEloA = ELO.getNoveltyAdjustedElo(ec.getArticleA());
			double adjustedEloB = ELO.getNoveltyAdjustedElo(ec.getArticleB());
			
			double weightA = ELO.getQScore(adjustedEloA, maxElo);
			double weightB = ELO.getQScore(adjustedEloB, maxElo);
			
			ec.setWeight(entropyWeight + weightA + weightB);
			weightSum += ec.getWeight();
		}
		
		return weightSum;
	}

	private static double getMaxElo(boolean p_adjustForNovelty)
	{
		double maxElo = 0;
		for(ArticleData data: m_articles.values())
		{
			double thisElo = data.getRating();
			
			if(p_adjustForNovelty)
			{
				thisElo = ELO.getNoveltyAdjustedElo(data);
			}
			
			maxElo = Math.max(maxElo, thisElo);
		}
		return maxElo;
	}

	public static void openFileWithDefaultProgram(String p_fileName)
	{
		try
		{
			Desktop.getDesktop().open(new File(p_fileName)); 
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void openEloRandomArticle()
	{
		double maxElo = getMaxElo(false);
		double weightSum = 0;
		
		for(ArticleData data: m_articles.values())
		{
			weightSum += ELO.getQScore(data.getRating(), maxElo);
		}
		
		double randomChoice = weightSum * m_random.nextDouble();
		
		double currentSum = 0;
		for(ArticleData data: m_articles.values())
		{
			currentSum += ELO.getQScore(data.getRating(), maxElo);
			if(currentSum > randomChoice)
			{
				openFileWithDefaultProgram(data.getFileName());
				break;
			}
		}
	}
}

package common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeMap;

public class ArticleIO
{

	public static TreeMap<String, ArticleData> loadArticlesFromFile()
	{
		final TreeMap<String, ArticleData> ret = new TreeMap<>();
		final SimpleDB simpleDB = SimpleDBIO.loadFromFile(getArticlesFile(), true);

		for (int i = 0; i < simpleDB.numRows(); i++)
		{
			final String articleTitle = simpleDB.get(i, Constants.ARTICLE_TITLE_COLUMN_NAME).asString();
			final String articleFilePath = simpleDB.get(i, Constants.ARTICLE_FILE_PATH_COLUMN_NAME).asString();
			final double rating = simpleDB.get(i, Constants.RATING_COLUMN_NAME).asDouble();
			final int numComparisons = simpleDB.get(i, Constants.NUM_COMPARISONS_COLUMN_NAME).asInt();
			final String articleAbstract = simpleDB.get(i, Constants.ARTICLE_ABSTRACT_COLUMN_NAME).asString();

			final ArticleData article =
				new ArticleData(articleTitle, new File(articleFilePath), rating, numComparisons, articleAbstract);
			ret.put(articleFilePath, article);
		}

		return ret;
	}

	public static void main(final String[] p_args)
	{
		final SimpleDB db = SimpleDBIO.loadFromFile(getArticlesFile(), true);
		SimpleDBIO.addColumnToDB(db, Constants.ARTICLE_ABSTRACT_COLUMN_NAME, Constants.NO_NAME_YET);
		SimpleDBIO.saveToFile(db, getArticlesFile());
	}

	public static void saveArticlesToFile(final Collection<ArticleData> p_articles)
	{
		final SimpleDB toSave = new SimpleDB();
		int rowIndex = 0;
		for (final ArticleData article : p_articles)
		{
			toSave.set(rowIndex, Constants.ARTICLE_TITLE_COLUMN_NAME, article.getTitle());
			toSave.set(rowIndex, Constants.ARTICLE_FILE_PATH_COLUMN_NAME, article.getFileName());
			toSave.set(rowIndex, Constants.RATING_COLUMN_NAME, Double.toString(article.getRating()));
			toSave.set(rowIndex, Constants.NUM_COMPARISONS_COLUMN_NAME, Integer.toString(article.getNumComparisons()));
			toSave.set(rowIndex, Constants.ARTICLE_ABSTRACT_COLUMN_NAME, article.getAbstract());

			rowIndex++;
		}

		final File articlesFile = getArticlesFile();
		if (!articlesFile.getParentFile().exists())
		{
			articlesFile.getParentFile().mkdirs();
		}

		if (!articlesFile.exists())
		{
			try
			{
				articlesFile.createNewFile();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}

		SimpleDBIO.saveToFile(toSave, getArticlesFile());
	}

	public static TreeMap<String, ArticleData> scrapeArticles(final File p_parent, final boolean p_recursive)
	{
		final TreeMap<String, ArticleData> ret = new TreeMap<>();

		scrapeArticlesPrivate(p_parent, p_recursive, ret);

		return ret;
	}

	private static File getArticlesFile()
	{
		final String fileLoc = Constants.SAVE_DIR;
		return new File(fileLoc, Constants.ARTICLE_SAVE_FILE);
	}

	private static void scrapeArticlesPrivate(
		final File p_parent,
		final boolean p_recursive,
		final TreeMap<String, ArticleData> p_ret)
	{
		final File[] files = p_parent.listFiles();

		for (final File file : files)
		{
			if (file.isDirectory())
			{
				if (p_recursive)
				{
					scrapeArticlesPrivate(file, p_recursive, p_ret);
				}
			}
			else
			{
				try
				{
					if (!p_ret.containsKey(file.getCanonicalPath()))
					{
						final ArticleData newArticle = new ArticleData(
							Constants.NO_NAME_YET,
							file,
							Constants.DEFAULT_ELO_RATING,
							0,
							Constants.NO_NAME_YET);
						p_ret.put(file.getCanonicalPath(), newArticle);
					}
					else
					{
						throw new RuntimeException("Repeated file name: " + file.getName());
					}
				}
				catch (final IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}

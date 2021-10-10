package common;

public class Constants
{
	public static final double	ELO_SCALE						= 400;
	public static final double	ELO_BASE						= 10;
	public static final double	K								= 400;
	public static final String	SAVE_DIR						= "save";
	public static final String	ARTICLE_SAVE_FILE				= "articles.sdb";
	public static final String	ARTICLE_TITLE_COLUMN_NAME		= "ARTICLE_TITLE";
	public static final String	ARTICLE_FILE_PATH_COLUMN_NAME	= "ARTICLE_FILE_NAME";
	public static final String	ARTICLE_ABSTRACT_COLUMN_NAME	= "ARTICLE_ABSTRACT";
	public static final String	RATING_COLUMN_NAME				= "RATING";
	public static final String	NO_NAME_YET						= "~";
	public static final double	DEFAULT_ELO_RATING				= 1500;
	public static final String	NUM_COMPARISONS_COLUMN_NAME		= "NUM_COMPARISONS";
	public static final double	NOVELTY_ADDEND					= 50;
	public static final double	NOVELTY_CEILING					= 20;
}

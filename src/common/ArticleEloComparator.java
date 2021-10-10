package common;

import java.util.Comparator;

public class ArticleEloComparator implements Comparator<ArticleData>
{
	@Override public int compare(ArticleData p_o1, ArticleData p_o2)
	{
		// TODO Auto-generated method stub
		if(p_o1 == p_o2)
		{
			return 0;
		}
		
		if(p_o1.getRating() > p_o2.getRating())
		{
			return 1;
		}
		
		if(p_o1.getRating() < p_o2.getRating())
		{
			return -1;
		}
		
		return (p_o1.getFileName().compareToIgnoreCase(p_o2.getFileName()));
	}
}

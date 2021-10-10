package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainView
{
	private JFrame m_frame;

	public MainView()
	{
		m_frame = new JFrame("Article Manager v0.1");
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel mainPanel = new JPanel(new FlowLayout());
		
		JButton scrapeButton = new JButton("Scrape a directory");
		JButton showTopButton = new JButton("Show best articles");
		JButton runEloButton = new JButton("Run Elo");
		JButton openRandomArticleButton = new JButton("Open Elo-random article");
		
		scrapeButton.addActionListener(new ActionListener() 
    		{
    			@Override public void actionPerformed(ActionEvent p_arg0)
    			{
    				AMMainController.scrapeADirectory(m_frame);
    			}
    		}
		);
		
		showTopButton.addActionListener(new ActionListener() 
    		{
    			@Override public void actionPerformed(ActionEvent p_arg0)
    			{
    				AMMainController.showTopArticles();
    			}
    		}
		);
		
		runEloButton.addActionListener(new ActionListener() 
    		{
    			@Override public void actionPerformed(ActionEvent p_arg0)
    			{
    				AMMainController.runElo(m_frame);
    			}
    		}
    	);
		
		openRandomArticleButton.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(ActionEvent p_arg0)
				{
					AMMainController.openEloRandomArticle();
				}
			});
		
		mainPanel.add(scrapeButton);
		mainPanel.add(showTopButton);
		mainPanel.add(runEloButton);
		mainPanel.add(openRandomArticleButton);

		// Add content to the window.
		m_frame.add(mainPanel, BorderLayout.CENTER);
		m_frame.setLocation(324, 200);
		m_frame.setPreferredSize(new Dimension(600, 75));
	}
	
	public void makeItSo()
	{
		m_frame.pack();
		m_frame.setVisible(true);
	}
}

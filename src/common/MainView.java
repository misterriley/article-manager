package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class MainView
{
	private final JFrame m_frame;

	public MainView()
	{
		m_frame = new JFrame("Article Manager v0.1");
		m_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JPanel mainPanel = new JPanel(new FlowLayout());

		final JButton scrapeButton = new JButton("Scrape a directory");
		final JButton showTopButton = new JButton("Show best articles");
		final JButton runEloButton = new JButton("Run Elo");
		final JButton openRandomArticleButton = new JButton("Open Elo-random article");

		scrapeButton.addActionListener(p_arg0 -> AMMainController.scrapeADirectory(m_frame));

		showTopButton.addActionListener(p_arg0 -> AMMainController.showTopArticles());

		runEloButton.addActionListener(p_arg0 -> AMMainController.runElo(m_frame));

		openRandomArticleButton.addActionListener(p_arg0 -> AMMainController.openEloRandomArticle());

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

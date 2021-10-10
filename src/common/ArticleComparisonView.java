package common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ArticleComparisonView extends JOptionPane
{
	private class ArticlePanel extends JPanel
	{
		/**
		 *
		 */
		private static final long	serialVersionUID	= 2591830625656994961L;
		private final ArticleData	m_article;
		private final JTextField	m_textField;

		public ArticlePanel(ArticleData p_article, ArticleComparisonView p_parent)
		{
			m_article = p_article;
			setLayout(new GridLayout(1, 4));

			m_textField = new JTextField(50);
			if (p_article != null)
			{
				m_textField.setText(p_article.getTitle());
				m_textField.addActionListener(p_e -> p_article.setTitle(m_textField.getText()));
			}

			final JButton openFileButton = new JButton("Open file");
			openFileButton
					.addActionListener(p_e -> AMMainController.openFileWithDefaultProgram(p_article.getFileName()));

			final JButton selectButton = new JButton("I select this one");
			selectButton.addActionListener(p_e -> p_parent.setValue(p_article));

			final JLabel fileNameLabel = new JLabel();
			if (p_article != null)
			{
				fileNameLabel.setText(p_article.getFileName());
			}

			final JPanel panelOne = new JPanel();
			final JPanel panelTwo = new JPanel();
			final JPanel panelThree = new JPanel();

			panelOne.add(m_textField);
			panelTwo.add(fileNameLabel);
			panelThree.add(openFileButton);
			panelThree.add(selectButton);

			add(panelOne);
			add(panelTwo);
			add(panelThree);
		}

		public void saveTitle()
		{
			m_article.setTitle(m_textField.getText());
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -3511960440606047358L;

	private static ArticleComparisonView buildAndShowArticleComparisonView(JFrame p_frame, EloComparison p_comparison)
	{
		final ArticleComparisonView optionPane = new ArticleComparisonView(p_comparison);

		final JDialog dialog = new JDialog(p_frame, "Select article", true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		optionPane.addPropertyChangeListener(e ->
		{
			final String prop = e.getPropertyName();

			if (prop.equals(JOptionPane.VALUE_PROPERTY))
			{
				optionPane.getNorthPanel().saveTitle();
				optionPane.getSouthPanel().saveTitle();
				dialog.setVisible(false);
			}
		});

		dialog.pack();
		dialog.setVisible(true);
		return optionPane;
	}

	public static ArticleData getChoice(JFrame p_frame, EloComparison p_comparison)
	{
		final ArticleComparisonView optionPane = buildAndShowArticleComparisonView(p_frame, p_comparison);

		final Object ret = optionPane.getValue();
		if (ret instanceof ArticleData)
		{
			return (ArticleData)ret;
		}

		return null;
	}

	public static void main(String[] p_args)
	{
		buildAndShowArticleComparisonView(null, null);
	}

	private ArticlePanel	m_northPanel;
	private ArticlePanel	m_southPanel;

	public ArticleComparisonView(EloComparison p_comparison)
	{
		super("Which article to read?");

		setSize(1000, 800);
		setLayout(new BorderLayout());

		final JPanel abstractsPanel = new JPanel(new FlowLayout());
		final JTextArea leftTextArea = new JTextArea();
		final JTextArea rightTextArea = new JTextArea();

		leftTextArea.setEditable(true);
		rightTextArea.setEditable(true);

		abstractsPanel.add(leftTextArea);
		abstractsPanel.add(rightTextArea);

		if (p_comparison == null)
		{
			m_northPanel = new ArticlePanel(null, this);
			m_southPanel = new ArticlePanel(null, this);

			leftTextArea.setText("first abstract here\n.\n.\n.\n.");
			rightTextArea.setText("second abstract here");
		}
		else
		{
			m_northPanel = new ArticlePanel(p_comparison.getArticleA(), this);
			m_southPanel = new ArticlePanel(p_comparison.getArticleB(), this);

			leftTextArea.setText(p_comparison.getArticleA().getAbstract());
			rightTextArea.setText(p_comparison.getArticleB().getAbstract());
		}

		add(m_northPanel, BorderLayout.NORTH);
		add(m_southPanel, BorderLayout.CENTER);
		add(abstractsPanel, BorderLayout.SOUTH);
	}

	private ArticlePanel getNorthPanel()
	{
		return m_northPanel;
	}

	private ArticlePanel getSouthPanel()
	{
		return m_southPanel;
	}
}

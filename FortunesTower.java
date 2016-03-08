//Andrew East - 2274193
//Final Project
//CIS 18a - Java - 47380

//FortunesTower.java: The main classfor my game

//This class has two functions:
//1. Drive the game, as the "controller", containing all the other classes  (the "model")
//2. Display the window, whose methods are the "view" and get called by the "controller"
// While I wanted to keep all these psudo-MVC components separate, the fact that the window's
//   action-listener needs to interact with the "controller" methods forces me to put them
//   together. Because of this, the MVC paradigm I hoped to implement was not possible,
//   although I attempted to maintin it virtually, and also some object-oriented principles
//   are violated, such as data hiding.



//GUI components
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

//GUI sub-components
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.LineBorder;

//GUI action components
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//File components - for reading the help file
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.IllegalStateException;
import java.util.Scanner;

public class FortunesTower extends JFrame implements Runnable //Runnable allows a separate thread to be branched off to run() an animation delay while keeping the window's thread free to repaint()
{
    public static void main(String args[])
	{
		FortunesTower window = new FortunesTower();
		
		window.newGame();
		window.setVisible(true);
		//from here, all actions are handled by the EventListeners of the buttons
    }


//***************************************************************************************
//***************************** Gameplay Functionality **********************************
//***************************************************************************************

	private GameBoard theBoard;

	private PlayerBank playerChips;
	private final int CHIPS_TO_START = 500; //arbitrary starting value, 500 is the defined starting point
	private final int CHIPS_TO_BET = 15; //15 is the default betting value, established by the original game, Fortune's Tower
	private int currentAnteMultiplier;
	
	private int currentBonusMultiplier;
	private boolean flagMisfortune;
	
	private final int ANIMATION_PAUSE = 270;
	private final int MSG_WIN = 1;
	private final int MSG_NORMAL = 0;
	
    public FortunesTower()
	{
		//initialize members
		theBoard = new GameBoard();
        initComponents();
    }
	
	private void newGame()
	{
		
		//TEMPcurrRow = 1;
		//visibleRevealRow(TEMPcurrRow++);
		
		playerChips = new PlayerBank(CHIPS_TO_START);
		currentAnteMultiplier = 1;
		updateLabelCurrentBet();
		jLabelCurrentBoardValue.setVisible(false);
		visibleHideBoard();
		
		resetForNewRound();
	}

	
	private void resetForNewRound()
	{
		theBoard.fillBoard(); //this method shuffles the deck
		
		currentBonusMultiplier = 1;
		flagMisfortune = false;
		initializeNewCardValues();
		changeLabelBank(playerChips.getBank());
		if (playerChips.getBank() < CHIPS_TO_BET) //not enough chips left to make even the smallest bet
			visibleGameOver(); //disable buttons so user's eye is drawn to the "New Game" button
		else
			visibleNewRoundStarting();
		
		repaint();
	}
	
	private void triggerWinningRound()
	{
		theBoard.takeRow();
		playerChips.creditBank(updateLabelBoardValue(theBoard.getBoardValue(), MSG_WIN));
		changeLabelBank(playerChips.getBank());
	}

	private void jButtonNextRowAction(ActionEvent evt)
	{
		theBoard.nextRow();
		visibleRevealRow(theBoard.getCurrentRow());
	}
	private void jButtonTakeRowAction(ActionEvent evt)
	{
		triggerWinningRound();
		resetForNewRound();
	}
	private void jButtonBetAnteAction(ActionEvent evt)
	{
		if (playerChips.debitBank(CHIPS_TO_BET * currentAnteMultiplier)) //performs debit ONLY if chips are available
		{
			visibleHideBoard();
			initalizeCardLabelsWithValues(); //resets the cards that are currently on the board
			
			visibleHideBetShowRowButtons();
			changeLabelBank(playerChips.getBank());
			//theBoard.nextRow(); //go to second row, already set up
			visibleRevealRow(theBoard.getCurrentRow());
		}
		else
		{
			JOptionPane.showMessageDialog(FortunesTower.this, "Insufficient chips to bet", "Not Enough Chips", JOptionPane.WARNING_MESSAGE);
		}
	}
	private void jButtonUpAnteAction(ActionEvent evt)
	{
		if (currentAnteMultiplier < 10)
		{
			++currentAnteMultiplier;
			updateLabelCurrentBet();
			repaint();
		}
	
	}
	private void jButtonDownAnteAction(ActionEvent evt)
	{
		if (currentAnteMultiplier > 1)
		{
			--currentAnteMultiplier;
			updateLabelCurrentBet();
			repaint();
		}
	}
	private void jButtonNewGameAction(ActionEvent evt)
	{
		if (playerChips.getBank() < CHIPS_TO_BET) //don't put up confirm dialog if the player "lost"
		{
			newGame();
		}
		else
		{
			//ConfirmDialog will return 0 for Yes, 1 for No, and -1 for the dialog box being closed manually
			if (JOptionPane.showConfirmDialog(FortunesTower.this, "This will end your current game.\nAre you sure?", "Confirm New Game", JOptionPane.YES_NO_OPTION) == 0)
				newGame(); //resets everything!
		}
	}






//***************************************************************************************
//********************************* GUI Functionality ***********************************
//***************************************************************************************

//Note: As suggested on the class discussion board, I have used the NetBeans IDE GUI editor
// to create the building blocks of this GUI. After consideration and experimentation, I have chosen
// GridBag as my layout manager, as it provides the most precise layout options. I created 
// the complex pyramid layout using NetBeans, and then modified and streamlined the resultant code,
// using the book as a reference.

	private JLabel cardLabels[][];
	private CardBurnable cardValues[][];
	private int rowSum[];
	
	private JLabel rowValueLabels[];
	
	private JLabel spacerLabels[];
	
    private JButton jButtonHelp;
    private JButton jButtonNewGame;
	
    private JButton jButtonNextRow;
    private JButton jButtonTakeRow;
	
	private JButton jButtonBetAnte;
    private JButton jButtonUpAnte;
    private JButton jButtonDownAnte;
	
    private JLabel jLabelPlayerBankTitle;
    private JLabel jLabelPlayerBank;
	
    private JLabel jLabelCurrentBoardValue;
    private JLabel jLabelCurrentBet;
	
    private JLabel jLabelJackpot;
	
	private Thread animateCards = null; //a Thread to run the animation
	private int rowAnimate;
	
	//private int TEMPcurrRow;
	
	//a few objects that will be re-used
	private Font labelFontBank;
	private Font labelFontNumber;
	private Font labelFontSymbol;
	private LineBorder labelBorderNormal;
	private LineBorder labelBorderBurn;
	private LineBorder labelBorderSaved;
	private LineBorder labelBorderMatched;
	private LineBorder labelBorderJackpot;
	private Color colorCardBG;
	private Color colorText;
	private Color colorRed;
	private Color colorRedBG;
	private Color colorBlue;
	private Color colorBlueBG;
	private Color colorGreen;
	private Color colorGreenBG;
	private Color colorYellow;
	private Color colorYellowBG;
	private Color colorYellowJackpot;
	private String stringCastle;
	private String stringKnight;
	
	private void initializeNewCardValues()
	{
		for (int i = 0; i < 8; ++i)
			for (int j = 0; j < (i + 1); ++j)
				cardValues[i][j] = theBoard.getCard(i, j);
	}
	
	private void initalizeCardLabelsWithValues()
	{
		cardLabels[0][0].setFont(labelFontSymbol);
		cardLabels[0][0].setText(stringCastle);
		cardLabels[0][0].setBorder(labelBorderNormal);
		cardLabels[0][0].setBackground(colorCardBG);
		
		for (int i = 1; i < 8; ++i)
		{
			for (int j = 0; j < (i + 1); ++j)
			{
				cardLabels[i][j].setBorder(labelBorderNormal);
				cardLabels[i][j].setBackground(colorCardBG);
				if (cardValues[i][j].getFaceInt() == 8)
				{
					cardLabels[i][j].setFont(labelFontSymbol);
					cardLabels[i][j].setText(stringKnight);
				}
				else
				{
					cardLabels[i][j].setFont(labelFontNumber);
					cardLabels[i][j].setText(String.valueOf(cardValues[i][j].getFaceInt()));
				}
			}
			rowSum[i] = 0; //holds the sum of the row's value, for rowValueLabels to use
		}
	}
	
	private void changeLabelBank(int num)
	{
		jLabelPlayerBank.setText(num + " chips");
	}
	private void updateLabelCurrentBet()
	{
		jLabelCurrentBet.setText("Current Bet: " + (CHIPS_TO_BET * currentAnteMultiplier)  + " chips");
	}
	private int updateLabelBoardValue(int boardValue, int msg)
	{
		if (boardValue == -1)
		{
			jLabelCurrentBoardValue.setText("Misfortune!");
			return -1;
		}
		else
		{
			String msgPrefix = (msg == MSG_NORMAL) ? "Board value: " : "You won: ";
			int mult = currentAnteMultiplier * currentBonusMultiplier;
			jLabelCurrentBoardValue.setText(msgPrefix + mult + " x " + boardValue + " = " + mult * boardValue);
			return mult * boardValue;
		}
	}


	
	//********************************* Show / Hide Elements Methods *********************************
	private void visibleHideBoard()
	{
		cardLabels[0][0].setFont(labelFontSymbol);
		cardLabels[0][0].setText(stringCastle);
		cardLabels[0][0].setVisible(true); //top card
		for (int i = 1; i < 8; ++i)
		{
			rowValueLabels[i].setVisible(false);
			for (int j = 0; j < (i + 1); ++j)
				cardLabels[i][j].setVisible(false);
		}
	}
	
	private void visibleNewRoundStarting()
	{
		visibleShowBetHideRowButtons();
		
		jLabelPlayerBankTitle.setVisible(true);
		jLabelPlayerBank.setVisible(true);
		//jLabelJackpot.setVisible(false);

		jLabelCurrentBet.setVisible(true);
	}
	
	private void visibleHideBetShowRowButtons()
	{
		jButtonBetAnte.setVisible(false);
		jButtonBetAnte.setEnabled(false);
		jButtonUpAnte.setVisible(false);
		jButtonUpAnte.setEnabled(false);
		jButtonDownAnte.setVisible(false);
		jButtonDownAnte.setEnabled(false);
		
		jButtonNextRow.setVisible(true);
		jButtonTakeRow.setVisible(true);
		jLabelCurrentBoardValue.setVisible(true);
		jLabelJackpot.setVisible(false);
		
		jButtonNextRow.requestFocusInWindow(); //the window was not responding to "Alt+X" for "neXt row" after this method, so I added this line to manually move focus. Now it works.
	}
	
	private void visibleShowBetHideRowButtons()
	{
		jButtonBetAnte.setVisible(true);
		jButtonBetAnte.setEnabled(true);
		jButtonUpAnte.setVisible(true);
		jButtonUpAnte.setEnabled(true);
		jButtonDownAnte.setVisible(true);
		jButtonDownAnte.setEnabled(true);

		jButtonNextRow.setVisible(false);
		jButtonTakeRow.setVisible(false);
		jLabelCurrentBoardValue.setVisible(true);
		
		jButtonBetAnte.requestFocusInWindow();
	}
	
	private void visibleGameOver()
	{
		jButtonBetAnte.setVisible(true);
		jButtonBetAnte.setEnabled(false);
		jButtonUpAnte.setVisible(true);
		jButtonUpAnte.setEnabled(false);
		jButtonDownAnte.setVisible(true);
		jButtonDownAnte.setEnabled(false);

		jButtonNextRow.setVisible(false);
		jButtonTakeRow.setVisible(false);
		jLabelCurrentBoardValue.setVisible(true);
		
		jButtonBetAnte.requestFocusInWindow();
	}
	

	private void visibleRevealRow(int rowNum) //2-8 or 1-7? first now not used
	{
		if (rowNum <= 7 && rowNum >= 1)
		{
			enableOrDisableButtons(false); //disable button presses for the duration of the animation
			
			rowValueLabels[rowNum].setText("");
			rowValueLabels[rowNum].setVisible(true); //show the row's sum label
			
			rowAnimate = rowNum; //how many times to loop the animation
			animateCards = new Thread(this);
			animateCards.start(); //start a new thread to run the animation
		}
		//else bad row
	}
	public void run()
	{
		boolean foundMisfortune = false;
	
		//animate out cards
		cardLabels[rowAnimate][0].setVisible(true);
		rowSum[rowAnimate] += (cardValues[rowAnimate][0].getFaceInt() == 8) ? 0 : cardValues[rowAnimate][0].getFaceInt();
		rowValueLabels[rowAnimate].setText(String.valueOf(rowSum[rowAnimate]));
		updateLabelBoardValue(rowSum[rowAnimate], MSG_NORMAL);
		repaint();
		
		for (int i = 1; i <= rowAnimate; ++i)
		{
			try
			{
				animateCards.sleep(ANIMATION_PAUSE); //pause for 0.370 second (arbitrary value that looks good while animating
			}
			catch (InterruptedException interExcept)
			{
				// java will execute this block if the Thread.sleep() is interrupted by another thread. I don't need to handle this kind of error.
			}

			cardLabels[rowAnimate][i].setVisible(true);
			rowSum[rowAnimate] += (cardValues[rowAnimate][i].getFaceInt() == 8) ? 0 : cardValues[rowAnimate][i].getFaceInt();
			rowValueLabels[rowAnimate].setText(String.valueOf(rowSum[rowAnimate]));
			updateLabelBoardValue(rowSum[rowAnimate], MSG_NORMAL);
			repaint();
		}
		
		//highlight the row if all cards match!!
		if (theBoard.getIsRowMatched(rowAnimate))
		{
			for (int i = 0; i <= rowAnimate; ++i)
			{
				cardLabels[rowAnimate][i].setBorder(labelBorderMatched);
				cardLabels[rowAnimate][i].setBackground(colorGreenBG);
				repaint();
				try { animateCards.sleep(135); } catch (InterruptedException interExcept) { }
			}
			currentBonusMultiplier *= rowAnimate + 1;
			rowValueLabels[rowAnimate].setText(String.valueOf(rowSum[rowAnimate]) + "-" + currentBonusMultiplier + "x!");
			updateLabelBoardValue(rowSum[rowAnimate], MSG_NORMAL);

		}
		else //only show the burned or saved cards there isn't a match. (weird, but it's in the rules)
		{
			//animate cards changing state to burnable or saved
			for (int i = 0; i <= rowAnimate; ++i)
			{
				if (cardValues[rowAnimate][i].isBurned())
				{
					foundMisfortune = true;
					try { animateCards.sleep(ANIMATION_PAUSE); } catch (InterruptedException interExcept) { }
					cardLabels[rowAnimate][i].setBorder(labelBorderBurn);
					cardLabels[rowAnimate][i].setBackground(colorRedBG);
					rowSum[rowAnimate] = -1;
					rowValueLabels[rowAnimate].setText("X");
					updateLabelBoardValue(rowSum[rowAnimate], MSG_NORMAL);
					repaint();
				}
				else if (cardValues[rowAnimate][i].isSaved())
				{
					try { animateCards.sleep(ANIMATION_PAUSE); } catch (InterruptedException interExcept) { }
					cardLabels[rowAnimate][i].setBorder(labelBorderSaved);
					cardLabels[rowAnimate][i].setBackground(colorBlueBG);
					repaint();
				}
			}
		}

		//wrap up
		if (foundMisfortune)
		{
			flagMisfortune = true;
			resetForNewRound(); //this method resets the button visiblity, then goes ot waiting. So, even though this separate Thread will be calling it, it will still finish before the buttons are toggled
		}
		else if (rowAnimate == 7) //final row is animated with no misfortune
		{
			//JACKPOT!!
			jLabelJackpot.setVisible(true);
			for (int i = 0; i < 8; ++i)
			{
				for (int j = 0; j <= i; ++j)
				{
					cardLabels[i][j].setBorder(labelBorderJackpot);
					cardLabels[i][j].setBackground(colorYellowBG);
					repaint();
					//the animation accelerates!
					try { animateCards.sleep(50 - i*4); } catch (InterruptedException interExcept) { }
				}
			}
			triggerWinningRound();
			resetForNewRound();
		}
		
		enableOrDisableButtons(true); //re-enable buttons
		
		this.animateCards = null; //destroy the reference to the thread instance created for sleep() purposes, will be garbage collected sometime
	}
	
	private void enableOrDisableButtons(boolean state)
	{
		jButtonHelp.setEnabled(state);
		jButtonNewGame.setEnabled(state);
		jButtonTakeRow.setEnabled(state);
		jButtonNextRow.setEnabled(state);
		//these buttons are already disabled:
		//jButtonDownAnte.setEnabled(state);
		//jButtonUpAnte.setEnabled(state);
		//jButtonBetAnte.setEnabled(state);
	}
	
	
	
	
	
	
    private void initComponents()
	{
		//define objects that are set up for constant re-use
		labelFontBank = new Font("Tahoma", 0, 18);
		labelFontNumber = new Font("Serif", Font.BOLD, 36);
		labelFontSymbol = new Font("default", Font.PLAIN, 28);
		colorCardBG = new Color(255, 255, 255);
		colorText = new Color(0, 0, 0);
		colorRed = new Color(204, 0, 0);
		colorRedBG = new Color(255, 185, 185);
		colorBlue = new Color(0, 0, 204);
		colorBlueBG = new Color(185, 185, 255);
		colorGreen = new Color(0, 204, 0);
		colorGreenBG = new Color(185, 255, 185);
		colorYellow = new Color(255, 222, 0);
		colorYellowBG = new Color(255, 255, 105);
		colorYellowJackpot = new Color(219, 176, 0);
		labelBorderNormal = new LineBorder(colorText, 2, false);
		labelBorderBurn = new LineBorder(colorRed, 2, true);
		labelBorderSaved = new LineBorder(colorBlue, 2, true);
		labelBorderMatched = new LineBorder(colorGreen, 2, true);
		labelBorderJackpot = new LineBorder(colorYellow, 2, true);
		stringCastle = String.valueOf('\u2656'); //unicode character for a Chess 'castle'
		stringKnight = String.valueOf('\u265E'); //unicode for a chess 'knight' -- I would have liked to use the unicode dingbat for a 'shield', but that character is not in the Java font for Windows
		

		//set up a GridBag layout manager
        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane().setLayout(new java.awt.GridBagLayout());

		//initialize all Frame objects:
				
		cardLabels = new JLabel[8][];
		cardValues = new CardBurnable[8][];
		rowSum = new int[8];
		java.awt.Dimension cardLabelSize = new java.awt.Dimension(20, 50);
		for (int i = 0; i < 8; ++i)
		{
			cardLabels[i] = new JLabel[i + 1];
			cardValues[i] = new CardBurnable[i + 1];
			for (int j = 0; j < (i + 1); ++j)
			{
				//already set to NULL when initialized: cardValues[i][j] = null; //default value
				cardLabels[i][j] = new JLabel();
				cardLabels[i][j].setFont(labelFontNumber);
				cardLabels[i][j].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				//gets set during each round: cardLabels[i][j].setText(String.valueOf(cardValues[i][j]));
				cardLabels[i][j].setBackground(colorCardBG);
				cardLabels[i][j].setBorder(labelBorderNormal);
				cardLabels[i][j].setPreferredSize(cardLabelSize);
				cardLabels[i][j].setOpaque(true);
				cardLabels[i][j].setVisible(false);
			}
			rowSum[i] = 0; //the sum of the row's value
		}
		
		spacerLabels = new JLabel[40];
		for (int i = 0; i < 40; ++i)
		{
			spacerLabels[i] = new JLabel();
			spacerLabels[i].setVisible(true);
		}

		rowValueLabels = new JLabel[8];
		for (int i = 1; i < 8; ++i)
		{
			rowValueLabels[i] = new JLabel();
			//rowValueLabels[i].setText("");
			rowValueLabels[i].setVisible(false);
		}
		
		jButtonNextRow = new JButton();
        jButtonTakeRow = new JButton();
        jButtonBetAnte = new JButton();
        jButtonUpAnte = new JButton();
        jButtonDownAnte = new JButton();
		
        jButtonHelp = new JButton();
        jButtonNewGame = new JButton();
		
        jLabelPlayerBankTitle = new JLabel();
        jLabelPlayerBank = new JLabel();
        jLabelCurrentBoardValue = new JLabel();
        jLabelJackpot = new JLabel();
        jLabelCurrentBet = new JLabel();
		
/* 		jInternalFrameHelp = new javax.swing.JInternalFrame();
		jTextAreaHelp = new javax.swing.JTextArea();
		jButtonHelpClose = new JButton(); */

		
		
		int spacerCounter = 0;
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 32;
		gridBagConstraints.ipady = 6;
		for (int i = 0; i <= 23; ++i)
		{
			gridBagConstraints.gridx = i;
			getContentPane().add(spacerLabels[spacerCounter++], gridBagConstraints);
		}
		gridBagConstraints.gridx = 0;
		gridBagConstraints.ipadx = 6;
		for (int i = 1; i <= 16; ++i)
		{
			gridBagConstraints.ipady = (i % 2 == 1) ? 55 : 8; //even and odd rows have different spacing
			gridBagConstraints.gridy = i;
			getContentPane().add(spacerLabels[spacerCounter++], gridBagConstraints);
		}
		
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][5], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][3], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][4], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][6], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][5], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[2][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][3], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][4], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][6], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[6][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[3][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[3][3], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[2][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[1][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[2][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[1][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][4], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][3], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[3][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[5][5], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[0][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[3][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[4][0], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 15;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[7][7], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[4][1], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[4][3], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[4][2], gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 3;
        getContentPane().add(cardLabels[4][4], gridBagConstraints);
		
		
		

		//ButtonHandler handler = new ButtonHandler();
		
        jButtonNextRow.setMnemonic('x');
        jButtonNextRow.setText("Next Row");
        jButtonNextRow.setVisible(false);
        jButtonNextRow.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonNextRowAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonNextRow, gridBagConstraints);

        jButtonTakeRow.setMnemonic('a');
        jButtonTakeRow.setText("Take Row");
        jButtonTakeRow.setVisible(false);
		jButtonTakeRow.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonTakeRowAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonTakeRow, gridBagConstraints);

        jButtonBetAnte.setMnemonic('b');
        jButtonBetAnte.setText("Bet Ante");
		jButtonBetAnte.setVisible(false);
		jButtonBetAnte.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonBetAnteAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonBetAnte, gridBagConstraints);

        jButtonUpAnte.setMnemonic('u');
        jButtonUpAnte.setText("Up Ante");
		jButtonUpAnte.setVisible(false);
		jButtonUpAnte.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonUpAnteAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonUpAnte, gridBagConstraints);

        jButtonDownAnte.setMnemonic('d');
        jButtonDownAnte.setText("Down Ante");
		jButtonDownAnte.setVisible(false);
		jButtonDownAnte.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonDownAnteAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonDownAnte, gridBagConstraints);

        jButtonHelp.setMnemonic('h');
        jButtonHelp.setText("Help");
		jButtonHelp.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonHelpAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonHelp, gridBagConstraints);

        jButtonNewGame.setText("New Game");
		jButtonNewGame.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { jButtonNewGameAction(event); } });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jButtonNewGame, gridBagConstraints);
		
        jLabelPlayerBankTitle.setFont(labelFontBank);
        jLabelPlayerBankTitle.setText("Player Bank: ");
        jLabelPlayerBankTitle.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jLabelPlayerBankTitle, gridBagConstraints);
		
        jLabelPlayerBank.setFont(labelFontBank);
		//jLabelPlayerBank.setText("200 chips");
        jLabelPlayerBank.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jLabelPlayerBank, gridBagConstraints);

		//jLabelCurrentBoardValue.setText("Board Value: 1 x 15 = 15");
		jLabelCurrentBoardValue.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 19;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jLabelCurrentBoardValue, gridBagConstraints);
		
		//jLabelCurrentBet.setText("Current Bet: 15 chips");
		jLabelCurrentBet.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 19;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        getContentPane().add(jLabelCurrentBet, gridBagConstraints);
		
		jLabelJackpot.setText("JACKPOT!");
        jLabelJackpot.setFont(labelFontNumber);
        jLabelJackpot.setForeground(colorYellowJackpot);
		jLabelJackpot.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jLabelJackpot, gridBagConstraints);
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		for (int i = 1; i < 8; ++i)
		{
			gridBagConstraints.gridx = 11 + i;
			gridBagConstraints.gridy = (i * 2) + 1;
			getContentPane().add(rowValueLabels[i], gridBagConstraints);
			
		}
		
		pack();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Fortune's Tower");
		setSize(800, 600);
		setResizable(false);
    }


	private void jButtonHelpAction(ActionEvent evt)
	{
		String rules = "";
	
		try
		{
			//Open file:
			Scanner fileReader = new Scanner( new File( "rules.txt" ) );
			
			while (fileReader.hasNextLine()) //loop through all lines
			{
				rules += fileReader.nextLine() + '\n';
			}
			rules += "Fortune's Tower is (C) Lionhead Studios and Microsoft Games. It was created by Lionhead Studios for the game Fable II.";
			
			//Close file:
			if (fileReader != null)
				fileReader.close();
		}
		catch ( FileNotFoundException fileNotFoundEx )
		{
			System.err.println("Error opening 'rules.txt'.");
			//System.exit(1); //end program due to fatal error
			rules = "ERROR: How to Play file not found.";
		}
		catch ( IllegalStateException stateEx )
		{
			System.err.println("Error reading from file.");
			rules = "ERROR: How to Play file read error.";
		}
	
		JOptionPane.showMessageDialog(FortunesTower.this, rules, "How to Play", JOptionPane.PLAIN_MESSAGE); //JOptionPane.QUESTION_MESSAGE);
	}

}

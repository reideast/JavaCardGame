//Andrew East - 2274193
//Final Project
//CIS 18a - Java - 47380

//GameBoard.java - the main Model for this project:
//A structured game board, controlling a Deck object that deals out Card objects
//Organized into an array of increasingly large GameBoardRows, a helper class; each row has a series of CardNode objects

public class GameBoard
{
	//Constants:
	private final int NUM_ROWS = 8; //How "tall" the game board is. Width is one card first, then one card more for each row, up to NUM_ROWS wide on the last row
	
	//Class members:
	private Deck deckMaster; //deck of Fortune's Tower cards
	private GameBoardRow rows[]; //the rows of the gameboard, 0...7

	private int currentRow; //internal pointer to how many rows have been dealt out, counts 0 to NUM_ROWS - 1
	private int rowWhichBetTaken; //is flagged 0 if no row's pay out has been taken, or the row number whose value has been taken	
	
	//Construtor (only default constructor needed, since all GameBoards will be identical)
	GameBoard()
	{
		deckMaster = new Deck(); //Create new Deck object; thusly creating new Card objects
		currentRow = 0; //intialized values
		rowWhichBetTaken = 0;
		
		rows = new GameBoardRow[NUM_ROWS]; //create array of rows
		for (int i = 0; i < NUM_ROWS; ++i)
		{
			rows[i] = new GameBoardRow();
		}
	}
	
	//Resets the game by dealing cards into all eight rows, and resetting the internal pointers and flags to te beginning
	public void fillBoard()
	{
		deckMaster.shuffle();
		currentRow = 1; //start at the first row that can have value, ie. the second row, AKA the one with TWO cards
		rowWhichBetTaken = 0; //zero is the flag value for "No bet taken yet"
		
		//create rows of proper length and deal cards from the deck into it
		for (int i = 0; i < NUM_ROWS; ++i)
		{
			rows[i].populateRow( deckMaster, (i + 1) ); //call the method of the GameBoardRow helper class, giving it the deck to draw cards from
		}
		
		calculateBurning();
		
		//printWholeBoard();
	}
	
	private void calculateBurning()
	{
		NodeCard currRow;
		NodeCard prevRow;
		boolean currKnight;
	
		for (int rowExamine = 2; rowExamine < NUM_ROWS; ++rowExamine) //start at the row of three cards, since the second row cannot be burned
		{
			//examine Linked List:
			currRow = rows[rowExamine].firstCardInRow;
			prevRow = rows[rowExamine - 1].firstCardInRow;
			currKnight = rows[rowExamine].rowHasKnight();
			
			while (currRow != null)
			{
				if (prevRow != null) //is NOT the last card in the row, so check the AFTER parent card
				{
					if (currRow.value.getFaceInt() == prevRow.value.getFaceInt() && currRow.value.getFaceInt() != 8) //matches the parent, and is not a knight
					{
						if (!currKnight)
						{
							currRow.value.setBurned();
							prevRow.value.setBurner();
						}
						else
							currRow.value.setSaved();
					}
					
					if (currRow.prev != null) //is NOT the first card in row, so check the BEFORE parent
					{
						if (currRow.value.getFaceInt() == ((prevRow.prev).value).getFaceInt() && currRow.value.getFaceInt() != 8) //matches the parent, and is not a knight
						{
							if (!currKnight)
							{
								currRow.value.setBurned();
								prevRow.prev.value.setBurner();
							}
							else
								currRow.value.setSaved();
						}
					}
				}
				else //special case: is last card, so prevRow is already pointing to NULL. use row[i - 1].lastCardInRow
				{
					if (currRow.value.getFaceInt() == rows[rowExamine - 1].lastCardInRow.value.getFaceInt() && currRow.value.getFaceInt() != 8)
					{
						if (!currKnight)
						{
							currRow.value.setBurned();
							rows[rowExamine - 1].lastCardInRow.value.setBurner();
						}
						else
							currRow.value.setSaved();
					}
				}
				
				//iterate
				currRow = currRow.next;
				if (prevRow != null) //since prevRow is shorter than currRow
					prevRow = prevRow.next;
			}
		}
	}
	
	public CardBurnable getCard(int rowToGet, int numInRow)
	{
		if (rowToGet >= 0 && rowToGet <= 7) //within rows 1-8
		{
			if (numInRow >= 0 && numInRow <= rowToGet)
			{
				NodeCard currCardAt = rows[rowToGet].firstCardInRow;
				for (int i = 0; i < numInRow; ++ i)
					currCardAt = currCardAt.next; //will not be assigned a NULL, since GameBoardRow has been FULLY tested
				return currCardAt.value; //returns the CardBurnable reference
			}
			else
				return null;
		}
		else
			return null;
	}
	
	public void nextRow()
	{
		if (currentRow < 7)
			++currentRow;
		//else NO ERROR HANDLING
	}
	
	public int getCurrentRow()
	{
		return currentRow;
	}
	
	public boolean getIsRowMatched(int row)
	{
		if (row >= 0 && row <= NUM_ROWS)
			return rows[row].getRowMatched();
		else
			return false;
	}
	
	//The player has decided to take the bet for this row, set the flag
	public void takeRow()
	{
		rowWhichBetTaken = currentRow;
	}
	
	public boolean isBoardTaken()
	{
		if (rowWhichBetTaken == 0) //the default value, which cannot be set otherwise
			return false;
		else
			return true;
	}
	
	//Return the board's value AFTER a bet is taken; will usually be just the value of the current row, unless we've reached the final row, then it's a jackpot
	public int getBoardValue()
	{
		if (rowWhichBetTaken == (NUM_ROWS - 1)) //JACKPOT! We've reached the final row, so the player gets a jackpot, or the value of all rows.
		{
			int total = 0;
			for (int i = 1; i < NUM_ROWS; ++i) //total all rows except the "Castle" row, so start at 2nd row
			{
				total += rows[i].getRowValue();
			}
			return total;
		}
		else if ( rowWhichBetTaken < NUM_ROWS && rowWhichBetTaken > 0) //within bounds of regular rows
		{
			return rows[rowWhichBetTaken].getRowValue();
		}
		else //bad value
		{
			return 0;
		}
	}
	
	//DEBUG: not really that useful in the final game, but it is useful to test printing methods of the Row/Card classes
	public void printWholeBoard()
	{
		String spaces;
		for (int i = 0; i < NUM_ROWS; ++i)
		{
			spaces = "";
			//since each row adds one new card == 4 new spaces, we need two spaces on each side of the current row to center the printout
			for (int spacesCounter = 0; spacesCounter < ((NUM_ROWS - i) * 2); ++spacesCounter)
			{
					spaces += " ";
			}
			System.out.print( spaces + rows[i].getRowString() + spaces );
			System.out.printf( " = %d\n\n", rows[i].getRowValue() );
		}
	}
}

//****************************** GameBoardRow **********************************

//A helper class. Is, essentially, a linked list of Cards
class GameBoardRow
{
	public NodeCard firstCardInRow;
	public NodeCard lastCardInRow;
	
	private boolean hasKnight;
	
	
	GameBoardRow()
	{
		firstCardInRow = null;
		lastCardInRow = null;
		hasKnight = false;
	}
	
	//create row of certain length and put cards into it
	// note: this has no error checking on the Deck class, so Deck.drawOne may be returning null if we overflow. Don't overflow.
	public void populateRow( Deck deckToDraw, int length )
	{
		hasKnight = false;
		
		if ( length > 1 ) //two or more nodes
		{
			//create first node
			firstCardInRow = new NodeCard( deckToDraw.drawOne() ); //create a new Node with a preexisting Card object drawn from the Deck
			firstCardInRow.prev = null; //the first Node has no previous object
			if (firstCardInRow.value.getFaceInt() == 8)
				hasKnight = true;
			
			//create all middle nodes
			NodeCard currentNode = firstCardInRow;
			for ( int cardNum = 1; cardNum < (length - 1); ++cardNum )
			{
				currentNode.next = new NodeCard( deckToDraw.drawOne() ); //create new Node, and set the pointer in THIS node to it
				(currentNode.next).prev = currentNode; //set the new node's previous pointer to this node
				currentNode = currentNode.next; //move on the next node
				if (currentNode.value.getFaceInt() == 8)
					hasKnight = true;
			}
			
			//create last node
			currentNode.next = new NodeCard( deckToDraw.drawOne() ); //create new, point to it
			lastCardInRow = currentNode.next; //set the class's pointer to the last object
			lastCardInRow.prev = currentNode;
			lastCardInRow.next = null; //set up this node pointer to flag it as the last one
			if (lastCardInRow.value.getFaceInt() == 8)
				hasKnight = true;
			
		}
		else if ( length == 1 ) //manually set up one node
		{
			firstCardInRow = new NodeCard( deckToDraw.drawOne() );
			firstCardInRow.prev = firstCardInRow.next = null;
			lastCardInRow = firstCardInRow;
			//unneeded, already set 2 lines ago: lastCardInRow.prev = lastCardInRow.next = null;
			if (lastCardInRow.value.getFaceInt() == 8)
				hasKnight = true;
		}
		else //length <= 0. This should not be happening, but assign null values by default anyway.
		{
			firstCardInRow = lastCardInRow = null;
		}
	} //end GameBoardRow.populateRow()
	
	public boolean rowHasKnight()
	{
		return hasKnight;
	}
	
	public int getRowValue()
	{
		int total = 0;
		//Taverse linked list; until the Node.next is a NULL value, thus is the last card
		//this will also cause the method to return 0 if the row is empty and thus firstCard is null
		NodeCard currentNode = firstCardInRow;
		while ( currentNode != null)
		{
			total += (currentNode.value).getFacePointValue(); //method of Card class, returns the card's value
			currentNode = currentNode.next; //traverse the list, will be set to NULL when reaching the lastCard
		}
		return total;
	} //end GameBoardRow.getRowValue()
	
	public boolean getRowMatched()
	{
		int prevCard = -1;
		NodeCard currentNode = firstCardInRow;
		while ( currentNode != null)
		{
			if (prevCard == -1) //is the first card
				prevCard = currentNode.value.getFaceInt();
			else
			{
				if (currentNode.value.getFaceInt() != prevCard)
					return false; //found a single one that doesn't match, leave immediately
				prevCard = currentNode.value.getFaceInt();
			}
			currentNode = currentNode.next;
		}
		return true; //did not find any non-matching pairs along the row
	}
	
	public String getRowString()
	{
		String result = "";
		//Taverse linked list; until the Node.next is a NULL value, thus is the last card
		// If row is empty, will return the empty string
		NodeCard currentNode = firstCardInRow;
		while ( currentNode != null)
		{
			result += (currentNode.value).getFaceOutput() + " "; //method of Card class, returns "|x|", where x is the value
			currentNode = currentNode.next; //traverse the list, will be set to null when reaching the lastCard
		}
		//NOTE: This will return with a trailing space...do I need to fix that? No, the trailing string is a good spacer.
		return result;
	}
}


//******************************* NodeCard *************************************
//A node of a GameBoardRow
//A separate Node class is needed, since GameBoardRow is a linked list, and not simply an array of Card objects
class NodeCard
{
	//Class members
	//All made PUBLIC to be accessible inside GameBoardRow
	public CardBurnable value;
	public NodeCard next;
	public NodeCard prev;
	
	NodeCard( CardBurnable valueToSet )
	{
		value = valueToSet;
		next = null;
		prev = null;
	}
	
	NodeCard() //Note: default constructor will probably not be used
	{
		value = null;
		next = null;
		prev = null;
	}
}
//Andrew East - 2274193
//Final Project
//CIS 18a - Java - 47380

//CardBurnable.java: A class to hold a "Fortune's Tower" card.
//...extended to be able to know its state.

public class CardBurnable extends Card
{
	protected boolean burned; //is a card that matches another above itself
	protected boolean burner; //is a card that matches one below
	protected boolean saved; //is a card that would normally be isBurned, but has been saved by a Knight
	
	public CardBurnable()
	{
		super(); //assign default to Card
		burned = false; //card is "safe", or not "burned" by default
		burner = false;
		saved = false;
	}	
	public CardBurnable(int newFace)
	{
		super(newFace);
		burned = false; //card is "safe", or not "burned" by default
		burner = false;
		saved = false;
	}
	
	public boolean isBurned()
	{
		return burned;
	}
	public boolean isBurner()
	{
		return burner;
	}
	public boolean isSaved()
	{
		return saved;
	}
	
	public void reset()
	{
		burned = burner = saved = false;
	}
	public void setBurned()
	{
		burned = true;
	}
	public void setBurner()
	{
		burner = true;
	}
	public void setSaved()
	{
		saved = true;
	}
	
	public String getFaceOutput()
	{
		String c = (burned) ? "*" : "|";
		switch (cardFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return c + cardFace + c; //return proper card
				//not needed, since returned already: break;
			case 8:
				return c + "K" + c; //knight card
				//not needed, since returned already: break;
			default:
				return "|e|"; //for error
		}
	}
}

class Card
{
	// Private members
	protected int cardFace; //values: 1-7, 8=knight
	
	public Card()
	{
		cardFace = 0; //technically invalid card
	}
	
	public Card( int newFace )
	{
		//using switch statement, since it should execute faster than an if statement for int
		switch (newFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				cardFace = newFace; //valid card
				break;
			default:
				cardFace = 0; //invalid card returned
		}
	}
	
	public void setFace( int newFace )
	{
		switch (cardFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				cardFace = newFace; //valid card
				break;
			default:
				cardFace = 0; //invalid card returned
		}
	}
	
	public char getFaceChar()
	{
		switch (cardFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return (char)(cardFace + '0'); //unicode value of digit
			case 8:
				return 'K'; //knight card
			default:
				return 'e'; //for error
		}
	}

	public int getFaceInt()
	{
		switch (cardFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8: //knights are returned as 8 for the board to display
				return cardFace;
			default:
				return 0; //error value
		}
	}
	
	public int getFacePointValue()
	{
		switch (cardFace)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return cardFace;
			case 8:
				return 0; //Knight worth NO points
			default:
				return 0; //error value
		}
	}
}

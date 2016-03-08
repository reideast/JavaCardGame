//Andrew East - 2274193
//Final Project
//CIS 18a - Java - 47380

//PlayerBank.java: The player's winnings, encapsulated

public class PlayerBank
{
	private int bank;
	private int intialValue;
	
	PlayerBank()
	{
		bank = 0;
		intialValue = 0;
	}
	PlayerBank( int bankValue )
	{
		bank = bankValue;
		intialValue = bankValue;
	}
	
	//Removes value from the bank if the amount is available,
	// or returns false and does not perform the debit.
	public boolean debitBank( int debitValue )
	{
		if ((bank - debitValue) < 0)
		{
			return false; //error returned
		}
		else
		{
			bank -= debitValue;
			return true;
		}
	}
	
	//Add an amount into the bank, as long as that amount is greater than zero (no debit allowed)
	public boolean creditBank( int creditValue )
	{
		if (creditValue >= 0)
		{
			bank += creditValue;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//Get the current value of the bank.
	public int getBank()
	{
		return bank;
	}
	
	public int getIntialValue()
	{
		return intialValue;
	}
	
	//Change the value of the bank.
	//Useful for resetting the bank to a default value.
	public void setBank( int bankValue )
	{
		bank = bankValue;
	}
}
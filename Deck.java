//Andrew East - 2274193
//Final Project
//CIS 18a - Java - 47380

//Deck.java A deck of cards; contains an array of cards for the "Fortune's Tower" game.
//Allows shuffling and drawing cards.

import java.util.Random;

public class Deck
{
	//Deck parameters
	private final int NUM_SHUFFLES = 4; //how many times to run through the whole deck, swapping cards
	private final int DECK_SIZE = 62; //62 = (8 * 7) + 6 eight cards each of value 1-7, and 6 Knights
	
	//Private members
	private CardBurnable[] cards; //the actual "deck" of cards
	private int currentCard; //the internal pointer of the deck, starts at 0. resets whenever the deck is shuffled

	//the constructor fills card array with appropriate Cards face values
	Deck()
	{
		cards = new CardBurnable[DECK_SIZE]; //only 56 cards = eight cards of face value 1-7
		
		int currentFaceValue = 0; //goes from 1-8 to set the face value of cards; starts at 0 since the first loop will increment it to 1
		for (int i = 0; i < DECK_SIZE; ++i) //loop through to create all cards
		{
			if ((i % 8) == 0) //rotate card value every eight cards
				++currentFaceValue;
			cards[i] = new CardBurnable(currentFaceValue);
		}
		
		currentCard = 0; //start the pointer at the first card
	}
	
	//returns one card, and moves the internal pointer on to the next card in the deck
	//if the deck is empty, returns null
	public CardBurnable drawOne()
	{
		if ( currentCard < DECK_SIZE ) //if the internal pointer has gone over the size of the array, return an error
		{
			++currentCard; //increment this first, since the return statement will NOT allow it as a side effects (from my testing)
			return cards[(currentCard - 1)]; //step back one card, since it was already incremented
		}
		else
		{
			return null; //note: there will be a fatal error if a method is attempted to be called on the returned Card. must be careful!
		}
	}
	
	//Loops through the whole deck a number of times, swapping each card with a different, random card
	public void shuffle()
	{
		Random randomNumbers = new Random(); //seeded by system clock automaticaly with creation
		int randomIndex;
		CardBurnable temp;
		for (int deckRepitition = 0; deckRepitition < NUM_SHUFFLES; ++deckRepitition)
		{
			for (int i = 0; i < DECK_SIZE; ++i)
			{
				randomIndex = randomNumbers.nextInt(DECK_SIZE);
				temp = cards[i];
				cards[i] = cards[randomIndex];
				cards[randomIndex] = temp;
			}
		}
		for (int i = 0; i < DECK_SIZE; ++i)
			cards[i].reset(); //reset all card state (of being "burned" or not
		currentCard = 0; //re-start the pointer at the first card
	}
}

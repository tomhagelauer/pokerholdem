package com.thagel.pokerholdem.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



import com.thagel.pokerholdem.model.Card;
import com.thagel.pokerholdem.model.CardColor;
import com.thagel.pokerholdem.model.CardValue;
import com.thagel.pokerholdem.model.HoldemGame;

public class HoldemDAO {
	private static final int MIN_CARD_NUM=1;	
	private static final int MAX_CARD_NUM=52;	

	private Card[] p1Cards = {	// 2 cards Player1
            new Card (CardColor.DIAMOND, CardValue.ACE),
            new Card (CardColor.HEARTS, CardValue.ACE)
		};
	private Card[] p2Cards = {	// 2 cards Player2
            new Card (CardColor.CLUB, CardValue.TEN),
            new Card (CardColor.SPADE, CardValue.JACK)
		};
	private Card[] flopCards = new Card []{	// 3 cards on board
			new Card (CardColor.HEARTS, CardValue.SIX), 
			new Card (CardColor.HEARTS, CardValue.TEN), 
			new Card (CardColor.DIAMOND, CardValue.TEN) 
	};
	private Card turnCard = 
			new Card (CardColor.CLUB, CardValue.QUEEN); // another one	
	private Card riverCard = 
			new Card (CardColor.SPADE, CardValue.ACE); //finally!

	public void run (boolean autoMode) { 
		if (autoMode) {	// generate all cards (9 different cards) 
			HoldemGame.create();
			List<Card> drawCards = new ArrayList<Card>();
			int i=0;
			do {
				int num = this.getRandomNumberInRange(MIN_CARD_NUM, MAX_CARD_NUM);
				Card c = HoldemGame.getCards().get(num);	// is this card allready used?
				if (drawCards.contains(c))	
					continue;	// allready in list
				drawCards.add(c);
			}while (drawCards.size()<9);	
				// update:
			this.p1Cards[0] = drawCards.get(0);
			this.p1Cards[1] = drawCards.get(1);
			this.p2Cards[0] = drawCards.get(2);
			this.p2Cards[1] = drawCards.get(3);
			this.flopCards[0] = drawCards.get(4);
			this.flopCards[1] = drawCards.get(5);
			this.flopCards[2] = drawCards.get(6);
			this.turnCard = drawCards.get(7);
			this.riverCard = drawCards.get(8);
		}// else use predefined cards!
	}

	public Card[] getP1Cards() {
		return p1Cards;
	}

	public void setP1Cards(Card[] p1Cards) {
		this.p1Cards = p1Cards;
	}

	public Card[] getP2Cards() {
		return p2Cards;
	}

	public void setP2Cards(Card[] p2Cards) {
		this.p2Cards = p2Cards;
	}

	public Card[] getFlopCards() {
		return flopCards;
	}

	public void setFlopCards(Card[] flopCards) {
		this.flopCards = flopCards;
	}

	public Card getTurnCard() {
		return turnCard;
	}

	public void setTurnCard(Card turnCard) {
		this.turnCard = turnCard;
	}

	public Card getRiverCard() {
		return riverCard;
	}

	public void setRiverCard(Card riverCard) {
		this.riverCard = riverCard;
	}

	private int getRandomNumberInRange(int min, int max) {
		Random r = new Random();	// min & max inclusive (1 to 52 integer randomly returned)
		return r.nextInt((max - min) + 1) + min;
	}

}

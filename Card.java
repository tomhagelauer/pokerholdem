package com.thagel.pokerholdem.model;

public class Card  {
	public CardColor col;
	public CardValue val;
	public Card(CardColor col, CardValue val) {
		super();
		this.col = col;
		this.val = val;
	}
	
	public Card() {
		this(CardColor.SPADE, CardValue.ACE);
	}

	@Override
	public String toString() {
		return col.val +" "+ val.val;
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		Card c=(Card)o;
		return this.col == c.col && this.val==c.val;
	}


}

package com.thagel.pokerholdem.model;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HandEvaluator {
	private List<Card> hand;
	private int note;
	private TreeMap<Integer, Integer> map=new TreeMap<Integer, Integer>();
	private boolean isAStraight=true;
	private boolean isAFlush=true;
	private boolean isRoyal=false;

	public HandEvaluator(List<Card> hand) {
		super();
		this.hand = hand;
		this.note = 900;
		this.prepare();
	}

	public void run() {
		/*	10 possible ordered hands with a scale of attributed values
		 * 1/ 0-100/ highest card : 14=Ace ... 2=2 (5 differents with highest Knight is 11)
		 * 2/ 100-200/ pair (pair of king is 113)
		 * 3/ 200-300/ dblPair (dblpair knight/six is 211)
		 * 4/ 300-400/ threeOfAKind (3 10 is 310)
		 * 5/ 400-500/ Straight (to King is 414)
		 * 6/ 500-600/ Flush (color is allways 500)
		 * 7/ 600-700/ Full House (3 aces/2 10 is 614)
		 * 8/ 700-800/ Square Le Carré (4 8 is 708)
		 * 9/ 800-900/ StraightFlush (to Knight is 811)
		 * 10/ 900/ Flush Royal 
		 */
				// criteria from highest to lowest
		if (this.isAFlush && this.isAStraight && this.isRoyal) {// 10 flush+straight+toAce
			note = 900;
			return;
		}
		if (this.isAFlush && this.isAStraight) {// 9 flush+straight
			note = 800 + this.getHighestCard(1);
			return;
		}
		if (this.map.containsValue(4)) {	// 8/ square
			note = 700+ this.getHighestCard(4);
			return;
		}
		if (this.map.containsValue(3)&&this.map.containsValue(2)) {	// 7/ full House 3+2
			note = 600 + this.getHighestCard(3);
			return;
		}
		if (this.isAFlush) {// 6 flush
			note = 500;
			return;
		}
		if (this.isAStraight) {// 5 straight simple
			note = 400 + this.getHighestCard(1);
			return;
		}
		if (this.map.containsValue(3)) {	// 4/ threeOfAKind
			note = 300 + this.getHighestCard(3);
			return;
		}
		if (this.map.containsValue(2)&&this.map.size()==3) {	// 3/ 2+2+1
			note = 200+ this.getHighestCard(2);
			return;
		}
		if (this.map.containsValue(2)&&this.map.size()==4) {	// 2/ 2+1+1+1
			note = 100 + this.getHighestCard(2);
			return;
		}
		if (! this.map.containsValue(2)) {	// 1/ 1+1+1+1+1
			note = this.getHighestCard(1);
			return;
		}
	}


	public int getNote() {
		return note;
	}

	private void prepare() {
		// generate an ordered map, key=weight value=occurence
		// ie: Hand(Ace+Ace+king+nine+two) is: 2->1 9->1 13->1 14->2
		CardColor col= null;
		for (Card c:this.hand) {
			Integer val= this.map.get(c.val.getWeight());
			if (val == null)
				this.map.put(c.val.getWeight(), 1);
			else 
				this.map.put(c.val.getWeight(), ++val);
			// Flush or not?
			if (col==null)	// first card
				col = c.col;
			else {
				if (col!=c.col)
					this.isAFlush= false;
			}
		}
		// straight or not?
		if (this.map.size() != 5)
			this.isAStraight= false;
		else {
			int w=0;
			for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
				if (w==0)	// first card
					w = entry.getKey();
				else {
					if (w+1 != entry.getKey())
						this.isAStraight= false;
					else
						w++;
				}	
			}
		}
		// royal?
		if (this.map.lastKey().equals(14))
			isRoyal = true;	// last card is an ACE
	}

	private int getHighestCard(int val) {
		// because ACE>KING>...>2 : val is the number of occurrences of key 
		Set<Integer> keys = map.keySet();
		int sol1 = 0;
		int sol2 = 0;
		for(int key: keys){
			if (map.get(key) == val) {	// pb if two pairs!
				if (sol1==0)
					sol1=key;	
				else
					sol2=key;
			}
		}	
			// in cas of two pairs so 2 values, take the highest
		int sol = sol1>sol2?sol1:sol2;
		return sol>0?sol:0;
	}

	@Override
	public String toString() {
		return "HandNote [hand=" + hand + ", note=" + note + "]";
	}

}

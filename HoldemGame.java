package com.thagel.pokerholdem.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HoldemGame {
	private static final int MIN_CARD_NUM=1;	
	private static final int MAX_CARD_NUM=52;	
	private static Map<Integer, Card> cards = null;	// 52 cards!

	private Card []  list1 = null;	// initial lists
	private Card []  list2 = null;
	private List<Card> p1Cards = null;	// dynamic hands
	private List<Card> p2Cards= null;
	private Card []  flopCards = null;
	private Card turnCard = null;
	private Card riverCard = null;
	private int probs[] = {50, 50, 0};	// p1 wins, p2 wins, null
	private int draws = 0;
	private int[] bestScore = {0, 0};


	public HoldemGame(Card [] list1, Card []  list2) {
		this (list1, list2, null);
	}
	public HoldemGame(Card [] list1, Card []  list2, Card [] flopCards ) {
		this (list1, list2, flopCards, null);
	}
	public HoldemGame(Card [] list1, Card []  list2, Card [] flopCards, Card turn ) {
		this (list1, list2, flopCards, turn, null);
	}
	public HoldemGame(Card [] list1, Card []  list2, Card [] flopCards, Card turn, Card river ) {
		create(); // create 52 cards
		this.list1 = list1;
		this.list2 = list2;
		this.flopCards = flopCards;
		this.turnCard=turn;
		this.riverCard=river;
		this.initialize();	// init list with 2 cards
	}
	public void run() {
		this.run(1);
	}

	public void run(int loops) {
		this.draws = this.riverCard!=null?1:loops;	// with 5 cards no need to iterate
		// counts wins of P1 & P2 to set probas
		int[] wins=this.getWinNumber();
		this.probs [0]= (int) ((float)wins[0]*100/(float)this.draws);
		this.probs [1]= (int) ((float)wins[1]*100/(float)this.draws);
		this.probs [2]= 100 - this.probs [0] - this.probs [1];
	}

	public static void create() {	// 52 cards to create once 1->2Spade, 2->3Spade... 52->AceClub
		if (getCards() != null)	// do it once!
			return;
		int i=1;
		cards = new HashMap<Integer, Card>();
		for(CardColor c:CardColor.values()) {
			for (CardValue v:CardValue.values()) {
				getCards().put(i++, new Card(c, v));
			}
		}
	}
	
	@Override
	public String toString() {
		String s = "" + 
//				(this.draws==1 ? "" : "draws="+this.draws+" ") + 
				"p1Cards=" + p1Cards + ", p2Cards=" + p2Cards +
				(this.flopCards == null ? "" : " flop=" + Arrays.asList(this.flopCards)) + 
				(this.turnCard == null ? "" : " turn=" + this.turnCard) + 
				(this.riverCard == null ? "" : " river=" + this.riverCard);
		if (this.riverCard == null) {
			s += "\t-> probas=" + Arrays.toString(probs);  
		}
		else {	// play is over -> result:
			if (this.probs[0] == 100) {
				s += "\n\t -> the Winner is: PLAYER1" + 
						" by: " + this.bestScore[0] + " to: " + this.bestScore[1];		
			}
			else if (this.probs[1] == 100) {
				s += "\n\t -> the Winner is: PLAYER2" + 
						" by: " + this.bestScore[1] + " to: " + this.bestScore[0];						
			}
			else {
				s += "\n\t -> null game between PLAYER1 & PLAYER2";
			}
		}
		return s;
	}

	//------------------------------------- private below

	private int [] getWinNumber() {
		// four different algos 
		int [] wins = new int [] {0, 0};
		if (this.riverCard != null) {
			// all is shown, just show the winner 
			// by running all 10 combinations
			Card[][] combs = this.generateAll3CardsCombinations(this.turnCard, this.riverCard);
			for (Card[] comb:combs) {
				p1Cards.addAll(Arrays.asList(comb));
				p2Cards.addAll(Arrays.asList(comb));
				int note1 = this.noteHand(true);
				int note2 = this.noteHand(false);
				if (note1>this.bestScore[0])
					this.bestScore[0] = note1;
				if (note2>this.bestScore[1])
					this.bestScore[1] = note2;
				this.initialize();
			}
			wins[0] += this.bestScore[0]>this.bestScore[1] ? 1:0;
			wins[1] += this.bestScore[1]>this.bestScore[0] ? 1:0;
			return wins;
		}
		if (this.turnCard != null) {
			// 2 fixed cards + 3 cards on board + 1
			for (int i=0; i < this.draws; i++) {
				// generate 1 new card (not in P1, P2, flopCards, nor turn)
				// and explore all combination, retain only 3 of (3 board + 2)
				Card  oneCard = this.drawRiver();
				// 10 combinations are possible (C4/5 in mathematic: 3 of 5 cards to add to P1 & P2)
				Card[][] combs = this.generateAll3CardsCombinations(this.turnCard, oneCard);
				int bestNote1=0;
				int bestNote2=0;
				for (Card[] comb:combs) {
					p1Cards.addAll(Arrays.asList(comb));
					p2Cards.addAll(Arrays.asList(comb));
					int note1 = this.noteHand(true);
					int note2 = this.noteHand(false);
					if (note1>bestNote1)
						bestNote1 = note1;
					if (note2>bestNote2)
						bestNote2 = note2;
					this.initialize();
				}
				wins[0] += bestNote1>bestNote2 ? 1:0;
				wins[1] += bestNote2>bestNote1 ? 1:0;
			}
			return wins;
		}
		if (this.flopCards != null) {
			// 2 fixed cards + 3 cards on board
			for (int i=0; i < this.draws; i++) {
				// generate 2 new cards (not in P1, P2 and flopCards)
				// and explore all combination, retain only 3 of (3 board + 2)
				Card [] twoCards = this.drawTurnAndRiver();
				// 10 combinations are possible (C3/5 in maths: 3 of 5 cards to add to P1 & P2)
				Card[][] combs = this.generateAll3CardsCombinations(twoCards[0], twoCards[1]);
				int bestNote1=0;
				int bestNote2=0;
				for (Card[] comb:combs) {
					p1Cards.addAll(Arrays.asList(comb));
					p2Cards.addAll(Arrays.asList(comb));
					int note1 = this.noteHand(true);
					int note2 = this.noteHand(false);
					if (note1>bestNote1)
						bestNote1 = note1;
					if (note2>bestNote2)
						bestNote2 = note2;
					this.initialize();
				}
				wins[0] += bestNote1>bestNote2 ? 1:0;
				wins[1] += bestNote2>bestNote1 ? 1:0;
			}
			return wins;
		}
			// with P1 & P2 (2 cards)
		for (int i=0; i < this.draws; i++) {
			this.draw(5);	// fill list until 5
			int res= this.compare();
			wins[0] += res ==1 ? 1:0;
			wins[1] += res ==-1 ? 1:0;
			this.initialize();
		}
		return wins;
	}
	private Card [][] generateAll3CardsCombinations(Card turn, Card river) {
		// with 3 cards flop + turn + river returns all combination
		//{this.flopCards[0], this.flopCards[1], this.flopCards[2], turn, river},
		Card [][] stack = new Card [][] 
				{
			{this.flopCards[0], this.flopCards[1], this.flopCards[2]},
			{this.flopCards[0], this.flopCards[1], turn},
			{this.flopCards[0], this.flopCards[1], river},
			{this.flopCards[0], this.flopCards[2], turn},
			{this.flopCards[0], this.flopCards[2], river},
			{this.flopCards[0], turn, river},
			{this.flopCards[1], this.flopCards[2], turn},
			{this.flopCards[1], this.flopCards[2], river},
			{this.flopCards[1], turn, river},
			{this.flopCards[2], turn, river},
				};
		return stack;

	}
	private void draw(int size) {	
		// fill both lists until 'size'(5) (by randomizing cards)
		// cards must be unique of course!
		do {
			int num = this.getRandomNumberInRange(MIN_CARD_NUM, MAX_CARD_NUM);
			Card c = getCards().get(num);
			if (this.p1Cards.contains(c) || this.p2Cards.contains(c))
				continue;	// must not belong to players of course, retry...

//			if (this.p1Cards.size() < size)  // first fill p1cards
//				this.p1Cards.add(c);
//			else {		// p1Cards is filled, fill now p2cards 
//				if (this.p2Cards.size() < size) 	// must not belong to p1cards
//					this.p2Cards.add(c);
//			}
			if (this.p1Cards.size() < size) {	// add Card to both hands
				this.p1Cards.add(c);
				this.p2Cards.add(c);
			}
		}while (this.p1Cards.size()<size || this.p2Cards.size()<size);	// both lists are filled

		// show draws (to comment if lot of draws!)
		//System.out.println(this.p1Cards);
		//System.out.println(this.p2Cards);
	}

	private Card [] drawTurnAndRiver () {
		Card [] turnAndRiver = new Card [2];
		int i=0;
		do {
			int num = this.getRandomNumberInRange(MIN_CARD_NUM, MAX_CARD_NUM);
			Card c = getCards().get(num);
			if (this.p1Cards.contains(c) || this.p2Cards.contains(c) || 
					Arrays.asList(this.flopCards).contains(c))
				continue;	// must not belong to players nor flop.
			turnAndRiver [i++] = c;
		}while (i<2);	// both lists are filled
		return turnAndRiver;
	}
	private Card drawRiver () {
		Card river = null;
		do {
			int num = this.getRandomNumberInRange(MIN_CARD_NUM, MAX_CARD_NUM);
			Card c = getCards().get(num);
			if (this.p1Cards.contains(c) || this.p2Cards.contains(c) || 
					Arrays.asList(this.flopCards).contains(c) ||
					c==this.turnCard)
				continue;	// must not belong to players nor flop.
			river  = c;
		}while (river == null);	
		return river;
	}

	private int compare() {
		int note1 = this.noteHand(true);	// search of best hand for each
		int note2 = this.noteHand(false);
		// show note of both hands
		//System.out.println(note1 + " " + note2);
		return note2<note1?1:(note1<note2)?-1:0;	// 3 cases (>0 p1 wins, <0 p2 wins, 0 is same note for each )
	}

	private int noteHand(boolean isPlayer1) {	// once a hand is generated give it a note
		List<Card> cs= (isPlayer1) ? this.p1Cards : this.p2Cards;
		HandEvaluator hN = new HandEvaluator(cs);
		hN.run();
		int note = hN.getNote();
		//System.out.println(isPlayer1 + " " + note + (!isPlayer1?"\n":""));
		return note;
	}

	private int getRandomNumberInRange(int min, int max) {
		Random r = new Random();	// min & max inclusive (1 to 52 integer randomly returned)
		return r.nextInt((max - min) + 1) + min;
	}

	private void initialize() {
		this.p1Cards = new ArrayList<Card>();	// dynamic hands
		this.p2Cards= new ArrayList<Card>();
		this.p1Cards.addAll(Arrays.asList(this.list1));		// initial values to add
		this.p2Cards.addAll(Arrays.asList(this.list2));
	}
	public static Map<Integer, Card> getCards() {
		return cards;
	}

}

package com.thagel.pokerholdem.controller;

import com.thagel.pokerholdem.dao.HoldemDAO;
import com.thagel.pokerholdem.model.HoldemGame;


public class HoldemController {
	// draws number (the higher the more it fits to maths probas: 10000 is a good compromise)
	private int draws = 1000;
	private int gameNb = 1;
	private boolean autoMode=false;

	public static void main(String[] args) {
		HoldemController holdemController = new HoldemController();
		holdemController.checkArgs(args);
		holdemController.readme();
		for (int i=0; i< holdemController.gameNb; i++) {
			holdemController.aGame(i);
		}
	}
	private void checkArgs (String[] args) {
		if (args.length==0) {
			return;
		}
		this.autoMode= true;
		try {
			this.gameNb = Integer.parseInt(args[0]);
			this.draws = Integer.parseInt(args[1]);
		} catch (Exception e) {
			this.readme();
			System.exit(2);
			
		}
		
	}
	private void aGame(int gameNum) {
		System.out.println("\n\t\t->GAME: " + (++gameNum));
		
		HoldemDAO holdemDao = new HoldemDAO();
		holdemDao.run(this.autoMode);
		// 1/ start with 2 cards each player and display probas
		HoldemGame holdem=new HoldemGame(
				holdemDao.getP1Cards(), 
				holdemDao.getP2Cards());	
		holdem.run(this.draws);
		System.out.println(holdem);
		// 2/ flop -> display 3 cards on board and update probas
		holdem=new HoldemGame(
				holdemDao.getP1Cards(), 
				holdemDao.getP2Cards(), 
				holdemDao.getFlopCards());	
		holdem.run(this.draws);
		System.out.println(holdem);
		// 3/ turn -> show another one and and update
		holdem=new HoldemGame(
				holdemDao.getP1Cards(), 
				holdemDao.getP2Cards(), 
				holdemDao.getFlopCards(),
				holdemDao.getTurnCard());	
		holdem.run(this.draws);
		System.out.println(holdem);
		// 4/ river -> show last card and display winner!
		holdem=new HoldemGame(
				holdemDao.getP1Cards(), 
				holdemDao.getP2Cards(), 
				holdemDao.getFlopCards(),
				holdemDao.getTurnCard(), 
				holdemDao.getRiverCard());	
		holdem.run();
		System.out.println(holdem);

	}
	private void readme() {
		final String README = 
				"\tTEXAS HOLD'EM Poker\n" + 
						"\t\t\t-1/ 2 hidden cards are given to 2 players -> show % success chances of each player (including null)\n"+
						"\t\t\t-2/ flop: display 3 visible cards on board ->  update %\n"+
						"\t\t\t-3/ turn: display another 1 visible card on board -> update %\n"+
						"\t\t\t-4/ river: display another 1 visible card on board -> display Winner and best possible hand value\n"+
						"[rules: 0-100: 5 diff cards(highest wins); 100<200: 1 pair; 200<300: 2 pairs; 300<400: 3OfAKind; 400<500: Straight; 500: flush;\n600<700: full house; 700<800: square; 800<900: straight+flush; 900: royal straight flush]\n"+
						"\n\n<howToInvoke by arguments>\n" + 
						"-1/ <noargument>: -> 1 game with manual mode(cards allready injected) / 1000 draws for each phasis (1, 2, 3) to set probas\n"+
						"-2/ aNumberOfGames aNumberOfDraws  \n"+
						"   ie: 20 10000 -> 20 games / 10000 draws each phasis (1, 2, 3) to set probas (default: 1000)\n"+
						"-3/ TODO: inject cards in manual mode by GUI, hope soon!!\n"+
						"--------------------------------------------------------------------";
		System.out.println(README);
	}
}

package com.thagel.pokerholdem.model;


public enum CardColor {
	SPADE ("SPADE"),
	HEARTS ("HEARTS"),
	DIAMOND ("DIAMOND"),
	CLUB ("CLUB");
	
	String val;
	CardColor(String val) {
		// TODO Auto-generated constructor stub
		this.val=val;
	}
	public String getVal() {
		return val;
	}

}

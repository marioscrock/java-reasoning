package app.eshop.user;

import java.util.Random;

public class Guest extends User{
	
	public Guest () {
		super("Guest" + Integer.toString((new Random()).nextInt(Integer.MAX_VALUE)));
	}

}

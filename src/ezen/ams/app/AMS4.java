package ezen.ams.app;

import ezen.ams.gui.AMSFrame2;

public class AMS4 {

//	public static AccountRepository repository = new JdbcAccountRepository();

	public static void main(String[] args) {
		
		AMSFrame2 ams2 = new AMSFrame2("EZEN-BANK AMS");
		ams2.addEventListner();
		ams2.setSize(500,500);
		ams2.setVisible(true);
	}
}

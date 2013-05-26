package fr.upmc.pc2r.bnr.test;

import fr.upmc.pc2r.bnr.model.ClientController;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ClientController client = new ClientController("127.0.0.1","2012");
		//ClientController client = new ClientController("172.20.10.5","2012");
		//ClientController client = new ClientController("192.168.137.176","2012");
		//ClientController client = new ClientController("192.168.50.31","2012");
		//ClientController client = new ClientController("80.119.68.89","2012");
		//ClientController client = new ClientController("134.157.251.68","2012");
		ClientController.setClientController(client);
		
	}

}

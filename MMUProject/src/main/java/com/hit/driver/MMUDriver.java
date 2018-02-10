package com.hit.driver;

import com.hit.model.MMUModel;
import com.hit.view.MMUView;
import com.hit.controller.MMUController;
import com.hit.driver.CLI;

public class MMUDriver {


	public static void main(String[] args) {
		/**
		 * BUILD MVC model to demonstrate MMU system actions
		 */
		
		CLI cli = new CLI(System.in,System.out);
		MMUModel model = new MMUModel();
		MMUView view = new MMUView();
		MMUController controller = new MMUController(model, view);
		model.addObserver(controller);
		cli.addObserver(controller);
		view.addObserver(controller);
		new Thread(cli).start();	
	}
}

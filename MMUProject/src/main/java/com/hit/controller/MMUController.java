package com.hit.controller;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import com.hit.driver.CLI;
import com.hit.model.MMUModel;
import com.hit.model.Model;
import com.hit.view.MMUView;
import com.hit.view.View;

public class MMUController implements Controller, Observer {

	private Model model;
	private View view;

	public MMUController(Model model, View view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof CLI)
		{
			String [] config = (String [])arg1;
			((MMUModel)model).setConfiguratio(Arrays.asList(config));
			model.start();
		}
		
		if(arg0 instanceof Model)
		{
			((MMUView)view).setPreConfiguration(((MMUModel)model).getLogData());
			view.start();
		}
	}
}

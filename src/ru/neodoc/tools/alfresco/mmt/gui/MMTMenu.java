package ru.neodoc.tools.alfresco.mmt.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MMTMenu {

	protected ModuleManagementToolGUI moduleManagementToolGUI;
	
	protected FileDialog warFileDialog;
	
	protected Menu menu;
	
	public MMTMenu(Decorations arg0, int arg1, ModuleManagementToolGUI mmtg) {
		super();
		this.menu = new Menu(arg0, arg1);
		setModuleManagementToolGUI(mmtg);
		init();
	}

	protected void init() {
		this.warFileDialog = WarFileDialogCreator.create(this.getMenu().getShell(), SWT.OPEN);
		fillMenu();
	}
	
	protected void fillMenu() {
		
		MenuItem fileItem = new MenuItem(this.menu, SWT.CASCADE);
		fileItem.setText("&File");
		
		Menu fileMenu = new Menu(this.menu);
		fileItem.setMenu(fileMenu);
		MenuItem openFileItem = new MenuItem(fileMenu, SWT.NONE);
		openFileItem.setText("&Open...");
		openFileItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String result = warFileDialog.open();
				if (result!=null)
					MMTMenu.this.getModuleManagementToolGUI().setWarFile(result);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public ModuleManagementToolGUI getModuleManagementToolGUI() {
		return moduleManagementToolGUI;
	}

	public void setModuleManagementToolGUI(ModuleManagementToolGUI moduleManagementToolGUI) {
		this.moduleManagementToolGUI = moduleManagementToolGUI;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	
	
}

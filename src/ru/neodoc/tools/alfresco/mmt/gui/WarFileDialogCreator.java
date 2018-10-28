package ru.neodoc.tools.alfresco.mmt.gui;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class WarFileDialogCreator {

	public static FileDialog create(Shell arg0, int arg1) {
		FileDialog fd = new FileDialog(arg0, arg1);
		init(fd);
		return fd;
	}
	


	protected static void init(FileDialog fileDialog) {
		fileDialog.setFilterExtensions(new String[]{"*.war"});
		fileDialog.setText("Select war file");
	}
	
}

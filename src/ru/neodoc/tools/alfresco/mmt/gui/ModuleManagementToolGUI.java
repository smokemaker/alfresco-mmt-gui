package ru.neodoc.tools.alfresco.mmt.gui;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.alfresco.repo.module.tool.ModuleManagementTool;
import org.alfresco.repo.module.tool.WarHelper;
import org.alfresco.repo.module.tool.WarHelperImpl;
import org.alfresco.service.cmr.module.ModuleDetails;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;

public class ModuleManagementToolGUI extends ModuleManagementTool {

	protected static ModuleManagementToolGUI _INSTANCE = new ModuleManagementToolGUI();

	public static void main(String[] args) {
		_INSTANCE.initGUI();
	}
	
	protected WarHelper localWarHelper = new WarHelperImpl(this);
	
	public void initGUI() {
		
		setVerbose(true);
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Alfresco Module Management Tool GUI");
		
		createControls(shell);
		
		System.setOut(new PrintStream(new TextOutputStream(logViewer)));
		Image icon = new Image(display, getClass().getClassLoader().getResourceAsStream("logo_alfresco.gif"));
		shell.setImage(icon);
		shell.open();
		
		while(!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
		
	}
	
	protected String currentWarFile = null;
	protected ModuleDetails selectedModuleDetails = null;
	protected TableItem selectedTableItem = null;
	
	protected Label filePathLabel;
	
	protected Table moduleListTable;
	
	protected Button refreshButton;
	
	protected Button forceCheckbox;
	protected Button backupCheckbox;
	
	protected Button installButton;
	protected Button installPreviewButton;
	protected Button uninstallButton;
	protected Button uninstallPreviewButton;
	
	protected Button clearLogButton;
	
	protected Text logViewer;
	
	protected FileDialog moduleFileDialog;
	
	protected void createControls(Shell shell) {

		shell.setMenuBar((new MMTMenu(shell, SWT.BAR, _INSTANCE)).getMenu());
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		shell.setLayout(gl);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		Composite container = new Composite(shell, SWT.BORDER);
		container.setLayout(gl);
		container.setLayoutData(gd);

		createTopPanel(container);
		
		createTable(container);
		
		createLogViewer(container);
		
		moduleFileDialog = new FileDialog(shell);
		moduleFileDialog.setFilterExtensions(new String[]{"*.amp"});
		moduleFileDialog.setText("Select module AMP file");
		
		updateButtons();
	}
	
	protected String getSelectedJarFile() {
		return moduleFileDialog.open();
	}
	
	protected boolean isForce() {
		return (forceCheckbox!=null) && forceCheckbox.getSelection();
	}
	
	protected boolean isBackup() {
		return (backupCheckbox!=null) && backupCheckbox.getSelection();
	}
	
	protected void createTopPanel(Composite container) {
		Composite labelAndButtonsContainer = new Composite(container, SWT.NONE);
		GridLayout lbgl = new GridLayout();
		lbgl.numColumns = 8;
		labelAndButtonsContainer.setLayout(lbgl);
		labelAndButtonsContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData lbgd = new GridData(GridData.FILL_HORIZONTAL);
		lbgd.grabExcessHorizontalSpace = true;
		
		filePathLabel = new Label(labelAndButtonsContainer, SWT.NONE);
		filePathLabel.setLayoutData(lbgd);
		filePathLabel.setText("");
		// filePathLabel.setSize(100, 100);
		
		Composite checkboxContainer = new Composite(labelAndButtonsContainer, SWT.NONE);
		GridLayout cbgl = new GridLayout();
		cbgl.numColumns = 1;
		checkboxContainer.setLayout(cbgl);
		checkboxContainer.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		forceCheckbox = new Button(checkboxContainer, SWT.CHECK);
		forceCheckbox.setText("Force");
		forceCheckbox.setSelection(true);
		
		backupCheckbox = new Button(checkboxContainer, SWT.CHECK);
		backupCheckbox.setText("Backup war");
		
		refreshButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		installPreviewButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		installPreviewButton.setText("Preview Install..");
		installPreviewButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String jar = getSelectedJarFile();
				if (jar!=null) {
					disableButtons();
					try {
						logOperationStart("install preview", jar);
						installModule(jar, currentWarFile, true, isForce(), isBackup());
						logFinish();
						TVFS.umount((new TFile(jar)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateButtons();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		installButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		installButton.setText("Install..");
		installButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String jar = getSelectedJarFile();
				if (jar!=null) {
					disableButtons();
					try {
						logOperationStart("install", jar);
						installModule(jar, currentWarFile, false, isForce(), isBackup());
						logFinish();
						TVFS.umount((new TFile(jar)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					refresh();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		uninstallPreviewButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		uninstallPreviewButton.setText("Preview Uninstall");
		uninstallPreviewButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				disableButtons();
				try {
					logOperationStart("preview uninstall");
					uninstallModule(selectedModuleDetails.getId(), currentWarFile, true, false);
					logFinish();
				} catch (Exception e) {
					System.out.println(e);
				}
				updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		uninstallButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		uninstallButton.setText("Uninstall");
		uninstallButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				disableButtons();
				try {
					logOperationStart("uninstall");
					uninstallModule(selectedModuleDetails.getId(), currentWarFile, false, true);
					logFinish();
				} catch (Exception e) {
					System.out.println(e);
				}
				refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		clearLogButton = new Button(labelAndButtonsContainer, SWT.PUSH);
		clearLogButton.setText("Clear log");
		clearLogButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				logClear();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	protected void createTable(Composite container) {
		/*
         * CREATE SCROLLABLE TABLE
         */
        ScrolledComposite scrolled = new ScrolledComposite(container, SWT.V_SCROLL);
        scrolled.setLayout(new GridLayout());
        scrolled.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        scrolled.setMinHeight(400);
        
        moduleListTable = new Table(scrolled, SWT.CHECK | SWT.NO_SCROLL | SWT.FULL_SELECTION);
        moduleListTable.setHeaderVisible(true);
        
        scrolled.setContent(moduleListTable);
        scrolled.setExpandHorizontal(true);
        scrolled.setExpandVertical(true);
        scrolled.setAlwaysShowScrollBars(true);
        
        scrolled.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
        
        createColumn("Module ID");
        createColumn("Title");
        createColumn("Version");
        createColumn("Install Date");
        createColumn("Description");
        
        scrolled.setMinHeight(200);
        //scrolled.setMinSize(moduleListTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        //moduleListTable.set
        //moduleListTable.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
//        moduleListTable.addL
        moduleListTable.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (arg0.detail==SWT.CHECK) {
					TableItem ti = (TableItem)arg0.item;
					if (ti == selectedTableItem)
						ti.setChecked(!ti.getChecked());
					processTableItemSelection(ti);
				} else {
					TableItem ti = (TableItem)arg0.item;
					ti.setChecked(!ti.getChecked());
					processTableItemSelection(ti);
				}
				updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				//System.out.println(arg0.detail);
			}
		});
	}

	protected void processTableItemSelection(TableItem tableItem) {
		moduleListTable.setSelection(tableItem);
		if (selectedTableItem!=null && selectedTableItem!=tableItem)
			selectedTableItem.setChecked(false);
		selectedTableItem = null;
		selectedModuleDetails = null;
		if (tableItem.getChecked()) {
			selectedTableItem = tableItem;
			selectedModuleDetails = (ModuleDetails)selectedTableItem.getData();
		}
	}
	
	protected void createLogViewer(Composite container) {
/*		ScrolledComposite scrolled = new ScrolledComposite(container, SWT.V_SCROLL);
        scrolled.setLayout(new GridLayout());
        scrolled.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolled.setExpandVertical(true);
        //scrolled.setMinHeight(600);
        scrolled.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
*/		
        logViewer = new Text(container, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER);
        logViewer.setLayoutData(new GridLayout());
        logViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        logViewer.setText("");
        
        // scrolled.setContent(logViewer);
	}
	
	protected void refresh() {
		loadFile(currentWarFile);
		if ((selectedTableItem!=null) && (moduleListTable.indexOf(selectedTableItem)>=0)) {
			moduleListTable.setSelection(selectedTableItem);
		} else {
			selectedTableItem = null;
			selectedModuleDetails = null;
			updateButtons();
		}
	}
	
	protected void updateButtons () {
		refreshButton.setEnabled((this.currentWarFile!=null) && (this.currentWarFile.trim().length()>0));
		
		installButton.setEnabled(currentWarFile!=null);
		installPreviewButton.setEnabled(currentWarFile!=null);
		
		uninstallButton.setEnabled(selectedModuleDetails!=null);
		uninstallPreviewButton.setEnabled(uninstallButton.getEnabled());
	}
	
	protected void disableButtons () {
		refreshButton.setEnabled(false);
		
		installButton.setEnabled(false);
		installPreviewButton.setEnabled(false);
		
		uninstallButton.setEnabled(false);
		uninstallPreviewButton.setEnabled(false);
	}
	
	protected void createColumn(String header) {
        TableColumn column = new TableColumn(moduleListTable, SWT.NONE);
        column.setText(header);
	}
	
	public void setWarFile(String path) {
		filePathLabel.setText(path);
		currentWarFile = path;
		loadFile(path);
	}
	
	public void loadFile(String path) {
		moduleListTable.removeAll();
		List<?> modulesFound = this.localWarHelper.listModules(new TFile(path));
		for (Iterator<?> iterator = modulesFound.iterator(); iterator.hasNext(); ) {
			ModuleDetails moduleDetails = (ModuleDetails)iterator.next();
			TableItem ti = new TableItem(moduleListTable, SWT.NONE);
			ti.setData(moduleDetails);
			ti.setText(0, moduleDetails.getId());
			ti.setText(1, moduleDetails.getTitle());
			ti.setText(2, moduleDetails.getModuleVersionNumber().toString());
			ti.setText(3, moduleDetails.getInstallDate()==null?"":moduleDetails.getInstallDate().toLocaleString());
			ti.setText(4, moduleDetails.getDescription());
		}
		for (TableColumn tc: moduleListTable.getColumns()) {
			tc.pack();
		}
		updateButtons();
	}
	protected void logOperationStart(String operationName) {
		logOperationStart(operationName, selectedModuleDetails==null?"unknown":selectedModuleDetails.getId());
	}
	
	protected void logClear() {
		logViewer.setText("");
	}
	
	protected void logOperationStart(String operationName, String module) {
		logViewer.append("\n\n");
		logViewer.append("##---------------------- ");
		logViewer.append("\n");
		logViewer.append("##     Operation: " + operationName);
		logViewer.append("\n");
		logViewer.append("##     WAR: " + currentWarFile);
		logViewer.append("\n");
		logViewer.append("##     Module: " + module);
		logViewer.append("\n");
		logViewer.append("##---------------------- ");
		logViewer.append("\n");
		
	}
	
	protected void logFinish() {
		logViewer.append("\n\n");
		logViewer.append("##-------FINISHED------- ");
		logViewer.append("\n");
	}
}

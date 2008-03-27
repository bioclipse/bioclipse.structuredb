package net.bioclipse.structuredb.dialogs;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class CreateStructureDatabaseDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Label NameLabel;
	private Text nameText;
	private Button cancelButton;
	private Button okButton;
	private String name;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			CreateStructureDatabaseDialog inst = new CreateStructureDatabaseDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CreateStructureDatabaseDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			FormLayout dialogShellLayout = new FormLayout();
			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(254, 134);
			dialogShell.setText("Create new Structure Database");
			{
				cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				cancelButton.setText("Cancel");
				FormData cancelButtonLData = new FormData();
				cancelButtonLData.width = 55;
				cancelButtonLData.height = 29;
				cancelButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
				cancelButtonLData.right =  new FormAttachment(1000, 1000, -63);
				cancelButton.setLayoutData(cancelButtonLData);
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						dialogShell.close();
					}
				});
			}
			{
				okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				okButton.setText("OK");
				FormData okButtonLData = new FormData();
				okButtonLData.width = 45;
				okButtonLData.height = 29;
				okButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
				okButtonLData.right =  new FormAttachment(1000, 1000, -12);
				okButton.setLayoutData(okButtonLData);
				okButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						name = nameText.getText();
						dialogShell.close();
					}
				});
			}
			{
				nameText = new Text(dialogShell, SWT.NONE);
				FormData nameTextLData = new FormData();
				nameTextLData.width = 161;
				nameTextLData.height = 17;
				nameTextLData.left =  new FormAttachment(0, 1000, 67);
				nameTextLData.top =  new FormAttachment(0, 1000, 12);
				nameText.setLayoutData(nameTextLData);
			}
			{
				NameLabel = new Label(dialogShell, SWT.NONE);
				NameLabel.setText("Name:");
				FormData NameLabelLData = new FormData();
				NameLabelLData.width = 43;
				NameLabelLData.height = 17;
				NameLabelLData.left =  new FormAttachment(0, 1000, 12);
				NameLabelLData.top =  new FormAttachment(0, 1000, 12);
				NameLabel.setLayoutData(NameLabelLData);
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}
	
}

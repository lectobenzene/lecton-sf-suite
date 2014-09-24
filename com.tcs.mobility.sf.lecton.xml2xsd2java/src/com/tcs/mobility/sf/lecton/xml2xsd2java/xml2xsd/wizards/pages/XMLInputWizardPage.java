package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.wizards.pages;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.tcs.mobility.sf.lecton.bttsource.errorhandler.IMessageInjector;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.sf.lecton.bttsource.parsers.context.DataModelParser;
import com.tcs.mobility.sf.lecton.xml2xsd2java.Activator;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.contentprovider.ContextContentProvider;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.labelprovider.ContextParseLabelProvider;

public class XMLInputWizardPage extends WizardPage implements IMessageInjector {
	private static final int TYPE_RESPONSE = 1;
	private static final int TYPE_REQUEST = 0;
	private Composite requestComposite;
	private Composite responseComposite;
	private Label lblRequest;
	private Label lblResponse;
	private TabFolder requestTabFolder;
	private TabItem tbtmRequestSource;
	private TabItem tbtmRequestDesign;
	private Composite composite_2;
	private Text txtRequestInput;
	private Composite composite_3;
	private TreeViewer requestTreeViewer;
	private Label label;
	private TabFolder responseTabFolder;
	private TabItem tbtmSource;
	private TabItem tbtmDesign;
	private Composite composite_4;
	private Text txtResponseInput;
	private Composite composite_5;
	private TreeViewer responseTreeViewer;

	private DataModelParser parser;
	protected boolean isRequestSuccessful;
	protected boolean isResponseSuccessful;

	private KeyedCollectionModel requestKColl;
	private KeyedCollectionModel responseKColl;

	public XMLInputWizardPage() {
		super("XMLInputWizardPage");
		setPageComplete(false);
		setDescription("Provide the Request and Response of the web service and let the tool do the Magic");
		setTitle("Request/Response Input");
		initialize();
	}

	private void initialize() {
		parser = new DataModelParser();
		parser.setMessageInjectorListener(this);

	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(3, false);
		container.setLayout(gl_container);

		requestComposite = new Composite(container, SWT.NONE);
		requestComposite.setLayout(new GridLayout(1, false));
		requestComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblRequest = new Label(requestComposite, SWT.NONE);
		lblRequest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblRequest.setText("REQUEST");

		requestTabFolder = new TabFolder(requestComposite, SWT.BOTTOM);
		requestTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tbtmRequestSource = new TabItem(requestTabFolder, SWT.NONE);
		tbtmRequestSource.setText("Input");
		tbtmRequestSource.setImage(Activator.getDefault().createImage("icons/prop_ps.gif"));

		composite_2 = new Composite(requestTabFolder, SWT.NONE);
		tbtmRequestSource.setControl(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		txtRequestInput = new Text(composite_2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		txtRequestInput.setText("<data>\n\t<AccountList>\n\t\t<Account>\n\t\t\t<first-name>String</first-name>\n\t\t\t<last-name>String</last-name>\n\t\t\t<Name-List>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-one>String</name-one>\n\t\t\t\t\t<name-two>String</name-two>\n\t\t\t\t</Names>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-one>String</name-one>\n\t\t\t\t\t<name-two>String</name-two>\n\t\t\t\t</Names>\n\t\t\t</Name-List>\n\t\t</Account>\n\t\t<Account>\n\t\t\t<first-name>String</first-name>\n\t\t\t<last-name>String</last-name>\n\t\t\t<Name-List>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-one>String</name-one>\n\t\t\t\t\t<name-two>String</name-two>\n\t\t\t\t</Names>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-one>String</name-one>\n\t\t\t\t\t<name-two>String</name-two>\n\t\t\t\t</Names>\n\t\t\t</Name-List>\n\t\t</Account>\n\t</AccountList>\n\t<end-credits>int</end-credits>\n\t<some-credits>int</some-credits>\n</data>");

		tbtmRequestDesign = new TabItem(requestTabFolder, SWT.NONE);
		tbtmRequestDesign.setText("Validate");
		tbtmRequestDesign.setImage(Activator.getDefault().createImage("icons/filenav_nav.gif"));

		composite_3 = new Composite(requestTabFolder, SWT.NONE);
		tbtmRequestDesign.setControl(composite_3);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));

		requestTreeViewer = new TreeViewer(composite_3, SWT.BORDER);
		requestTreeViewer.setContentProvider(new ContextContentProvider());
		requestTreeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new ContextParseLabelProvider(parent.getDisplay())));

		requestTabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isRequestSuccessful = parseDesignData(e, txtRequestInput, requestTreeViewer,
						TYPE_REQUEST);
				updateStatus();
			}
		});

		label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1));

		responseComposite = new Composite(container, SWT.NONE);
		responseComposite.setLayout(new GridLayout(1, false));
		responseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblResponse = new Label(responseComposite, SWT.NONE);
		lblResponse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblResponse.setText("RESPONSE");

		responseTabFolder = new TabFolder(responseComposite, SWT.BOTTOM);
		responseTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		responseTabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isResponseSuccessful = parseDesignData(e, txtResponseInput, responseTreeViewer,
						TYPE_RESPONSE);
				updateStatus();
			}
		});
		tbtmSource = new TabItem(responseTabFolder, SWT.NONE);
		tbtmSource.setText("Input");
		tbtmSource.setImage(Activator.getDefault().createImage("icons/prop_ps.gif"));

		composite_4 = new Composite(responseTabFolder, SWT.NONE);
		tbtmSource.setControl(composite_4);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		txtResponseInput = new Text(composite_4, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		txtResponseInput.setText("<data>\n\t<AccountList>\n\t\t<Account>\n\t\t\t<any-name>String</any-name>\n\t\t\t<some-name>String</some-name>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-three>String</name-three>\n\t\t\t\t\t<name-four>String</name-four>\n\t\t\t\t</Names>\n\t\t</Account>\n\t\t<Account>\n\t\t\t<any-name>String</any-name>\n\t\t\t<some-name>String</some-name>\n\t\t\t\t<Names>\n\t\t\t\t\t<name-three>String</name-three>\n\t\t\t\t\t<name-four>String</name-four>\n\t\t\t\t</Names>\n\t\t</Account>\n\t</AccountList>\n\t<end-credits>int</end-credits>\n\t<some-credits>int</some-credits>\n</data>");

		tbtmDesign = new TabItem(responseTabFolder, SWT.NONE);
		tbtmDesign.setImage(Activator.getDefault().createImage("icons/filenav_nav.gif"));
		tbtmDesign.setText("Validate");

		composite_5 = new Composite(responseTabFolder, SWT.NONE);
		tbtmDesign.setControl(composite_5);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));

		responseTreeViewer = new TreeViewer(composite_5, SWT.BORDER);
		responseTreeViewer.setContentProvider(new ContextContentProvider());
		responseTreeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new ContextParseLabelProvider(parent.getDisplay())));
		
		try {
			setContentProposal();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	private void setContentProposal() throws ParseException {
		char[] autoActivationChars = new char[] {'>'};
		KeyStroke keyStroke = KeyStroke.getInstance("Ctrl+Space");
		String[] proposals = new String[]{"string","decimal","int"};
		TextContentAdapter controlContentAdapter = new TextContentAdapter();
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(proposals);
		ContentProposalAdapter reqAdapter = new ContentProposalAdapter(txtRequestInput, controlContentAdapter, proposalProvider, keyStroke, autoActivationChars);
		ContentProposalAdapter resAdapter = new ContentProposalAdapter(txtResponseInput, controlContentAdapter, proposalProvider, keyStroke, autoActivationChars);
	}

	protected void updateStatus() {
		if (!(isRequestSuccessful && isResponseSuccessful)) {
			setErrorMessage("Provide proper Request/Response");
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	public Text getTxtResponseInput() {
		return txtResponseInput;
	}

	public Text getTxtRequestInput() {
		return txtRequestInput;
	}

	@Override
	public boolean showConsoleMessage(String message) {
		return false;
	}

	@Override
	public boolean showErrorMessage(String message) {
		MessageDialog.openError(getShell(), "Error", message);
		return false;
	}

	private boolean parseDesignData(SelectionEvent e, Text txtInput, TreeViewer inputTreeViewer,
			int type) {
		String tabName = ((TabItem) e.item).getText();
		if ("Validate".equals(tabName) && txtInput.getText().length() > 0) {
			String xmlContent = txtInput.getText();
			KeyedCollectionModel rootElement = parser.getRootNodeFromFile(xmlContent);
			if (type == TYPE_REQUEST) {
				requestKColl = rootElement;
			} else if (type == TYPE_RESPONSE) {
				responseKColl = rootElement;
			}
			if (rootElement != null) {
				inputTreeViewer.setInput(rootElement.getChildren().toArray());
				return true;
			}
		}
		return false;
	}

	public KeyedCollectionModel getRequestKColl() {
		return requestKColl;
	}

	public KeyedCollectionModel getResponseKColl() {
		return responseKColl;
	}
}

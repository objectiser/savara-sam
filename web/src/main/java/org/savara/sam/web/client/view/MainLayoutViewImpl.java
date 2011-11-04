/**
 * 
 */
package org.savara.sam.web.client.view;

import org.savara.sam.web.client.presenter.MainLayoutPresenter.MainLayoutView;
import org.savara.sam.web.shared.dto.Statistic;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author Jeff Yu
 * @date Nov 4, 2011
 */
public class MainLayoutViewImpl extends ViewImpl implements MainLayoutView{
	
	private VLayout panel;
	
	private Statistic[] data;
	
	private VLayout txnRatioPanel = new VLayout();
	
	private PortalLayout portal;
	
	@Inject
	public MainLayoutViewImpl() {
		panel = new VLayout();
		panel.setWidth("100%");
		panel.setAlign(Alignment.CENTER);
		panel.setPadding(5);
		
		addHeaderLayout();
		
		HLayout body = new HLayout();
		body.setWidth("100%");
		body.setPadding(3);
		body.setHeight(700);
		panel.addMember(body);
				
		addSectionStack(body);
		
		VLayout main = new VLayout(15);
		main.setMargin(10);
		body.addMember(main);
		
		final int portalColumn = 3;
		portal = new PortalLayout(portalColumn);
		portal.setWidth100();
		portal.setHeight100();
		portal.setCanAcceptDrop(true);
		portal.setShowColumnMenus(false);
		portal.setBorder("0px");
		portal.setColumnBorder("0px");
			
		setPortalMenus(main);
		
        main.addMember(portal);
        
        Portlet txnRatio = createPortlet("Txn Ratio");       
        portal.addPortlet(txnRatio, 0, 0);
        txnRatio.addChild(txnRatioPanel);
        
        
        Runnable onloadCallback = new Runnable() {
			public void run() {
				PieChart pc = createTxnRatioChart(data);
				panel.addChild(pc);
			}        	
        };
                
        VisualizationUtils.loadVisualizationApi(onloadCallback, PieChart.PACKAGE);
        
        // create portlets...  
        for (int i = 1; i < 6; i++) {  
            Portlet portlet = createPortlet("AQ Chart");
        	
            Label label = new Label();  
            label.setAlign(Alignment.CENTER);  
            label.setLayoutAlign(VerticalAlignment.CENTER);  
            label.setContents("Portlet contents");  
            portlet.addItem(label);
            
            portal.addPortlet(portlet, (i % portalColumn), (i/portalColumn));
        } 
		
		addFooterLayout();
		panel.draw();
	}
	
	private PieChart createTxnRatioChart(Statistic[] values) {
		DataTable dt = DataTable.create();
		dt.addColumn(ColumnType.STRING, "transaction type");
		dt.addColumn(ColumnType.NUMBER, "percentage");
		dt.addRows(values.length);
		
		for (int i = 0; i < values.length; i++) {
			Statistic statistic = values[i];
			dt.setValue(i, 0, statistic.getName());
			dt.setValue(i, 1, statistic.getValue());
		}
		
		Options options = Options.create();
		options.setWidth(120);
		options.setHeight(100);
		options.setTitle("Txn Ratio");

		PieChart pc = new PieChart(dt, options);
		return pc;
	}
	
	private Portlet createPortlet(String title) {
        Portlet portlet = new Portlet();  
        portlet.setTitle(title);  
        portlet.setShowShadow(false);
        portlet.setDragAppearance(DragAppearance.OUTLINE);
        portlet.setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL,
        		new HeaderControl(HeaderControl.SETTINGS), HeaderControls.CLOSE_BUTTON);
        portlet.setVPolicy(LayoutPolicy.NONE);
        portlet.setOverflow(Overflow.VISIBLE);
        portlet.setAnimateMinimize(true);
        
        
        portlet.setWidth(300);
        portlet.setHeight(300);
        portlet.setCanDragResize(false);
        
        return portlet;
	}


	private void setPortalMenus(VLayout main) {
		final DynamicForm form = new DynamicForm();  
        form.setAutoWidth();  
        form.setNumCols(1);  
		
        ButtonItem addColumn = new ButtonItem("addAQ", "Add AQ Chart");  
        addColumn.setAutoFit(true);  
        addColumn.setStartRow(false);  
        addColumn.setEndRow(false);  
        
        form.setItems(addColumn);
        
        main.addMember(form);
	}


	private void addSectionStack(HLayout body) {
		final SectionStack  linkStack = new SectionStack();
		linkStack.setVisibilityMode(VisibilityMode.MULTIPLE);
		linkStack.setCanResizeSections(false);
		linkStack.setWidth(200);
		linkStack.setHeight(400);
		
		SectionStackSection dashboard = new SectionStackSection("Dashboard");
		dashboard.setCanCollapse(true);
		dashboard.setExpanded(true);
		HTMLFlow flow = new HTMLFlow();
		flow.setContents("Active Query");
		flow.setPadding(5);
		dashboard.addItem(flow);
		
		linkStack.addSection(dashboard);
		linkStack.draw();
		
		body.addMember(linkStack);
	}


	private void addHeaderLayout() {
		Label headerLabel = new Label();
		headerLabel.setContents("Savara SAM :: Header ");
		headerLabel.setSize("100%", "85");
		headerLabel.setAlign(Alignment.CENTER);
		headerLabel.setBorder("1px solid #808080");		
		panel.addMember(headerLabel);
	}


	private void addFooterLayout() {
		Label footerLabel = new Label();
		footerLabel.setContents("Savara SAM :: Footer ");
		footerLabel.setSize("100%", "30");
		footerLabel.setAlign(Alignment.CENTER);
		footerLabel.setBorder("1px solid #808080");
		
		panel.addMember(footerLabel);
	}
	
	
	public Widget asWidget() {
		return panel;
	}


	public void setStatistics(Statistic[] value) {
		this.data = value;
	}

}

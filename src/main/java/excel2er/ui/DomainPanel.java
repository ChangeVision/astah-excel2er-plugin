package excel2er.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import excel2er.Messages;

public class DomainPanel extends JPanel {

	private static final long serialVersionUID = 3038249479574243112L;
	static final String NAME = "domainpanel";
	private StartRow startRow;
	private ItemCol logicalCol;
	private ItemCol physicalCol;
    private ItemCol alias1Col;
    private ItemCol alias2Col;
	private ItemCol dataTypeCol;
    private ItemCol lengthAndPrecisionCol;
    private ItemCol notNullCol;
    private ItemCol parentDomainCol;
	private ItemCol definitionCol;

	public DomainPanel() {
		setName(NAME);
		GridBagLayout manager = new GridBagLayout();
		setLayout(manager);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createTitledBorder(Messages
				.getMessage("explain_domain")));
		createContents();
		
		setDefaultValueForAstahEREntityDocument();
		
		setVisible(true);
	}

	private void setDefaultValueForAstahEREntityDocument() {
		startRow.setText(StringUtils.defaultString(ConfigUtil.getDomainStartRow(),"4"));
		logicalCol.setText(StringUtils.defaultString(ConfigUtil.getDomainLogicalCol(),"B"));
		physicalCol.setText(StringUtils.defaultString(ConfigUtil.getDomainPhysicalCol(),"G"));
        alias1Col.setText(StringUtils.defaultString(ConfigUtil.getAlias1Col(), "L"));
        alias2Col.setText(StringUtils.defaultString(ConfigUtil.getAlias2Col(), "Q"));
		dataTypeCol.setText(StringUtils.defaultString(ConfigUtil.getDomainDataTypeCol(),"V"));
        lengthAndPrecisionCol.setText(StringUtils.defaultString(
                ConfigUtil.getLengthAndPrecisionCol(), "Z"));
        notNullCol.setText(StringUtils.defaultString(ConfigUtil.getNotNullCol(), "AD"));
        parentDomainCol.setText(StringUtils.defaultString(ConfigUtil.getDomainParentDomainCol(),
                "AE"));
		definitionCol.setText(StringUtils.defaultString(ConfigUtil.getDomainDefinitionCol(),"AJ"));
	}
	
	public void saveLatestSetting() {
		ConfigUtil.saveDomainStartRow(getStartRow());
		ConfigUtil.saveDomainLogicalCol(getLogicalCol());
		ConfigUtil.saveDomainPhysicalCol(getPhysicalCol());
        ConfigUtil.saveAlias1Col(getAlias1Col());
        ConfigUtil.saveAlias2Col(getAlias2Col());
		ConfigUtil.saveDomainDataTypeCol(getDataTypeCol());
        ConfigUtil.saveLengthAndPrecisionCol(getLengthAndPrecisionCol());
        ConfigUtil.saveNotNullCol(getNotNullCol());
        ConfigUtil.saveDomainParentDomainCol(getParentDomainCol());
		ConfigUtil.saveDomainDefinitionCol(getDefinitionCol());
	}

	
	private void createContents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 0, 2);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = GridBagConstraints.RELATIVE;

		gbc.weightx = 0.0d;
		add(new Empty(), gbc);
		gbc.weightx = 0.2d;
		add(new JLabel(Messages.getMessage("start_row")), gbc);
		gbc.weightx = 0.6d;
		add(startRow = new StartRow(), gbc);
		gbc.weightx = 0.2d;
		add(new Empty(), gbc);

		gbc.gridy = 3;
		gbc.weightx = 0.2d;
		add(new Empty(), gbc);
		gbc.weightx = 0.6d;
		add(new JLabel(Messages.getMessage("item_name")), gbc);
		gbc.weightx = 0.0d;
		add(new JLabel(Messages.getMessage("col")), gbc);
		gbc.weightx = 0.2d;
		add(new Empty(), gbc);

		gbc.gridy = 4;
		add(new Empty(), gbc);
		GridBagConstraints separatorConstraint = new GridBagConstraints();
		separatorConstraint.gridx = GridBagConstraints.RELATIVE;
		separatorConstraint.gridy = 4;
		separatorConstraint.weightx = 0;
		separatorConstraint.fill = GridBagConstraints.HORIZONTAL;
		separatorConstraint.gridwidth = GridBagConstraints.RELATIVE;
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		sep.setBorder(BorderFactory.createEtchedBorder());
		add(sep, separatorConstraint);
		add(new Empty(), gbc);

		gbc.gridy =5;
		add(new Empty(), gbc);
		add(new JLabel(Messages.getMessage(ItemCol.LOGICAL)), gbc);
		add(logicalCol = new ItemCol(ItemCol.LOGICAL), gbc);
		gbc.gridy = 6;
		add(new Empty(), gbc);
		add(new JLabel(Messages.getMessage(ItemCol.PHYSICAL)), gbc);
		add(physicalCol = new ItemCol(ItemCol.PHYSICAL), gbc);
		gbc.gridy = 7;
        add(new Empty(), gbc);
        add(new JLabel(Messages.getMessage(ItemCol.ALIAS1)), gbc);
        add(alias1Col = new ItemCol(ItemCol.ALIAS1), gbc);
        gbc.gridy = 8;
        add(new Empty(), gbc);
        add(new JLabel(Messages.getMessage(ItemCol.ALIAS2)), gbc);
        add(alias2Col = new ItemCol(ItemCol.ALIAS2), gbc);
        gbc.gridy = 9;
		add(new Empty(), gbc);
		add(new JLabel(Messages.getMessage(ItemCol.DATATYPE)), gbc);
		add(dataTypeCol = new ItemCol(ItemCol.DATATYPE), gbc);
        gbc.gridy = 10;
		add(new Empty(), gbc);
        add(new JLabel(Messages.getMessage(ItemCol.LENGTH_AND_PRECISION)), gbc);
        add(lengthAndPrecisionCol = new ItemCol(ItemCol.LENGTH_AND_PRECISION), gbc);
        gbc.gridy = 11;
        add(new Empty(), gbc);
        add(new JLabel(Messages.getMessage(ItemCol.NOTNULL)), gbc);
        add(notNullCol = new ItemCol(ItemCol.NOTNULL), gbc);
        gbc.gridy = 12;
        add(new Empty(), gbc);
        add(new JLabel(Messages.getMessage(ItemCol.PARENT_DOMAIN)), gbc);
        add(parentDomainCol = new ItemCol(ItemCol.PARENT_DOMAIN), gbc);
        gbc.gridy = 13;
        add(new Empty(), gbc);
		add(new JLabel(Messages.getMessage(ItemCol.DEFINITION)), gbc);
		add(definitionCol = new ItemCol(ItemCol.DEFINITION), gbc);

	}

	class StartRow extends JTextField {
		private static final long serialVersionUID = 1L;
		static final String NAME = "start_row";

		public StartRow() {
			setName(NAME);
			setColumns(2);
		}
	}

	class ItemCol extends JTextField {

		private static final long serialVersionUID = 1L;
		static final String LOGICAL = "item_logical_domain";
		static final String PHYSICAL = "item_physical_domain";
        static final String ALIAS1 = "item_alias1_domain";
        static final String ALIAS2 = "item_alias2_domain";
		static final String DATATYPE = "item_datatype_domain";
        static final String LENGTH_AND_PRECISION = "item_length_and_precision_domain";
        static final String NOTNULL = "item_not_null_domain";
		static final String PARENT_DOMAIN = "item_parent_domain_domain";
		static final String DEFINITION = "item_definition_domain";
		
		public ItemCol(String key) {
			setName(key);
			setColumns(2);
		}
	}

	public String getStartRow() {
		return startRow.getText();
	}

	public String getLogicalCol() {
		return logicalCol.getText();
	}

	public String getPhysicalCol() {
		return physicalCol.getText();
	}

    public String getAlias1Col() {
        return alias1Col.getText();
    }

    public String getAlias2Col() {
        return alias2Col.getText();
    }

	public String getDataTypeCol() {
		return dataTypeCol.getText();
	}

    public String getLengthAndPrecisionCol() {
        return lengthAndPrecisionCol.getText();
    }

    public String getNotNullCol() {
        return notNullCol.getText();
    }

    public String getParentDomainCol() {
        return parentDomainCol.getText();
    }

	public String getDefinitionCol() {
		return definitionCol.getText();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(DomainPanel.class.getName());
		JDialog dialog = new JDialog(frame);
		DomainPanel p = new DomainPanel();
		dialog.add(p);
		frame.setSize(800, 600);
		dialog.pack();
		dialog.setVisible(true);
	}
}

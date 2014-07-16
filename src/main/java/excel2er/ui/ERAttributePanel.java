package excel2er.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import excel2er.Messages;

public class ERAttributePanel extends JPanel {

	private static final long serialVersionUID = 2225522938156327518L;
	static final String NAME = "entitypanel";
	private StartRow startRow;
	private ItemCol logicalCol;
	private ItemCol physicalCol;
	private ItemCol primaryKeyCol;
	private ItemCol notNullCol;
	private ItemCol defaultValueCol;
	private ItemCol dataTypeCol;
	private ItemCol lengthCol;
	private ItemCol definitionCol;

	public ERAttributePanel() {
		setName(NAME);
		GridBagLayout manager = new GridBagLayout();
		setLayout(manager);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createTitledBorder(Messages.getMessage("explain_attribute")));
		createContents();
		setVisible(true);
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
		add(new Empty(),gbc);
		gbc.weightx = 0.2d;
		add(new JLabel(Messages.getMessage("start_row")), gbc);
		gbc.weightx = 0.6d;
		add(startRow = new StartRow(), gbc);
		gbc.weightx = 0.2d;
		add(new Empty(),gbc);
		
		
		gbc.gridy = 3;
		gbc.weightx = 0.2d;
		add(new Empty(),gbc);
		gbc.weightx = 0.6d;
		add(new JLabel(Messages.getMessage("item_name")), gbc);
		gbc.weightx = 0.0d;
		add(new JLabel(Messages.getMessage("col")), gbc);
		gbc.weightx = 0.2d;
		add(new Empty(),gbc);
		
		gbc.gridy = 4;
		add(new Empty(),gbc);
		GridBagConstraints separatorConstraint = new GridBagConstraints();
		separatorConstraint.gridx = GridBagConstraints.RELATIVE;
		separatorConstraint.gridy = 4;
        separatorConstraint.weightx = 0;
        separatorConstraint.fill = GridBagConstraints.HORIZONTAL;
        separatorConstraint.gridwidth = GridBagConstraints.RELATIVE;
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		sep.setBorder(BorderFactory.createEtchedBorder());
		add(sep, separatorConstraint);
		add(new Empty(),gbc);

		gbc.gridy = 5;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.LOGICAL)), gbc);
		add(logicalCol = new ItemCol(ItemCol.LOGICAL),gbc);
		gbc.weightx = 0.2d;
		gbc.gridy = 6;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.PHYSICAL)), gbc);
		add(physicalCol = new ItemCol(ItemCol.PHYSICAL),gbc);
		gbc.gridy = 7;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.PRIMARYKEY)), gbc);
		add(primaryKeyCol = new ItemCol(ItemCol.PRIMARYKEY),gbc);
		gbc.gridy = 8;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.NOTNULL)), gbc);
		add(notNullCol = new ItemCol(ItemCol.NOTNULL),gbc);
		gbc.gridy = 9;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.DEFAULT_VALUE)), gbc);
		add(defaultValueCol = new ItemCol(ItemCol.DEFAULT_VALUE),gbc);
		gbc.gridy = 10;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.DATATYPE)), gbc);
		add(dataTypeCol = new ItemCol(ItemCol.DATATYPE),gbc);
		gbc.gridy = 11;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.LENGTH)), gbc);
		add(lengthCol = new ItemCol(ItemCol.LENGTH),gbc);
		gbc.gridy = 12;
		add(new Empty(),gbc);
		add(new JLabel(Messages.getMessage(ItemCol.DEFINITION)), gbc);
		add(definitionCol = new ItemCol(ItemCol.DEFINITION),gbc);

	}
	
	class StartRow extends JTextField{
		private static final long serialVersionUID = 1L;
		static final String NAME = "start_row";

		public StartRow() {
			setName(NAME);
			setColumns(2);
			setText("1");
		}
	}
	
	class ItemCol extends JTextField{
		
		private static final long serialVersionUID = 1L;
		static final String LOGICAL = "item_logical";
		static final String PHYSICAL = "item_physical";
		static final String PRIMARYKEY = "item_primarykey";
		static final String NOTNULL = "item_notnull";
		static final String DEFAULT_VALUE = "item_defaultvalue";
		static final String DATATYPE = "item_datatype";
		static final String LENGTH = "item_length";
		static final String DEFINITION = "item_definition";

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


	public String getPrimaryKeyCol() {
		return primaryKeyCol.getText();
	}


	public String getNotNullCol() {
		return notNullCol.getText();
	}


	public String getDefaultValueCol() {
		return defaultValueCol.getText();
	}


	public String getDataTypeCol() {
		return dataTypeCol.getText();
	}


	public String getLengthCol() {
		return lengthCol.getText();
	}


	public String getDefinitionCol() {
		return definitionCol.getText();
	}

}

package excel2er.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.math.NumberUtils;

import excel2er.Messages;

public class EntityPanel extends JPanel {

	private static final long serialVersionUID = 2225522938156327518L;
	static final String NAME = "entitypanel";
	public UseSheetNameButton useSheetNameButton;
	public AdvanceSettingButton advanceSettingButton;
	private Component target;
	private JDialog dialog;
	private AdvanceElementRowCol logicalrow;
	private AdvanceElementRowCol logicalcol;
	private AdvanceElementRowCol physicalrow;
	private AdvanceElementRowCol physicalcol;

	public EntityPanel(JDialog dialog) {
		this.dialog = dialog;
		setName(NAME);
		GridBagLayout manager = new GridBagLayout();
		setLayout(manager);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createTitledBorder(Messages.getMessage("explain_entity")));
		createContents();
		createAdvanceTab();
		setVisible(true);
	}

	private void createAdvanceTab(){
		JPanel advance = new JPanel();
		GridBagLayout manager = new GridBagLayout();
		advance.setLayout(manager);
		
		Insets def = new Insets(2, 2, 0, 2);
		Insets label = new Insets(2,2,0,14);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = label;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = GridBagConstraints.RELATIVE;
		
		advance.add(new JLabel(Messages.getMessage("entity.logicalname")), gbc);
		gbc.insets = def;
		advance.add(new JLabel(Messages.getMessage("row")), gbc);
		advance.add(logicalrow = new AdvanceElementRowCol(AdvanceElementRowCol.LOGICAL_ROW), gbc);
		advance.add(new JLabel(Messages.getMessage("col")), gbc);
		advance.add(logicalcol = new AdvanceElementRowCol(AdvanceElementRowCol.LOGICAL_COL), gbc);

		gbc.gridy = 1;
		advance.add(new JLabel(Messages.getMessage("entity.physicalname")), gbc);
		advance.add(new JLabel(Messages.getMessage("row")), gbc);
		advance.add(physicalrow = new AdvanceElementRowCol(AdvanceElementRowCol.PHYSICAL_ROW), gbc);
		advance.add(new JLabel(Messages.getMessage("col")), gbc);
		advance.add(physicalcol = new AdvanceElementRowCol(AdvanceElementRowCol.PHYSICAL_COL), gbc);

		setTarget(advance);
	}
	
	private void createContents() {
		new SelectionArea();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new Empty(), gbc);
		gbc.gridx = 1;
		add(useSheetNameButton, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new Empty(), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0d;
		add(advanceSettingButton, gbc);
	}

	public boolean isAllSelected() {
		return useSheetNameButton.isSelected();
	}

	class AdvanceElementRowCol extends JTextField{
		
		private static final long serialVersionUID = 1L;
		static final String PHYSICAL_ROW = "physical_row";
		static final String PHYSICAL_COL = "physical_col";
		static final String LOGICAL_ROW = "logical_row";
		static final String LOGICAL_COL = "logical_col";
		
		AdvanceElementRowCol(String name){
			setName(name);
			setColumns(2);
		}
		
	}
	
	class SelectionArea extends ButtonGroup {

		private static final long serialVersionUID = -4630277115815049856L;

		private SelectionArea() {
			add(useSheetNameButton = new UseSheetNameButton());
			add(advanceSettingButton = new AdvanceSettingButton());
		}
	}

	class AdvanceSettingButton extends JRadioButton {
		private static final long serialVersionUID = 2270864414354703154L;
		static final String NAME = "advance_setting";

		private AdvanceSettingButton() {
			super();
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (AdvanceSettingButton.this.isSelected()) {
						if (target != null)
							target.setEnabled(true);
					}
				}
			});
		}
	}

	class UseSheetNameButton extends JRadioButton {
		private static final long serialVersionUID = 4037864631476683014L;
		static final String NAME = "use_sheetname";

		private UseSheetNameButton() {
			super();
			setSelected(true);
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (target != null) {
						if (UseSheetNameButton.this.isSelected()) {
							target.setVisible(false);
							dialog.pack();
						} else {
							target.setVisible(true);
							dialog.pack();
						}
					}
				}
			});
		}
	}

	public void setTarget(Component target) {
		this.target = target;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 6;
		gbc.gridheight = 2;
		gbc.insets = new Insets(2, 10, 0, 10);
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(target, gbc);
		if (useSheetNameButton.isSelected()) {
			target.setVisible(false);
			dialog.pack();
		} else {
			target.setVisible(true);
			dialog.pack();
		}
	}
	
	public Boolean isUseSheetName() {
		return useSheetNameButton.isSelected();
	}
	
	public Boolean isAdvanceSetting() {
		return advanceSettingButton.isSelected();
	}
	
	public String getLogicalRow() {
		return logicalrow.getText();
	}

	public String getLogicalCol() {
		return logicalcol.getText();
	}

	public String getPhysicalRow() {
		return physicalrow.getText();
	}

	public String getPhysicalCol() {
		return physicalcol.getText();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(EntityPanel.class.getName());
		JDialog dialog = new JDialog(frame);
		EntityPanel p = new EntityPanel(dialog);
		p.createAdvanceTab();
		dialog.add(p);
		frame.setSize(800, 600);
		dialog.pack();
		dialog.setVisible(true);
	}

	public void checkInputValue() {
		isDigit(getLogicalRow(),Messages.getMessage("entity.logicalname") + " " + Messages.getMessage("row"));
		isDigit(getLogicalCol(),Messages.getMessage("entity.logicalname") + " " + Messages.getMessage("col"));
		isDigit(getPhysicalRow(),Messages.getMessage("entity.physicalname") + " " + Messages.getMessage("row"));
		isDigit(getPhysicalRow(),Messages.getMessage("entity.physicalname") + " " + Messages.getMessage("col"));
	}

	private void isDigit(String value, String message) {
		if(!NumberUtils.isDigits(value))
			throw new NumberFormatException(message);
	}



}

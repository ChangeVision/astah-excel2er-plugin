package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URL;

import javax.swing.JFrame;

import org.apache.commons.lang3.SystemUtils;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IEREntity;
import com.change_vision.jude.api.inf.model.INamedElement;

import excel2er.AstahModelManager;
import excel2er.Messages;

@RunWith(GUITestRunner.class)
public class AcceptanceTest {

	private FrameFixture frameFixture;
	private DialogFixture dialogFixture;
	private ImportDialog target;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		target = new ImportDialog(frame);
		dialogFixture = new DialogFixture(frameFixture.robot, target);
		dialogFixture.moveToFront();
		dialogFixture.show();
		dialogFixture.focus();
	}

	@After
	public void tearDown() throws Exception {
		if (dialogFixture != null) {
			dialogFixture.cleanUp();
		}
		if (frameFixture != null) {
			frameFixture.cleanUp();
		}
	}

	/**
	 * <pre>
	 * - userSheetname for Entity's logicalName 
	 * - following attribute property set... 
	 *   - logical - datatype
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void accept_import_when_set_logical_datatype_attribute_properties_and_usesheetname()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());
		dialogFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).check();
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("9");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("B");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("Q");

		dialogFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("");

		dialogFixture.button(ImportDialog.ImportButton.NAME).click();

		dialogFixture.optionPane().requireVisible();
		dialogFixture.optionPane().requireInformationMessage();

		assertThat(countEREntity(), is(6));

		assertThat(findEREntity("Domain List"), is(notNullValue()));
		assertThat(findEREntity("Entity List"), is(notNullValue()));

		assertThat(findEREntity("Customer"), is(notNullValue()));
		assertThat(findEREntity("Order"), is(notNullValue()));
		assertThat(findEREntity("OrderDetail"), is(notNullValue()));

		IEREntity entity = findEREntity("Product");
		assertThat(entity, is(notNullValue()));

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getNonPrimaryKeys()[0].getLogicalName(),
				is("ProductID"));
		assertThat(entity.getNonPrimaryKeys()[0].getDatatype().getName(),
				is("CHAR"));
	}

	/**
	 * <pre>
	 * - set Entity's logicalName and physicalName
	 * - following attribute property set... 
	 *   - logical - datatype
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void accept_import_when_set_entity_logical_and_physical_properties()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
				.setText("1");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
				.setText("H");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
				.setText("2");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
				.setText("H");
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("9");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("B");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("Q");

		dialogFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("");

		dialogFixture.button(ImportDialog.ImportButton.NAME).click();

		dialogFixture.optionPane().requireVisible();
		dialogFixture.optionPane().requireInformationMessage();

		assertThat(countEREntity(), is(4));

		assertThat(findEREntity("Customer"), is(notNullValue()));
		assertThat(findEREntity("Order"), is(notNullValue()));
		assertThat(findEREntity("OrderDetail"), is(notNullValue()));
		assertThat(findEREntity("Product"), is(notNullValue()));

		IEREntity entity = findEREntity("OrderDetail");
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getNonPrimaryKeys().length, is(5));
		assertThat(entity.getNonPrimaryKeys()[3].getLogicalName(), is("Amount"));
		assertThat(entity.getNonPrimaryKeys()[3].getDatatype().getName(),
				is("INT"));
	}

	/**
	 * <pre>
	 * - set Entity's logicalName and physicalName
	 * - following attribute property set... 
	 *   - all
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void accept_import_when_set_all_properties()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
				.setText("1");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
				.setText("H");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
				.setText("2");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
				.setText("H");
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("9");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("B");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("G");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("L");
		dialogFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("P");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("Q");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("U");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText("Y");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("AC");

		dialogFixture.button(ImportDialog.ImportButton.NAME).click();

		dialogFixture.optionPane().requireVisible();
		dialogFixture.optionPane().requireInformationMessage();

		assertThat(countEREntity(), is(4));

		assertThat(findEREntity("Customer"), is(notNullValue()));
		assertThat(findEREntity("Order"), is(notNullValue()));
		assertThat(findEREntity("OrderDetail"), is(notNullValue()));
		assertThat(findEREntity("Product"), is(notNullValue()));

		IEREntity entity = findEREntity("Product");
		assertThat(entity, is(notNullValue()));
		
		assertThat(entity.getPrimaryKeys().length, is(1));
		assertThat(entity.getPrimaryKeys()[0].getLogicalName(), is("ProductID"));
		assertThat(entity.getPrimaryKeys()[0].getPhysicalName(), is("PRODUCTID"));
		assertThat(entity.getPrimaryKeys()[0].isPrimaryKey(), is(true));
		assertThat(entity.getPrimaryKeys()[0].isNotNull(), is(true));
		assertThat(entity.getPrimaryKeys()[0].getDatatype().getName(), is("CHAR"));
		assertThat(entity.getPrimaryKeys()[0].getLengthPrecision(), is("20"));
		assertThat(entity.getPrimaryKeys()[0].getDefaultValue(), is("init"));
		assertThat(entity.getPrimaryKeys()[0].getDefinition(), is("def"));
		
		assertThat(entity.getNonPrimaryKeys().length, is(3));
		
		//name
		assertThat(entity.getNonPrimaryKeys()[0].getLogicalName(), is("Name"));
		assertThat(entity.getNonPrimaryKeys()[0].getPhysicalName(), is("NAME"));
		assertThat(entity.getNonPrimaryKeys()[0].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[0].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[0].getDatatype().getName(), is("VARCHAR"));
		assertThat(entity.getNonPrimaryKeys()[0].getLengthPrecision(), is("30"));
		assertThat(entity.getNonPrimaryKeys()[0].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[0].getDefinition(), is(""));
		
		//kind
		assertThat(entity.getNonPrimaryKeys()[1].getLogicalName(), is("Kind"));
		assertThat(entity.getNonPrimaryKeys()[1].getPhysicalName(), is("KIND"));
		assertThat(entity.getNonPrimaryKeys()[1].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[1].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[1].getDatatype().getName(), is("VARCHAR"));
		assertThat(entity.getNonPrimaryKeys()[1].getLengthPrecision(), is("30"));
		assertThat(entity.getNonPrimaryKeys()[1].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[1].getDefinition(), is(""));
		
		//Price
		assertThat(entity.getNonPrimaryKeys()[2].getLogicalName(), is("Price"));
		assertThat(entity.getNonPrimaryKeys()[2].getPhysicalName(), is("PRICE"));
		assertThat(entity.getNonPrimaryKeys()[2].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[2].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[2].getDatatype().getName(), is("INT"));
		assertThat(entity.getNonPrimaryKeys()[2].getLengthPrecision(), is(""));
		assertThat(entity.getNonPrimaryKeys()[2].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[2].getDefinition(), is(""));
	}
	

	/**
	 * <pre>
	 * - import from astah er document.
	 * - set Entity's logicalName and physicalName
	 * - following attribute property set... 
	 *   - all
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void accept_import_when_set_default_properties()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());
		
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.button(ImportDialog.ImportButton.NAME).click();

		dialogFixture.optionPane().requireVisible();
		dialogFixture.optionPane().requireInformationMessage();

		assertThat(countEREntity(), is(4));

		assertThat(findEREntity("Customer"), is(notNullValue()));
		assertThat(findEREntity("Order"), is(notNullValue()));
		assertThat(findEREntity("OrderDetail"), is(notNullValue()));
		assertThat(findEREntity("Product"), is(notNullValue()));

		IEREntity entity = findEREntity("Product");
		assertThat(entity, is(notNullValue()));
		
		assertThat(entity.getPrimaryKeys().length, is(1));
		assertThat(entity.getPrimaryKeys()[0].getLogicalName(), is("ProductID"));
		assertThat(entity.getPrimaryKeys()[0].getPhysicalName(), is("PRODUCTID"));
		assertThat(entity.getPrimaryKeys()[0].isPrimaryKey(), is(true));
		assertThat(entity.getPrimaryKeys()[0].isNotNull(), is(true));
		assertThat(entity.getPrimaryKeys()[0].getDatatype().getName(), is("CHAR"));
		assertThat(entity.getPrimaryKeys()[0].getLengthPrecision(), is("20"));
		assertThat(entity.getPrimaryKeys()[0].getDefaultValue(), is("init"));
		assertThat(entity.getPrimaryKeys()[0].getDefinition(), is("def"));
		
		assertThat(entity.getNonPrimaryKeys().length, is(3));
		
		//name
		assertThat(entity.getNonPrimaryKeys()[0].getLogicalName(), is("Name"));
		assertThat(entity.getNonPrimaryKeys()[0].getPhysicalName(), is("NAME"));
		assertThat(entity.getNonPrimaryKeys()[0].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[0].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[0].getDatatype().getName(), is("VARCHAR"));
		assertThat(entity.getNonPrimaryKeys()[0].getLengthPrecision(), is("30"));
		assertThat(entity.getNonPrimaryKeys()[0].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[0].getDefinition(), is(""));
		
		//kind
		assertThat(entity.getNonPrimaryKeys()[1].getLogicalName(), is("Kind"));
		assertThat(entity.getNonPrimaryKeys()[1].getPhysicalName(), is("KIND"));
		assertThat(entity.getNonPrimaryKeys()[1].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[1].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[1].getDatatype().getName(), is("VARCHAR"));
		assertThat(entity.getNonPrimaryKeys()[1].getLengthPrecision(), is("30"));
		assertThat(entity.getNonPrimaryKeys()[1].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[1].getDefinition(), is(""));
		
		//Price
		assertThat(entity.getNonPrimaryKeys()[2].getLogicalName(), is("Price"));
		assertThat(entity.getNonPrimaryKeys()[2].getPhysicalName(), is("PRICE"));
		assertThat(entity.getNonPrimaryKeys()[2].isPrimaryKey(), is(false));
		assertThat(entity.getNonPrimaryKeys()[2].isNotNull(), is(false));
		assertThat(entity.getNonPrimaryKeys()[2].getDatatype().getName(), is("INT"));
		assertThat(entity.getNonPrimaryKeys()[2].getLengthPrecision(), is(""));
		assertThat(entity.getNonPrimaryKeys()[2].getDefaultValue(), is(""));
		assertThat(entity.getNonPrimaryKeys()[2].getDefinition(), is(""));
	}
	
	@Test
	public void validate_necessary_property() throws Exception {
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("");
		
		dialogFixture.button(ImportDialog.ImportButton.NAME).click();
		
		dialogFixture.optionPane().requireErrorMessage();
		
		StringBuilder message = new StringBuilder();
		message.append(Messages.getMessage("error.inputfile_required")).append(SystemUtils.LINE_SEPARATOR);
		message.append(getMessage("error.required","explain_attribute","item_datatype")).append(SystemUtils.LINE_SEPARATOR);
		message.append(getMessage("error.required","explain_attribute","start_row"));
		assertThat(dialogFixture.optionPane().textBox(ImportDialog.DETAIL_TEXT).text(),is(message.toString()));
	}
	
	@Test
	public void should_show_error_dialog_not_set_entity_logical_physical_col_and_row()
			throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				this.getClass().getResource("entityListModel.xls").getFile());
		
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		
		dialogFixture.button(ImportDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		
		StringBuilder sb = new StringBuilder();
		sb.append(getMessage("error.required","explain_entity","entity.logicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(getMessage("error.required","explain_entity","entity.logicalname.col")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(getMessage("error.required","explain_entity","entity.physicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(getMessage("error.required","explain_entity","entity.physicalname.col"));
		
		assertThat(dialogFixture.optionPane().textBox(ImportDialog.DETAIL_TEXT)
				.text(), is(sb.toString()));
		
	}
	
	private String getMessage(String key, String parameter, String subparameter) {
		return Messages.getMessage(key, Messages.getMessage(parameter)
				+ " - " + Messages.getMessage(subparameter));
	}
	
	private URL getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename);
	}

	private int countEREntity() throws Exception {
		INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(IEREntity.class);
		return elements.length;
	}

	private IEREntity findEREntity(String name) throws Exception {
		INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(IEREntity.class, name);
		if (elements.length > 0)
			return (IEREntity) elements[0];

		return null;
	}
}

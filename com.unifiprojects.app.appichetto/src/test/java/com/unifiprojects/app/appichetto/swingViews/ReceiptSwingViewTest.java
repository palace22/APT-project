package com.unifiprojects.app.appichetto.swingViews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controls.ReceiptController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;



@RunWith(GUITestRunner.class)
public class ReceiptSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private ReceiptSwingView receiptSwingView;

	private User addUserToList(String name, String psw) {
		User user = new User(name, psw);
		GuiActionRunner.execute(() -> receiptSwingView.getListUsersModel().addElement(user));
		return user;
	}

	private Item addItemToList(String name, String stringPrice, String stringQuantity, List<User> users) {
		Item item = new Item(name, stringPrice, stringQuantity, users);
		GuiActionRunner.execute(() -> receiptSwingView.getListItemModel().addElement(item));
		return item;

	}

	@Mock
	private ReceiptController receiptController;

	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			receiptSwingView = new ReceiptSwingView();
			receiptSwingView.setReceiptController(receiptController);
			return receiptSwingView;
		});
		window = new FrameFixture(robot(), receiptSwingView);
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("nameBox").requireEnabled();

		window.label(JLabelMatcher.withText("Price"));
		window.textBox("priceBox").requireEnabled();

		window.label(JLabelMatcher.withText("Quantity"));
		window.textBox("quantityBox").requireEnabled();

		// TODO test both scroll panels are present

		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.label("errorMsgLabel").requireText("");
	}

	@Test
	@GUITest
	public void testWhenFormIsNonEmptyThenAddButtonShouldBeEnabledCompilingFirstItemInfoThenUsersList() {
		window.textBox("nameBox").enterText("Sugo");
		window.textBox("priceBox").enterText("2.2");
		window.textBox("quantityBox").enterText("2");
		addUserToList("Pippo", "psw");

		window.list("usersList").selectItem(0);

		window.button(JButtonMatcher.withText("Save")).requireEnabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenFormIsNonEmptyThenAddButtonShouldBeEnabledCompilingFirstUsersListThenItemInfo() {
		addUserToList("Pippo", "psw");
		window.list("usersList").selectItem(0);
		window.textBox("nameBox").enterText("Sugo");
		window.textBox("priceBox").enterText("2.2");
		window.textBox("quantityBox").enterText("2");

		window.button(JButtonMatcher.withText("Save")).requireEnabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenOneOrMoreArgumentAreBlanckThenSaveButtonIsDisabled() {
		JTextComponentFixture nameBox = window.textBox("nameBox");
		JTextComponentFixture priceBox = window.textBox("priceBox");
		JTextComponentFixture quantityBox = window.textBox("quantityBox");

		addUserToList("Pippo", "psw");
		window.list("usersList").selectItem(0);
		nameBox.enterText("Sugo");
		priceBox.enterText("  ");
		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();

		nameBox.setText("");
		priceBox.setText("");
		quantityBox.setText("");

		nameBox.setText(" ");
		priceBox.setText("2.2");
		quantityBox.setText("  ");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();

	}

	@Test
	public void testSaveUpdateAndDeleteButtonsShouldBeEnabledOnlyWhenAnItemIsSelected() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		addItemToList("Sugo", "2.2", "2", users);
		window.list("itemsList").selectItem(0);
		JButtonFixture saveButton = window.button(JButtonMatcher.withText("Save"));
		JButtonFixture updateButton = window.button(JButtonMatcher.withText("Update"));
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
		saveButton.requireEnabled();
		updateButton.requireEnabled();		
		deleteButton.requireEnabled();
	}

	@Test
	public void testShowCurrentItemsListShouldAddItemDescriptionsToTheList() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		Item item1 = new Item("Sugo", "1", "1", users);
		Item item2 = new Item("Pasta", "2", "1", users);
		GuiActionRunner.execute(() -> receiptSwingView.showCurrentItemsList(Arrays.asList(item1, item2)));
		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item1.toString(), item2.toString());
	}

	@Test
	public void testShowErrorMessage() {
		GuiActionRunner.execute(() -> receiptSwingView.showError("Item name is null"));
		window.label("errorMsgLabel").requireText("Item name is null");
	}

	@Test
	public void testItemAddedShouldAddTheItemToTheListAndResetTheErrorLabel() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = new Item("Sugo", "1", "1", users);

		GuiActionRunner.execute(() -> receiptSwingView.itemAdded(item));
		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMsgLabel").requireText(" ");
	}

	@Test
	public void testItemDeletedShouldDeleteTheItemFromTheListAndResetTheErrorLabel() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item1 = new Item("Sugo", "1", "1", users);
		Item item2 = new Item("Pasta", "1", "1", users);

		GuiActionRunner.execute(() -> {
			receiptSwingView.getListItemModel().addElement(item1);
			receiptSwingView.getListItemModel().addElement(item2);
		});

		GuiActionRunner.execute(() -> receiptSwingView.itemDeleted(item1));

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item2.toString());
		window.label("errorMsgLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testWhenAnItemIsSelectedThenFormIsCompiledWithItsArguments() {
		User user = addUserToList("Pippo", "psw");
		User user1 = addUserToList("Pluto", "psw");
		addItemToList("Sugo", "2.2", "2", Arrays.asList(user, user1));
		JTextComponentFixture nameBox = window.textBox("nameBox");
		JTextComponentFixture priceBox = window.textBox("priceBox");
		JTextComponentFixture quantityBox = window.textBox("quantityBox");

		window.list("itemsList").selectItem(0);
		nameBox.requireText("Sugo");
		priceBox.requireText("2.2");
		quantityBox.requireText("2");
		assertTrue(receiptSwingView.getUsersList().getSelectedValuesList().containsAll(Arrays.asList(user, user1)));
	}
	
	@Test
	public void testAddButtonShouldDelegateToReceiptControllerNewItem() {
		User user1 = addUserToList("Pippo", "psw");
		User user2 = addUserToList("Pluto", "psw");

		window.textBox("nameBox").enterText("Sugo");
		window.textBox("priceBox").enterText("2");
		window.textBox("quantityBox").enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);

		window.button(JButtonMatcher.withText("Save")).click();
		verify(receiptController).addItem("Sugo", "2", "2", Arrays.asList(user1, user2));
	}

	@Test
	public void testUpdateButtonShouldDelegateToReceiptControllerUpdateItem() {
		User user1 = addUserToList("Pippo", "psw");
		User user2 = addUserToList("Pluto", "psw");
		
		Item item = addItemToList("Sugo", "1", "1", Arrays.asList(user1,user2));
		Item item1 = addItemToList("Pasta", "1", "1", Arrays.asList(user1,user2));

		window.list("itemsList").selectItem(1);
		
		window.button(JButtonMatcher.withText("Update")).click();
		verify(receiptController).updateItem("Pasta", "1.0", "1", Arrays.asList(user1, user2), receiptSwingView.getListUsersModel().indexOf(item));
	}

	@Test
	public void testDeleteButtonShouldDelegateToReceiptControllerDeleteItem() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = addItemToList("Sugo", "1", "1", users); 

		window.list("itemsList").selectItem(0);
		
		window.button(JButtonMatcher.withText("Delete")).click();
		verify(receiptController).deleteItem(item);
		
	}
	

}
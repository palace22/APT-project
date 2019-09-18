package com.unifiprojects.app.appichetto.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptControllerTest {

	@Spy
	private Receipt receipt;

	@Mock
	private ReceiptView receiptView;

	@InjectMocks
	private ReceiptController receiptController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddItem() {
		String name = "Item";
		String price = "2";
		String quantity = "2";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		Item item = new Item(name, price, quantity, users);
		receiptController.addItem(item);
		
		verify(receipt).addItem(item);
		verify(receiptView).itemAdded(item);
		assertTrue(receipt.getItems().contains(item));
	}

	@Test
	public void testUpadteItemWithWrongIndex() {
		String name = "Item";
		String price = "1";
		String quantity = "1";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = new Item(name, price, quantity, users);

		try {
			receiptController.updateItem(item, 1);
			fail("Illegal index");
		} catch (IllegalIndex e) {
			assertEquals("Index not in list", e.getMessage());
		}
	}

	@Test
	public void testUpadteItem() {
		String name = "Item";
		String price = "1";
		String quantity = "1";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item oldItem = new Item(name, "2", quantity, users);
		Item newItem = new Item(name, price, quantity, users);

		receiptController.addItemToReceipt(oldItem);

		int index = 0;
		receiptController.updateItem(newItem, index);
		
		verify(receipt).updateItem(index, newItem);
		verify(receiptView).itemUpdated(index, newItem);
		assertTrue(receipt.getItems().contains(newItem));
		assertFalse(receipt.getItems().contains(oldItem));
	}

	@Test
	public void testDeleteItem() {
		String name = "Item";
		String price = "1";
		String quantity = "1";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item itemToDelete = new Item(name, price, quantity, users);

		receiptController.addItemToReceipt(itemToDelete);
		
		receiptController.deleteItem(itemToDelete);
		verify(receipt).deteleItem(itemToDelete);
		verify(receiptView).itemDeleted(itemToDelete);
		assertFalse(receipt.getItems().contains(itemToDelete));
	}

}
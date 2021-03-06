package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionCommands;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ReceiptView;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptControllerTest {

	@Mock
	private ReceiptManager receiptManager;

	@Mock
	private ReceiptView receiptView;

	@Mock
	private UserRepository userRepository;

	@Mock
	private TransactionHandler transactionHandler;

	@InjectMocks
	private ReceiptController receiptController;

	@Captor
	private ArgumentCaptor<TransactionCommands> lambdaCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddItem() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = new Item("Item", 2.2, 2, users);

		receiptController.addItem(item);

		verify(receiptManager).addItem(item);
		verify(receiptView).itemAdded(item);
	}

	@Test
	public void testUpadteItemWithWrongIndex() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = new Item("Item", 2.2, 2, users);
		doThrow(IllegalIndex.class).when(receiptManager).updateItem(0, item);
		
		receiptController.updateItem(0, item);
		
		verify(receiptView).showError("Item index not in list");
	}

	@Test
	public void testUpadteItem() {
		String name = "Item";
		double price = 1;
		int quantity = 1;
		int index = 0;
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item newItem = new Item(name, price, quantity, users);

		receiptController.updateItem(index, newItem);

		verify(receiptManager).updateItem(index, newItem);
		verify(receiptView).itemUpdated(index, newItem);
	}

	@Test
	public void testDeleteItem() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item itemToDelete = new Item("Item", 2.2, 2, users);

		receiptController.deleteItem(itemToDelete);

		verify(receiptManager).deleteItem(itemToDelete);
		verify(receiptView).itemDeleted(itemToDelete);
	}

	@Test
	public void testSaveReceiptWhenUncommittableTransactionExceptionIsCatched() {

		doThrow(new UncommittableTransactionException()).when(transactionHandler)
				.doInTransaction(Matchers.any(TransactionCommands.class));

		receiptController.saveReceipt();

		verify(receiptView).showError("Something went wrong while saving receipt.");
	}

	@Test
	public void testSaveReceipt() {

		receiptController.saveReceipt();

		verify(transactionHandler).doInTransaction(lambdaCaptor.capture());

		TransactionCommands receiptManagerCallSaveReceipt = lambdaCaptor.getValue();
		receiptManagerCallSaveReceipt.execute();

		verify(receiptManager).saveReceipt();
		verify(receiptView).showCurrentItemsList(null);
		verify(receiptView).goBack();
	}

	@Test
	public void testGetUserReturnUserListCallingUserRepository() {
		User pippo = new User("pippo", "psw");
		User pluto = new User("pluto", "psw");

		when(userRepository.findAll()).thenReturn(Arrays.asList(pippo, pluto));

		List<User> users = receiptController.getUsers();

		verify(userRepository).findAll();
		assertThat(users).containsExactlyInAnyOrder(pippo, pluto);
	}

	@Test
	public void testUploadReceiptManagerNotifyReceiptViewToUpdateAttributes() {
		Receipt receipt = new Receipt();
		
		receiptController.uploadReceipt(receipt);

		verify(receiptManager).uploadReceipt(receipt);
		verify(receiptView).setDescriptionUploadedReceipt(receipt.getDescription());
		verify(receiptView).showCurrentItemsList(receipt.getItems());
		verify(receiptView).setTimestampUploadedReceipt(receipt.getTimestamp());
	}
	
	@Test
	public void testUpdateSetLoggedUserInReceiptManager() {
		User loggedUser = new User("loggedUser", "password");
		receiptController.setLoggedUser(loggedUser);
		
		receiptController.update();
		
		verify(receiptManager).setBuyer(loggedUser);
	}
	
}

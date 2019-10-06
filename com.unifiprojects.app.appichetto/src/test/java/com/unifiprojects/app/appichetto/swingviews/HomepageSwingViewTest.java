package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.swingviews.utils.ObservableFrame;

public class HomepageSwingViewTest extends AssertJSwingJUnitTestCase {
	
	
	private FrameFixture window;
	@Mock
	private ObservableFrame loginView;
	@Mock
	private ObservableFrame receiptView;
	@Mock
	private ObservableFrame payReceiptsView;
	@Mock
	private ObservableFrame historyView;

	private HomepageSwingView homepageSwingView;
	
	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			homepageSwingView = new HomepageSwingView();

			homepageSwingView.setLoginView(loginView);
			homepageSwingView.setReceiptSwingView(receiptView);
			homepageSwingView.setPayViewReceiptsViewSwing(payReceiptsView);
			homepageSwingView.setHistoryViewSwing(historyView);
			return homepageSwingView;
		});
		window = new FrameFixture(robot(), homepageSwingView.getFrame());
		window.show();
	}


	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.button(JButtonMatcher.withText("Create Receipt")).requireEnabled();
		window.button(JButtonMatcher.withText("Pay Receipt")).requireEnabled();
		window.button(JButtonMatcher.withText("Show History")).requireEnabled();
		window.button(JButtonMatcher.withText("Log Out")).requireEnabled();
	}

	@Test
	public void testCreateReceiptButtonShowOnlyCreateReceiptView() {
		window.button(JButtonMatcher.withText("Create Receipt")).click();
		verify(receiptView).show();
		assertThat(homepageSwingView.getFrame().isActive()).isFalse();
	}

	@Test
	public void testPayDebtButtonShowOnlyPayDebtView() {
		window.button(JButtonMatcher.withText("Pay Receipt")).click();
		verify(payReceiptsView).show();
		assertThat(homepageSwingView.getFrame().isActive()).isFalse();
	}

	@Test
	public void testShowHistoryButtonShowOnlyShowHistoryView() {
		window.button(JButtonMatcher.withText("Show History")).click();
		verify(historyView).show();
		assertThat(homepageSwingView.getFrame().isActive()).isFalse();
	}

	@Test
	public void testLogOutButtonShowOnlyLogInView() {
		window.button(JButtonMatcher.withText("Log Out")).click();
		verify(loginView).show();
		assertThat(homepageSwingView.getFrame().isActive()).isFalse();
	}
}

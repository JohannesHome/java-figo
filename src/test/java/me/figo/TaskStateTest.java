package me.figo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import me.figo.internal.TaskStatusResponse;
import me.figo.internal.TaskTokenResponse;
import me.figo.internal.TokenResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskStateTest {

	private static final String CLIENT_ID = "FIGO_CLIENT_ID";
	private static final String CLIENT_SECRET = "FIGO_CLIENT_SECRET";
	private final String USER = "testuser@test.de";
	private final String PASSWORD = "some_words";
	
	// Bank account infos needed
	private final String ACCOUNT = "demo";
	private final String BANKCODE = "90090042";
	private final String PIN = "demo";
	
	private static String rand = null;
	
	
	private final FigoConnection fc = new FigoConnection(System.getenv(CLIENT_ID), System.getenv(CLIENT_SECRET),
			"https://127.0.0.1/");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SecureRandom random = new SecureRandom();
		rand = new BigInteger(130, random).toString(32); 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void test_01_addUser() throws IOException, FigoException {
		String response = this.fc.addUser("Test", rand+USER, PASSWORD, "de");
		assertTrue(response.length() == 19);
	}

	@Test
	public void test_02_tastStateNotNull() throws FigoException, IOException {
		TokenResponse tokenResponse = this.fc.credentialLogin(rand + USER, PASSWORD);
		assertTrue(tokenResponse.getAccessToken() instanceof String);
		FigoSession figoSession = new FigoSession(tokenResponse.getAccessToken());
		List<String> syncTasks = Arrays.asList("transactions");
		List<String> credentials = Arrays.asList(ACCOUNT, PIN);
		TaskTokenResponse taskTokenResponse = figoSession.setupNewAccount(BANKCODE, "de", credentials, syncTasks, false,
				false);
		TaskStatusResponse taskStatus = figoSession.getTaskState(taskTokenResponse);
		Assert.assertNotNull(taskStatus);
	}	
	
}

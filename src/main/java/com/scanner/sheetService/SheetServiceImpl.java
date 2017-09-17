package com.scanner.sheetService;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SheetServiceImpl implements SheetService {

	private final String APPLICATION_NAME =
			"Google Sheets API Java Quickstart";

	private final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

	private FileDataStoreFactory DATA_STORE_FACTORY;

	private final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();

	private HttpTransport HTTP_TRANSPORT;

	private final List<String> SCOPES =
			Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.DRIVE_FILE, SheetsScopes.SPREADSHEETS);

	private void init() {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private Credential authorize() throws IOException {
		init();

		InputStream in =
				SheetServiceImpl.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
						.setDataStoreFactory(DATA_STORE_FACTORY)
						.setAccessType("offline")
						.build();
		Credential credential = new AuthorizationCodeInstalledApp(
				flow, new LocalServerReceiver()).authorize("user");
		System.out.println(
				"Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}
	private Sheets getSheetsService() throws IOException {
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
	private Sheets createSheetsService() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		Credential credential = authorize();

		return new Sheets.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public String createSheet(Date sentDate, String name, String phone, String email) {
		Spreadsheet requestBody = new Spreadsheet();
		SpreadsheetProperties ex = new SpreadsheetProperties();
		ex.setTitle(name + " - " + sentDate);
		requestBody.setProperties(ex);
		Sheets sheetsService;
		String spreadsheetUrl = null;
		try {
			sheetsService = createSheetsService();
			Sheets.Spreadsheets.Create request = sheetsService.spreadsheets().create(requestBody);
			Spreadsheet response = request.execute();
			String spreadsheetId = response.getSpreadsheetId();
			spreadsheetUrl = response.getSpreadsheetUrl();
			System.out.println(response);
			Sheets service = getSheetsService();
			String range = "A1:C";
			Object[] x = {name, phone, email};
			List<List<Object>> values = Arrays.asList(Arrays.asList(x));
			ValueRange body = new ValueRange()
					.setValues(values);
			service.spreadsheets().values().update(spreadsheetId, range, body)
					.setValueInputOption("RAW")
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return spreadsheetUrl;
	}
}

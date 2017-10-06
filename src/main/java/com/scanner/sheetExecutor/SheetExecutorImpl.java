package com.scanner.sheetExecutor;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
@PropertySource("classpath:application.properties")
public class SheetExecutorImpl implements SheetExecutor {
	private boolean launched;

	@Value("${spreadsheet.url}")
	private String spreadsheetUrl = null;

	private final String APPLICATION_NAME =
			"Google Sheets API Java Quickstart";

	private final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.dir"), "src/main/resources");

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
				SheetExecutorImpl.class.getResourceAsStream("/client_secret.json");
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

	public void appendData(List<List<Object>> userDetailsList) {
		launched = true;
		String spreadsheetId = getSpreadsheetId();
		String range = "A1";

		ValueRange body = new ValueRange()
				.setValues(userDetailsList);

		try {
			Sheets service = getSheetsService();
			service.spreadsheets().values()
					.append(spreadsheetId, range, body)
					.setValueInputOption("RAW")
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		launched = false;
	}

	private String getSpreadsheetId() {
		Pattern pattern = Pattern.compile("/");
		return pattern.split(spreadsheetUrl)[5];
	}

	@Override
	public boolean isLaunched() {
		return launched;
	}

	/*private String createSheet(Spreadsheet requestBody) throws IOException, GeneralSecurityException {
		Sheets sheetsService = createSheetsService();
		Sheets.Spreadsheets.Create request = sheetsService.spreadsheets().create(requestBody);
		Spreadsheet response = request.execute();
		currentSpreadsheetId = response.getSpreadsheetId();

		List<Request> requests = new ArrayList<>();
		List<CellData> cellDataList = new ArrayList<>();
		cellDataList.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue("Имя"))
				.setUserEnteredFormat(new CellFormat()
						.setHorizontalAlignment("CENTER")
						.setTextFormat(new TextFormat()
								.setFontSize(10)
								.setBold(true))));
		cellDataList.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue("Телефон"))
				.setUserEnteredFormat(new CellFormat()
						.setHorizontalAlignment("CENTER")
						.setTextFormat(new TextFormat()
								.setFontSize(10)
								.setBold(true))));
		cellDataList.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue("Email"))
				.setUserEnteredFormat(new CellFormat()
						.setHorizontalAlignment("CENTER")
						.setTextFormat(new TextFormat()
								.setFontSize(10)
								.setBold(true))));
		cellDataList.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue("Дата получения"))
				.setUserEnteredFormat(new CellFormat()
						.setHorizontalAlignment("CENTER")
						.setTextFormat(new TextFormat()
								.setFontSize(10)
								.setBold(true))));

		List<RowData> rowDataList = new ArrayList<>();
		rowDataList.add(new RowData().setValues(cellDataList));

		requests.add(new Request()
				.setUpdateCells(new UpdateCellsRequest()
						.setStart(new GridCoordinate()
								.setSheetId(0)
								.setRowIndex(0)
								.setColumnIndex(0))
						.setRows(rowDataList)
						.setFields("userEnteredValue,userEnteredFormat(backgroundColor,textFormat,horizontalAlignment)")));
		requests.add(new Request()
				.setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
						.setProperties(new SpreadsheetProperties()
								.setTitle("Таблица отправителей"))
						.setFields("title")));
		requests.add(new Request().setUpdateDimensionProperties(
				new UpdateDimensionPropertiesRequest()
						.setRange(new DimensionRange()
								.setSheetId(0)
								.setDimension("COLUMNS")
								.setStartIndex(2)
								.setEndIndex(4))
						.setProperties(new DimensionProperties()
								.setPixelSize(160))
						.setFields("pixelSize")));

		BatchUpdateSpreadsheetRequest body =
				new BatchUpdateSpreadsheetRequest().setRequests(requests);


		Sheets service = getSheetsService();

		service.spreadsheets()
				.batchUpdate(currentSpreadsheetId, body)
				.execute();

		return response.getSpreadsheetUrl();
	}*/
}

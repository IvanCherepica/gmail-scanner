package com.scanner.mailServices;


import com.scanner.models.DateWrapper;
import com.scanner.service.DateService;
import com.scanner.sheetExecutor.SheetExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class MailCheckerImpl implements MailChecker {
	@Autowired
	private DateService service;
	@Autowired
	private MailSender mailSender;
	@Autowired
	private SheetExecutor sheetService;

	private Date lastDate = null;
	private String user = null;
	private String password = null;
	private int answersAmount = 0;
	private String senderName = null;
	private String rightAnswers = null;
	private ArrayList<String> explanations = new ArrayList<>();
	private HashMap<String, String> userDetails = new HashMap<>();
	private ArrayList<String> rightAnswersList = new ArrayList<>();

	public MailCheckerImpl(String user, String password, int answersAmount, String senderName, String rightAnswers) {
		this.user = user;
		this.password = password;
		this.answersAmount = answersAmount;
		this.senderName = senderName;
		this.rightAnswers = rightAnswers;
	}

	public void check() {
		String host = "imap.googlemail.com";
		String mailStoreType = "imaps";
		String content = null;
		rightAnswersList = new ArrayList<>(Arrays.asList(rightAnswers.split(",")));
		populateExplanations();

		try {
			Properties properties = new Properties();
			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");

			Session emailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
			Store store = emailSession.getStore(mailStoreType);
			store.connect(host, user, password);
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);

			if (lastDate == null)
				lastDate = service.getLastDate().getCurrentDate();

			SearchTerm lastSentDate = new SentDateTerm(ComparisonTerm.GT, lastDate);
			SearchTerm sender = new FromTerm(new InternetAddress(senderName));
			Message messages[] = emailFolder.search(new AndTerm(lastSentDate, sender));

			for (Message message : messages) {
				if (!message.isSet(Flags.Flag.SEEN)) {
					content = message.getContent().toString();
					lastDate = message.getSentDate();
					service.addLastDate(new DateWrapper(message.getSentDate()));
					shapeAndSendAnswer(content);
				}
			}
			emailFolder.close(false);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateExplanations() {
		String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\explanations.txt";
		try {
			Scanner scan = new Scanner(new InputStreamReader(new FileInputStream(fileName), "cp1251"));
			scan.useDelimiter("\\d\\)\\s");
			try {
				while(scan.hasNext()){
					explanations.add(scan.next().replaceAll("\\r\\n", ""));
				}
			} finally {
				scan.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void shapeAndSendAnswer(String content) {
		ArrayList<String> specificExplanation = new ArrayList<>();
		int rightAnswersAmount = 0;
		StringBuilder result = new StringBuilder();

		ArrayList<String> splitContent = new ArrayList<>(Arrays.asList(content.split("\\s\\n")));
		for (int i = 0; i < splitContent.size(); i++) {
			if (i <= answersAmount - 1) {
				if (splitContent.get(i).substring(3).equals(rightAnswersList.get(i))) {
					rightAnswersAmount++;
				} else {
					specificExplanation.add("Ошибка в вопросе №" + (i+1) + ". Пояснение:\n" + explanations.get(i) + "\n");
				}
			} else {
				switch (splitContent.size() - i) {
					case 3:
						userDetails.put("Имя", splitContent.get(i).substring(5));
						break;
					case 2:
						userDetails.put("Телефон", splitContent.get(i).substring(9));
						break;
					case 1:
						userDetails.put("Email", splitContent.get(i).substring(7));
				}
			}
		}
		String url = sheetService
				.executeSheet(lastDate, userDetails.get("Имя"), userDetails.get("Телефон"), userDetails.get("Email"));
		result.append("Процент правильных ответов: ")
				.append(rightAnswersAmount * 100 / rightAnswersList.size())
				.append("%\n");
		if (specificExplanation.size() > 0) {
			for (String explanation : specificExplanation) {
				result.append(explanation);
			}
		}
		result.append(url);
		mailSender.sendMessage(userDetails.get("Email"), result.toString());
	}
}

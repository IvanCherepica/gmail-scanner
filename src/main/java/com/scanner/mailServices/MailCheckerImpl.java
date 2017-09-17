package com.scanner.mailServices;


import com.scanner.entity.DateWrapper;
import com.scanner.service.DateService;
import com.scanner.sheetService.SheetService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.util.*;
import java.util.regex.Pattern;


public class MailCheckerImpl implements MailChecker {
	@Autowired
	private DateService service;
	@Autowired
	private MailSender mailSender;
	@Autowired
	private SheetService sheetService;

	private Date lastDate = null;

	private String user = null;
	private String password = null;
	private int answersAmount = 0;
	private String senderName = null;
	private String rightAnswers = null;

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
		int rightAnswersAmount = 0;
		String content = null;
		String result = null;
		ArrayList<String> rightAnswersList = new ArrayList<>(Arrays.asList(rightAnswers.split(",")));
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
			emailFolder.open(Folder.READ_ONLY);

			SearchTerm sender = new FromTerm(new InternetAddress(senderName));
			Message messages[] = emailFolder.search(sender);

			String regEx = new StringBuilder("1:\\s\\d+\\s+(\\d:\\s\\d+\\s+){")
					.append(answersAmount - 1)
					.append("}Имя:\\s[А-Яа-я]+\\s+Телефон:\\s\\d+\\s+Email:\\s\\w+@\\w.+\\s+")
					.toString();
			Pattern pattern = Pattern.compile(regEx);
			HashMap<String, String> userDetails = new HashMap<>();

			if (lastDate == null) {
				lastDate = service.getLastDate().getCurrentDate();
			}

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				String messageContent = message.getContent().toString();
				if (message.getSentDate().after(lastDate)) {
					if (pattern.matcher(messageContent).matches()) {
						System.out.println(lastDate);
						System.out.println(message.getSentDate());
						System.out.println("Subject: " + message.getSubject());
						System.out.println("From: " + message.getFrom()[0]);
						System.out.println(messageContent);
						content = messageContent;
						lastDate = message.getSentDate();
						service.addLastDate(new DateWrapper(message.getSentDate()));
					}
				}
			}
			if (content != null) {
				ArrayList<String> splitContent = new ArrayList<>(Arrays.asList(content.split("\\s\\n")));
				for (int i = 0; i < splitContent.size(); i++) {
					if (i <= answersAmount - 1) {
						if (splitContent.get(i).substring(3).equals(rightAnswersList.get(i))) {
							System.out.println(i);
							rightAnswersAmount++;
						}
					} else if (splitContent.size() - i == 3) {
						userDetails.put("Имя", splitContent.get(i).substring(5));
					} else if (splitContent.size() - i == 2) {
						userDetails.put("Телефон", splitContent.get(i).substring(9));
					} else if (splitContent.size() - i == 1) {
						userDetails.put("Email", splitContent.get(i).substring(7));
					}
				}
				String url = sheetService
						.createSheet(lastDate, userDetails.get("Имя"), userDetails.get("Телефон"), userDetails.get("Email"));
				result = new StringBuilder("Процент правильных ответов: ")
						.append(rightAnswersAmount*100 / rightAnswersList.size())
						.append("%\n")
						.append(url)
						.toString();
				System.out.println(userDetails.get("Email"));
				System.out.println(result);
				mailSender.sendMessage(userDetails.get("Email"), result);
			} else {
				System.out.println("Нет сообщений");
			}
			emailFolder.close(false);
			store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

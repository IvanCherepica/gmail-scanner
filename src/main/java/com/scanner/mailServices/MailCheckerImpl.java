package com.scanner.mailServices;


import com.scanner.DTO.Letter;
import com.scanner.properties.MailProperties;
import com.scanner.sheetExecutor.SheetExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.mail.Flags.Flag.SEEN;

@PropertySource("classpath:application.properties")
public class MailCheckerImpl implements MailChecker {
	@Autowired
	private MailSender mailSender;
	@Autowired
	private SheetExecutor sheetExecutor;
	@Autowired
	private MailProperties mailProp;
	@Value("${received.mail.subject}")
	private String mailSubject;

	private boolean launched;
	private String user;
	private String password;
	private int answersAmount;
	private String senderName;
	private String rightAnswers;
	private List<String> explanations;
	private List<String> rightAnswersList;
	private Pattern nameSearch = Pattern.compile("Имя\\s?:\\s?[А-Яа-я]*-?\\s?[А-Яа-я]*");
	private Pattern dateSearch = Pattern.compile("\\d\\d\\.\\d\\d\\.\\d{4}\\sв\\s\\d\\d:\\d\\d:\\d\\d");
	private Pattern phoneSearch = Pattern.compile("Телефон\\s?:\\s?((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}");

	public MailCheckerImpl(String user, String password, int answersAmount, String senderName,
						   List<String> explanations, String rightAnswers) {
		this.user = user;
		this.password = password;
		this.answersAmount = answersAmount;
		this.senderName = senderName;
		this.explanations = explanations;
		this.rightAnswers = rightAnswers;
	}
	@Override
	public boolean isLaunched() {
		return launched;
	}

	public void check() {
		launched = true;
		String host = mailProp.getInboxHost();
		String mailStoreType = mailProp.getInboxStoreType();
		int quantity = 0;
		List<String> contentList = new ArrayList<>();
		rightAnswersList = new ArrayList<>(Arrays.asList(rightAnswers.split(",")));
		try {
			Properties properties = new Properties();
			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", mailProp.getInboxPort());
			properties.put("mail.pop3.starttls.enable", mailProp.getInboxStartTls());

			Session emailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
			Store store = emailSession.getStore(mailStoreType);
			store.connect(host, user, password);
			Folder emailFolder = store.getFolder(mailProp.getInboxStoreFolder());
			emailFolder.open(Folder.READ_WRITE);

			SearchTerm sender = new FromTerm(new InternetAddress(senderName));
			Flags seen = new Flags(SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

			Message messages[] = emailFolder.search(new AndTerm(unseenFlagTerm, sender));
			for (Message message : messages) {
				if (message.getSubject().contains(mailSubject)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy 'в' HH:mm:ss");
					String date = dateFormat.format(message.getSentDate());
					contentList.add(message.getContent().toString() + date);
					quantity++;
				}
			}
			if (quantity > 0)
				sandeMessagesAndWriteTable(contentList);
			emailFolder.close(false);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		launched = false;
	}

	private void sandeMessagesAndWriteTable(List<String> contentList) throws Exception {
		List<List<Object>> userDetailsList = new ArrayList<>();
		List<Letter> letters = new ArrayList<>();
		for (String content: contentList) {
			List<String> emailAddress = new ArrayList<>();
			int rightAnswersAmount = 0;
			StringBuilder result = new StringBuilder();
			List<String> specificExplanation = new ArrayList<>();
			List<String> splitContent = new ArrayList<>(Arrays.asList(content.split("<br>")));
			List<Object> userDetails = new ArrayList<>();
			try {
				for (int i = 0; i < answersAmount; i++) {
					if (splitContent.get(i).substring(3).equals(rightAnswersList.get(i))) {
						rightAnswersAmount++;
					} else {
						specificExplanation.add("Ошибка в вопросе №" + (i + 1) + ". Пояснение:\n" + explanations.get(i) + "\n\n");
					}
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
			}



			emailAddress = getEmailAddress(content);
			String stringAddress = emailAddress.toString();
			userDetails.add(getUserDetail(content, nameSearch, true));
			userDetails.add(getUserDetail(content, phoneSearch, true));
			userDetails.add(stringAddress.substring(1, stringAddress.length()-1));
			userDetails.add(getUserDetail(content, dateSearch, false));
			userDetailsList.add(userDetails);
			if (!specificExplanation.isEmpty()) {
				String lastComment = specificExplanation.get(specificExplanation.size() - 1);
				specificExplanation.remove(specificExplanation.size() - 1);
				specificExplanation.add(lastComment.substring(0, lastComment.length()-2));
			}
			result.append("Процент правильных ответов: ")
					.append(rightAnswersAmount * 100 / rightAnswersList.size())
					.append("%\n\n");
			if (specificExplanation.size() > 0) {
				for (String explanation : specificExplanation) {
					result.append(explanation);
				}
			}
			letters.add(new Letter(emailAddress, result.toString()));
		}

		updateSpreadSheet(userDetailsList);
		sendMail(letters);
	}

	private List<String> getEmailAddress(String content) {
		List<String> emails = new ArrayList<>();
		Pattern searchPattern = Pattern.compile("Email.?(.\\w)?:.\\w+.?\\w+?\\@\\w+.?\\w+");
		Matcher searchMatcher = searchPattern.matcher(content);
		Pattern cutPattern = Pattern.compile("[^:\\s?]*$");
		while(searchMatcher.find()) {
			String rec = content.substring(searchMatcher.start(), searchMatcher.end());
			Matcher cutMatcher = cutPattern.matcher(rec);
			if (cutMatcher.find()) {
				String email = rec.substring(cutMatcher.start(), cutMatcher.end());
				if (!email.isEmpty()) {
					emails.add(email);
				}
			}
		}
		return emails;
	}

	private Object getUserDetail(String content, Pattern pattern, boolean isCut) {
		String detail = "";
		Matcher detailMatcher = pattern.matcher(content);
		Pattern cutDetail = Pattern.compile("[^:\\s?]*$");
		while(detailMatcher.find()) {
			String rawDetail = content.substring(detailMatcher.start(), detailMatcher.end());
			if (isCut) {
				Matcher cutMatcher = cutDetail.matcher(rawDetail);
				if (cutMatcher.find()) {
					String nameX = rawDetail.substring(cutMatcher.start(), cutMatcher.end());
					if (!nameX.isEmpty())
						detail = nameX;
				}
			} else {
				detail = rawDetail;
			}
		}
		return detail;
	}



	private void updateSpreadSheet(List<List<Object>> userDetailsList) {
		new Thread(
				() -> sheetExecutor.appendData(userDetailsList)
		).start();
	}

	private void sendMail(List<Letter> letters) {
		new Thread(
				() -> mailSender.sendMessage(letters)
		).start();
	}
}

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
		Map<Message,String> contentMap = new HashMap<>();
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
					String content = message.getContent().toString() + date;
					contentMap.put(message, content);
					quantity++;
				}
			}
			if (quantity > 0)
				sandeMessagesAndWriteTable(contentMap);
			emailFolder.close(false);
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		launched = false;
	}

	private void sandeMessagesAndWriteTable(Map<Message,String> contentMap) throws Exception {
		List<List<Object>> userDetailsList = new ArrayList<>();
		List<Letter> letters = new ArrayList<>();
		Set<Message> unReadableMessages = new HashSet<>();
		for (Map.Entry<Message, String> entry : contentMap.entrySet()) {
			String content = entry.getValue();
			Message message = entry.getKey();

			List<String> emailAddress;
			int rightAnswersAmount = 0;
			StringBuilder result = new StringBuilder();
			List<String> specificExplanation = new ArrayList<>();
			List<String> answersList = getAnswersList(content);
			List<Object> userDetails = new ArrayList<>();
			try {
				if (answersList.size() == answersAmount) {
					for (int i = 0; i < answersAmount; i++) {
						if (answersList.get(i).equals(rightAnswersList.get(i))) {
							rightAnswersAmount++;
						} else {
							specificExplanation.add("Ошибка в вопросе №" + (i + 1) + ". Пояснение:\n" + explanations.get(i) + "\n\n");
						}
					}
				} else {
					unReadableMessages.add(message);
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
				unReadableMessages.add(message);
			}

			if (!unReadableMessages.contains(message)) {
				emailAddress = getEmailAddress(content);
				String stringAddress = emailAddress.toString();
				String name = getUserDetail(content, nameSearch, true);
				String phone = getUserDetail(content, phoneSearch, true);
				String address = stringAddress.substring(1, stringAddress.length() - 1);
				String date = getUserDetail(content, dateSearch, false);

				if (!name.isEmpty() && !address.isEmpty()) {
					userDetails.add(name);
					userDetails.add(phone);
					userDetails.add(address);
					userDetails.add(date);
					userDetailsList.add(userDetails);
					if (!specificExplanation.isEmpty()) {
						String lastComment = specificExplanation.get(specificExplanation.size() - 1);
						specificExplanation.remove(specificExplanation.size() - 1);
						specificExplanation.add(lastComment.substring(0, lastComment.length() - 2));
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
				} else {
					unReadableMessages.add(message);
				}
			}
		}
		doNotRead(unReadableMessages);
		updateSpreadSheet(userDetailsList);
		sendMail(letters);
	}

	private List<String> getAnswersList(String content) {
		List<String> answers = new ArrayList<>();
		List<String> splitContent = new ArrayList<>(Arrays.asList(content.split("<br>")));
		Pattern pattern = Pattern.compile("^\\w+:\\s?\\w+$");
		Pattern cutPattern = Pattern.compile("[^:\\s?]*$");
		for (String answer : splitContent) {
			if (pattern.matcher(answer).matches()) {
				Matcher cutMatcher = cutPattern.matcher(answer);
				if (cutMatcher.find()) {
					answers.add(answer.substring(cutMatcher.start(), cutMatcher.end()));
				}
			}
		}
		return answers;
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

	private String getUserDetail(String content, Pattern pattern, boolean isCut) {
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

	private void doNotRead(Set<Message> unReadableMessages) {
		String host = mailProp.getInboxHost();
		String mailStoreType = mailProp.getInboxStoreType();
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

			Message[] messages = new Message[unReadableMessages.size()];
			unReadableMessages.toArray(messages);
			emailFolder.setFlags(messages, new Flags(Flags.Flag.SEEN), false);

			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

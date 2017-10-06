package com.scanner.mailServices;


import com.scanner.properties.MailProperties;
import com.scanner.sheetExecutor.SheetExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
	private Date lastDate;
	private String user;
	private String password;
	private int answersAmount;
	private String senderName;
	private String rightAnswers;
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
	@Override
	public boolean isLaunched() {
		return launched;
	}

	public void check() {
		launched = true;

		String host = mailProp.getInboxHost();
		String mailStoreType = mailProp.getInboxStoreType();
		String content = null;
		rightAnswersList = new ArrayList<>(Arrays.asList(rightAnswers.split(",")));
		populateExplanations();

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
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

			Message messages[] = emailFolder.search(new AndTerm(unseenFlagTerm, sender));

			for (Message message : messages) {
				if (message.getSubject().contains(mailSubject)) {
					content = message.getContent().toString();
					lastDate = message.getSentDate();
					shapeAndSendAnswer(content);
				}
			}
			emailFolder.close(false);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		launched = false;
	}

	private void populateExplanations() {
		String fileName = "explanations.txt";
		try (Scanner scan = new Scanner(new FileInputStream(fileName))
				.useDelimiter("\\d\\)\\s")) {
			while (scan.hasNext()) {
				explanations.add(scan.next().replaceAll("\\r\\n", ""));
			}
		}catch(IOException e) {
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
					specificExplanation.add("Ошибка в вопросе №" + (i + 1) + ". Пояснение:\n" + explanations.get(i) + "\n\n");
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
		if (!specificExplanation.isEmpty()) {
			String lastComment = specificExplanation.get(specificExplanation.size() - 1);
			specificExplanation.remove(specificExplanation.size() - 1);
			specificExplanation.add(lastComment.substring(0, lastComment.length()-2));
		}
		sheetExecutor.appendData(lastDate, userDetails.get("Имя"), userDetails.get("Телефон"), userDetails.get("Email"));
		result.append("Процент правильных ответов: ")
				.append(rightAnswersAmount * 100 / rightAnswersList.size())
				.append("%\n\n");
		if (specificExplanation.size() > 0) {
			for (String explanation : specificExplanation) {
				result.append(explanation);
			}
		}
		sendMail(userDetails.get("Email"), result.toString());
	}

	private void sendMail(String sender, String result) {
		new Thread(
				() -> mailSender.sendMessage(sender, result)
		).start();
	}
}

package com.scanner;

import com.scanner.timer.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = "com.scanner")
@SpringBootApplication
@EnableScheduling
public class MyApplication{
	@Autowired
	Timer timer;

	private static String username = null;
	private static String password = null;
	private static int answersAmount = 0;
	private static String sender = null;
	static String rightAnswers = null;

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);

		/*FileInputStream fis;
		Properties property = new Properties();
		try {
			fis = new FileInputStream("src/main/resources/application.properties");
			property.load(fis);
			sender = property.getProperty("sender");
			username = property.getProperty("username");
			password = property.getProperty("password");
			answersAmount = Integer.parseInt(property.getProperty("answers.amount"));
			rightAnswers = property.getProperty("right.answers");
		} catch (IOException e) {
			System.err.println("ОШИБКА: Файл свойств отсуствует!");
		}
*/
		/*TimerTask timerTask = timer;
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, 10*1000);*/
	}


}

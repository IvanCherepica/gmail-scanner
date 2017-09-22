package com.scanner;

import com.scanner.timer.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;

@ComponentScan(basePackages = "com.scanner")
@SpringBootApplication
@EnableScheduling
public class MyApplication{

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}
}

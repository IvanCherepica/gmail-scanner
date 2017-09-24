package com.scanner.dao;

import com.scanner.model.DateWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.xml.bind.SchemaOutputResolver;
import java.util.Date;

@Transactional
@Repository
public class DateDaoImpl implements DateDao {
	@PersistenceContext
	private EntityManager entityManager;

	public DateWrapper LastDate() {
		DateWrapper dateWrapper = null;
		try {
			dateWrapper = (DateWrapper) entityManager
					.createQuery("SELECT u FROM DateWrapper u WHERE u.id =1").getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Не найдена дата последнего отправленного сообщения.");
			System.out.println("Проверяться будут только сообщения за сегодняшний день.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dateWrapper == null) {
			return new DateWrapper(new Date());
		} else {
			return dateWrapper;
		}
	}

	public void rewriteLastDate(DateWrapper dateWrapper) {
		entityManager.merge(dateWrapper);
	}


}

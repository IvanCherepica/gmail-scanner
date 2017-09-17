package com.scanner.dao;

import com.scanner.entity.DateWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Transactional
@Repository
public class DateDaoImpl implements DateDao {
	@PersistenceContext
	private EntityManager entityManager;

	public DateWrapper getLastDate() {
		DateWrapper dateWrapper = null;
		try {
			dateWrapper = (DateWrapper) entityManager
					.createQuery("SELECT u FROM DateWrapper u WHERE u.id =1").getSingleResult();
		} catch (Exception e) {

		}
		if (dateWrapper == null) {
			return new DateWrapper(new Date());
		} else {
			return dateWrapper;
		}
	}

	public void addDate(DateWrapper dateWrapper) {
		dateWrapper.setId(1);
		entityManager.merge(dateWrapper);
	}


}

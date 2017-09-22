package com.scanner.dao;

import com.scanner.models.DateWrapper;
import com.scanner.models.SheetsIdentifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Transactional
@Repository
public class SheetsIdDaoImpl implements SheetsIdDao {
	@PersistenceContext
	private EntityManager entityManager;


	@Override
	public SheetsIdentifier getLastIdentifier() {
		SheetsIdentifier getLastIdentifier = null;
		try {
			getLastIdentifier = (SheetsIdentifier) entityManager
					.createQuery("SELECT u FROM SheetsIdentifier u WHERE u.id =1").getSingleResult();
		} catch (Exception e) {

		}
		return getLastIdentifier;
	}

	@Override
	public void addIdentifier(SheetsIdentifier sheetsIdentifier) {
		sheetsIdentifier.setId(1);
		entityManager.merge(sheetsIdentifier);
	}
}

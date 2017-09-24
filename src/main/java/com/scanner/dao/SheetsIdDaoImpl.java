package com.scanner.dao;

import com.scanner.model.SheetsIdentifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Transactional
@Repository
public class SheetsIdDaoImpl implements SheetsIdDao {
	@PersistenceContext
	private EntityManager entityManager;


	@Override
	public SheetsIdentifier LastIdentifier() {
		SheetsIdentifier LastIdentifier = null;
		try {
			LastIdentifier = (SheetsIdentifier) entityManager
					.createQuery("SELECT u FROM SheetsIdentifier u WHERE u.id =1").getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Не найдена таблица для записи.");
			System.out.println("Будет сгенерированна новая таблица.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return LastIdentifier;
	}

	@Override
	public void rewriteIdentifier(SheetsIdentifier sheetsIdentifier) {
		entityManager.merge(sheetsIdentifier);
	}
}

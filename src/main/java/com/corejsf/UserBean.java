package com.corejsf;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@Named("user")
@SessionScoped
public class UserBean implements Serializable {

	private static final Logger logger = Logger.getLogger("com.corejsf");
	private static final long serialVersionUID = 1L;

	private String name;
	private String password;
	private int count;
	private boolean loggedIn;
	
	@PersistenceUnit(unitName="default")
	private EntityManagerFactory emf;
	
	@Resource
	private UserTransaction utx;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCount() {
		return count;
	}
	
	public String login() {
		try {
			doLogin();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "login failed", e);
			
			return "internalError";
		}
		
		if(loggedIn) {
			return "loginSuccess";
		} else {
			return "loginFailure";
		}
	}
	
	public String logout() {
		loggedIn = false;
		name = "";
		password = "";
		
		return "login";
	}
	
	private void doLogin() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		EntityManager em = emf.createEntityManager();
		
		try {
			utx.begin();
			em.joinTransaction();
			boolean committed = true;
			
			try {
				TypedQuery<Credential> query = em.createQuery(
					"SELECT c FROM Credential c WHERE c.username = :username",
					Credential.class);
				query.setParameter("username", name);
				List<Credential> result = query.getResultList();
				
				if (result.size() == 1) {
					Credential credential = result.get(0);
					if (credential.getPassword().equals(password)) {
						loggedIn = true;
						count = credential.incrementLoginCount();
					}
				}
				
				utx.commit();
				committed = true;
			} finally {
				if (!committed) utx.rollback();
			}
		} finally {
			em.close();
		}
	}
}

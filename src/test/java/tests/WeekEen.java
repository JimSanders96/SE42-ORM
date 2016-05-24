package tests;

import bank.domain.Account;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import util.DatabaseCleaner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by David on 24-5-2016.
 */
public class WeekEen {

    private EntityManager em;
    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("bankPU");
        em = emf.createEntityManager();

        DatabaseCleaner databaseCleaner = new DatabaseCleaner(em);

        try {
            databaseCleaner.clean();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);
//TODO: verklaar en pas eventueel aan
        assertNull(account.getId());
        em.getTransaction().commit();
        System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
        assertTrue(account.getId() > 0L);
    }
}

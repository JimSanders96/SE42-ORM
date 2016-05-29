package tests;

import bank.dao.AccountDAOJPAImpl;
import bank.domain.Account;
import bank.domain.AccountSequence;
import bank.domain.AccountTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.DatabaseCleaner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by David on 24-5-2016.
 */
public class WeekEen {

     /*
        Voor elke test moet je in ieder geval de volgende vragen beantwoorden:
        1. Wat is de waarde van asserties en printstatements?
            Corrigeer verkeerde asserties zodat de test ‘groen’ wordt.
        3. Welke SQL statements worden gegenereerd?
        4. Wat is het eindresultaat in de database?
        Verklaring van bovenstaande drie observaties.
        De antwoorden op de vragen kun je als commentaar bij de testen vastleggen.
     */

    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("bankPU");
        EntityManager em = emf.createEntityManager();

        DatabaseCleaner databaseCleaner = new DatabaseCleaner(em);

        try {
            databaseCleaner.clean();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {

        EntityManager em = emf.createEntityManager();

        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);

//TODO: verklaar en pas eventueel aan
        // Een account krijgt pas een ID op het moment dat hij gecommit wordt naar de database (generated val)
        // Insert Account
        assertNull(account.getId());
        em.getTransaction().commit();
        System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
        // Zie bovenstaande comment
        assertTrue(account.getId() > 0L);

        //2.	Welke SQL statements worden gegenereerd?
        //      Er wordt een create table account gemaakt en een insert in het account
        //      INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)

        //3.	Wat is het eindresultaat in de database?
        //      1 Account met een ID, account nummer 111 en balance en threshold 0.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Een ID (generated value IDENTITY) wordt pas aangemaakt tijdens het committen (het daadwerkelijke 'in de databse zetten')

    }

    @Test
    public void test2() {
        EntityManager em = emf.createEntityManager();
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);
        assertNull(account.getId());
        em.getTransaction().rollback();
// TODO code om te testen dat table account geen records bevat. Hint: bestudeer/gebruik AccountDAOJPAImpl

        AccountDAOJPAImpl dao = new AccountDAOJPAImpl(em);
        // Haal alle records in de tabel Account op en controleer of het resultaat geen accounts bevat.
        assertTrue(dao.findAll().isEmpty());

        //2.	Welke SQL statements worden gegenereerd?
        //      Er wordt een rollback uitgevoerd.
        //      Om alle accounts op te halen: SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT

        //3.	Wat is het eindresultaat in de database?
        //      Account is leeg.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Omdat er een rollback in de transaction wordt gedaan worden er geen statements uitgevoerd op de database.
    }

    @Test
    public void test3() {
        EntityManager em = emf.createEntityManager();

        Long expected = -100L;
        Account account = new Account(111L);
        account.setId(expected);
        em.getTransaction().begin();
        em.persist(account);
//TODO: verklaar en pas eventueel aan
        // Het ID van het account wordt hier handmatig geset.
//assertNotEquals(expected, account.getId();
        assertEquals(expected, account.getId());
        em.flush();

//TODO: verklaar en pas eventueel aan
        // Omdat het account nu naar de database is gepusht heeft hij een auto-gen-value toegewezen gekregen.
//assertEquals(expected, account.getId();
        Assert.assertNotEquals(expected, account.getId());
        em.getTransaction().commit();
//TODO: verklaar en pas eventueel aan

        //2.	Welke SQL statements worden gegenereerd?
        //		flush: INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)

        //3.	Wat is het eindresultaat in de database?
        //		Het account staat in de database met een auto-gen ID

        //4.	Verklaring van bovenstaande drie observaties.
        //		Eerst wordt het ID handmatig geset. Bij de flush wordt hij in de database gezet met een auto-gen ID
    }


    @Test
    public void test4() {
        EntityManager em = emf.createEntityManager();

        Long expectedBalance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(expectedBalance);
        em.getTransaction().commit();
        assertEquals(expectedBalance, account.getBalance());
//TODO: verklaar de waarde van account.getBalance
        // De waarde is geset naar de expectedBalance (400L)
        Long acId = account.getId();

        account = null;
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class, acId);
//TODO: verklaar de waarde van found.getBalance
        // Je zoekt (en vindt) het account met het ID van het toegevoegde account. De waarde van de balance daarvan is dus nog steeds expectedBalance.
        // Het account is alleen lokaal null geset.
        assertEquals(expectedBalance, found.getBalance());

        //2.	Welke SQL statements worden gegenereerd?
        //      INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?);
        //		select lastval();
        //		SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT WHERE (ID = ?);

        //3.	Wat is het eindresultaat in de database?
        //      1 Account met nummer 114, balance 400 & threshold 0.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Er wordt een account gemaakt en verstuurd naar de database. Het account wordt op een gegeven moment lokaal op null geset, maar in de database bestaat deze nog.
    }

    @Test
    public void test5() {
        EntityManager em = emf.createEntityManager();

        Long expectedBalance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(expectedBalance);
        em.getTransaction().commit();
        Long acId = account.getId();

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class, acId);

        em2.persist(found);
        found.setBalance(451L);
        em2.getTransaction().commit();

        EntityManager em3 = emf.createEntityManager();
        em3.getTransaction().begin();
        account = em3.find(Account.class, acId);

        assertEquals(account.getBalance(), found.getBalance());

        //2.	Welke SQL statements worden gegenereerd?
//      INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?);
//		select lastval();
//		SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT WHERE (ID = ?);
//		UPDATE ACCOUNT SET BALANCE = ? WHERE (ID = ?)

// 		3.	Wat is het eindresultaat in de database?
//      1 Account met account nummer 114, balance 451 & threshold 0.

// 		4.	Verklaring van bovenstaande drie observaties.
//		Het account wordt opgehaald uit de database, veranderd en gecommit. Daardoor heeft het account in de database de waardes gekregen van het aangepaste object.
    }

    @Test
    public void test6() {
        EntityManager em = emf.createEntityManager();

        Account acc = new Account(1L);
        Account acc2 = new Account(2L);
        Account acc9 = new Account(9L);
        AccountDAOJPAImpl accountDAOJPA = new AccountDAOJPAImpl(em);
// scenario 1
        Long balance1 = 100L;
        em.getTransaction().begin();
        em.persist(acc);
        acc.setBalance(balance1);
        //Het lokale object heeft de goede balance waarde
        assertEquals(acc.getBalance(), balance1);
        em.getTransaction().commit();
        //Het database object heeft de goede balance waarde
        Account accountFromDatabase = accountDAOJPA.find(acc.getId());
        assertEquals(accountFromDatabase.getBalance(), balance1);

        //2.	Welke SQL statements worden gegenereerd?
        //		INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)

        //3.	Wat is het eindresultaat in de database?
        //		Een account met een bepaalde balance.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Phase 1 completed.


// scenario 2
        Long balance2a = 211L;
        acc = new Account(2L);
        em.getTransaction().begin();
        acc9 = em.merge(acc);
        // Kijk of beide accounts nu hetzelfde nummer hebben.
        assertEquals(acc9.getAccountNr(), acc.getAccountNr());

        acc.setBalance(balance2a);
        acc9.setBalance(balance2a * 2);
        //Kijk of de balances kloppen
        assertTrue(acc.getBalance() * 2 == acc9.getBalance());
        em.getTransaction().commit();

        Account accFromDatabase = accountDAOJPA.findByAccountNr(acc9.getAccountNr());
        //Zowel acc als acc9 hebben hetzelfde acc nummer. Omdat acc9 nieuwer is, zal het account dat uit de database gehaald wordt de waardes van acc9 bevatten.
        assertTrue(accFromDatabase.getBalance() == acc9.getBalance());

        //2.	Welke SQL statements worden gegenereerd?
        //INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)
        //SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT WHERE (ACCOUNTNR = ?)

        //3.	Wat is het eindresultaat in de database?
        //		De waardes van acc9 stan nu in de database.

        //4.	Verklaring van bovenstaande drie observaties.
        //		Na het mergen is het account nummer gelijk. Van beide accounts worden de balances aangepast. Omdat er gezegd wordt dat acc9 gemerged wordt met acc, zal acc9 na de commit op de plaats van acc staan in de database.


// scenario 3
        Long balance3b = 322L;
        Long balance3c = 333L;
        acc = new Account(3L);
        em.getTransaction().begin();
        Account acc3 = em.merge(acc);
//		assertTrue(em.contains(acc)); // acc zit niet in deze transaction.
        assertFalse(em.contains(acc));

        assertTrue(em.contains(acc3)); // Deze is zojuist uit de database gehaald (gemerged)

//		assertEquals(acc,acc3);  //acc en acc3 zijn niet dezelfde objecten.
        Assert.assertNotEquals(acc, acc3);

        acc3.setBalance(balance3b);
        acc.setBalance(balance3c);
        em.getTransaction().commit();

        accountFromDatabase = accountDAOJPA.findByAccountNr(acc.getAccountNr());
        Assert.assertNotNull(accountFromDatabase);
        assertEquals(balance3b, accountFromDatabase.getBalance());

        //2.	Welke SQL statements worden gegenereerd?
        //INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)
        //SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT WHERE (ACCOUNTNR = ?)

        //3.	Wat is het eindresultaat in de database?
        //		Het account heeft nu de waardes van acc3

        //4.	Verklaring van bovenstaande drie observaties.
        //		Account acc wordt niet in deze transaction meegenomen. Hierdoor is acc3 het acc met de 'meest recente' waardes wanneer de commit gedaan wordt. Het account in de database zal daarom de waardes van acc3 bevatten.

// scenario 4
        System.out.println("Begin Scenario 4 \n");
        Account account = new Account(114L);
        account.setBalance(450L);
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();

        Account account2 = new Account(114L);
        Account account22 = account2;
        account22.setBalance(650l);
        assertEquals((Long)650L,account2.getBalance());  // Omdat het account nu een referentie naar 2 objecten heeft, is een e.v.t. aanpassing bij al deze objecten te zien.
        account2.setId(account.getId());
        em.getTransaction().begin();
        account2 = em.merge(account2);
        assertSame(account,account2);  // account2 heeft hardcoded hetzelfde ID als account gekregen. Omdat er een merge uitgevoerd wordt, wordt account nu account 2.
        assertTrue(em.contains(account2));  //account2 is aangepast in de transaction, waardoor zijn changes verstuurd worden naar de database.
        assertFalse(em.contains(account22)); // Omgekeerde van comment hierboven.
        account22.setBalance(850l);
        assertEquals((Long)650L,account.getBalance());  // Vanwege de merge heeft account22 nu geen referentie meer naar het object van account2.
        assertEquals((Long)650L,account2.getBalance());  // Vanwege de merge heeft account22 nu geen referentie meer naar het object van account2.
        em.getTransaction().commit();
        em.close();

        //2.	Welke SQL statements worden gegenereerd?
        //INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?)
        //UPDATE ACCOUNT SET BALANCE = ? WHERE (ID = ?)

        //3.	Wat is het eindresultaat in de database?
        //		account2 staat in de database

        //4.	Verklaring van bovenstaande drie observaties.
        //		Er is een merge uitgevoerd tussen objecten met hetzelfde ID. De meest recente versie wordt in de database gezet.
    }



    @Test
    public void test7() {
        EntityManager em = emf.createEntityManager();

        Account acc1 = new Account(77L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
//Database bevat nu een account.

// scenario 1
        Account account1;
        Account account2;
        account1 = em.find(Account.class, acc1.getId());
        account2 = em.find(Account.class, acc1.getId());
        assertSame(account1, account2);

// scenario 2
        account1 = em.find(Account.class, acc1.getId());
        em.clear();
        account2 = em.find(Account.class, acc1.getId());
        Assert.assertNotSame(account1, account2);

        // account1 wordt uit de persistence context gehaald, waarna deze leeggemaakt wordt.
        // Wanneer account2 het account wil ophalen, is deze null.



//		2.	Welke SQL statements worden gegenereerd?
//      INSERT INTO ACCOUNT (ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?);
//		select lastval();
//		SELECT ID, ACCOUNTNR, BALANCE, THRESHOLD FROM ACCOUNT WHERE (ID = ?);

// 		3.	Wat is het eindresultaat in de database?
//      1 Account  met account nummer 77, balance 0 & threshold 0.

// 		4.	Verklaring van bovenstaande drie observaties.
//		De persistence context wordt geleegd door em.clear aan te roepen. Alle zoekopdrachten naar een object in een geclearde persistence context zullen 'null' teruggeven.
    }

    @Test
    public void test8() {
        EntityManager em = emf.createEntityManager();
        Account acc1 = new Account(88L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
        Long id = acc1.getId();
//Database bevat nu een account.

        em.remove(acc1);
        assertEquals(id, acc1.getId());
        Account accFound = em.find(Account.class, id);
        assertNull(accFound);
        // acc1 bestaat nog lokaal, waardoor je gewoon getID kunt aanroepen op dat object.
        // Echter, omdat hij verwijderd is uit de persistence context, zal deze null teruggeven wanneer je hem daar probeert te vinden.

    }

    @Test
    public void test9() {
        EntityManager em = emf.createEntityManager();

        // Account maakt gebruik van IDENTITY strategy

        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);

        // Niet in database? Geen idee.
        assertNull(account.getId());

        em.getTransaction().commit();
        System.out.println("AccountId: " + account.getId());

        // Wel in database? Idee!
        assertTrue(account.getId() > 0L);

        /////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////                SPACER           ////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////


        em.getTransaction().begin();
        AccountSequence account2 = new AccountSequence(114L);
        em.persist(account2);

        Assert.assertNotNull(account2.getId()); // Dit hoort niet null te zijn, omdat een SEQUENCE strategy wel al een ID aanmaakt tijdens persist. Alleen hier werkt dit om een of andere reden niet.
        em.getTransaction().commit();
        System.out.println("AccountId: " + account2.getId());

        assertTrue(account2.getId() > 0L);

        //2.	Welke SQL statements worden gegenereerd?
        //      Er wordt een create table account gemaakt en een insert in het account
        //      INSERT INTO ACCOUNTVOORVRAAG9 (ID, ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?, ?)
        //		SELECT * FROM SEQUENCE WHERE SEQ_NAME = SEQ_GEN_TABLE
        //		select nextval(SEQ_GEN_SEQUENCE)
        //		ALTER SEQUENCE SEQ_GEN_SEQUENCE INCREMENT BY 50

        //3.	Wat is het eindresultaat in de database?
        //      1 Account die gebruik maakt van de Sequence.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Het maakt nu gebruik van Sequence i.p.v Identity

        /////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////                SPACER           ////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        AccountTable account3 = new AccountTable(115L);
        em.getTransaction().begin();
        em.persist(account3);


        em.getTransaction().commit();
        System.out.println("AccountId: " + account3.getId());

        assertTrue(account3.getId() > 0L);

        //2.	Welke SQL statements worden gegenereerd?
        //      UPDATE SEQUENCE SET SEQ_COUNT = SEQ_COUNT + ? WHERE SEQ_NAME = ?
        //		SELECT SEQ_COUNT FROM SEQUENCE WHERE SEQ_NAME = ?
        //		INSERT INTO ACCOUNTVOORVRAAG9TABLE (ID, ACCOUNTNR, BALANCE, THRESHOLD) VALUES (?, ?, ?, ?)

        //3.	Wat is het eindresultaat in de database?
        //      1 Account die gebruik maakt van de Sequence. Sequences worden gesimuleerd m.b.v. tables in een ORM-based provider.

        //4.	Verklaring van bovenstaande drie observaties.
        //      Het maakt nu gebruik van Table i.p.v Identity of Sequence

    }
}

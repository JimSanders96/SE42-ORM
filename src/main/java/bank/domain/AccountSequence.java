package bank.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@SequenceGenerator(name = "seq")
@NamedQueries({
    @NamedQuery(name = "Account.getAll", query = "select a from Account as a"),
    @NamedQuery(name = "Account.count", query = "select count(a) from Account as a"),
    @NamedQuery(name = "Account.findByAccountNr", query = "select a from Account as a where a.accountNr = :accountNr")
})
public class AccountSequence implements Serializable {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long Id;
    @Column(unique = true)
    private Long accountNr;
    private Long balance;
    private Long threshold;

    public AccountSequence() {
    }

    public AccountSequence(Long accountNr){
        balance = 0L;
        threshold = 0L;
        this.accountNr = accountNr;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public Long getAccountNr() {
        return accountNr;
    }

    public void setAccountNr(Long accountNr) {
        this.accountNr = accountNr;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public Boolean add(Long amount) {
        if (balance + amount >= threshold) {
            balance += amount;
            return true;
        } else {
            return false;
        }
    }
}

package splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Account implements Serializable
{
    private static final long serialVersionUID = 123123123123L;
    private String login;
    private String password;
    private String name;
    private ArrayList<Debt> debts;

    public Account(String login, String password, BufferedReader reader) {
        this.login = login;
        this.password = password;
        debts = new ArrayList<>(5);
        this.name = enterName(reader);
    }

    //for test users
    public Account(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
        debts = new ArrayList<>(5);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Debt> getDebts() {
        return debts;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Debt findDebtByCreditor(Account creditor) {
        for (Debt debt : debts) {
            if (debt.getCreditor() == creditor) {
                return debt;
            }
        }
        //System.out.printf("New debt: creditor - %s, borrower - %s\n", creditor, this);
        Debt d = new Debt(this, creditor, 0.0);
        debts.add(d);
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return login.equals(account.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return name;
    }

    private String enterName(BufferedReader reader){
        String name = null;
        try {
            System.out.println("Enter your name: ");
            name = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
}

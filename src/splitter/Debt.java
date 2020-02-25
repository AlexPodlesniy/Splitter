package splitter;

import java.io.Serializable;

public class Debt implements Serializable {
    private static final long serialVersionUID = 1234234234234L;
    private Account borrower;
    private Account creditor;
    private double value;

    public Debt(Account borrower, Account creditor, double value) {
        this.borrower = borrower;
        this.creditor = creditor;
        this.value = value;
    }

    public Account getBorrower() {
        return borrower;
    }

    public Account getCreditor() {
        return creditor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double changeDebt(double payment) {
        value += payment;

        Debt creditorsDebt = creditor.findDebtByCreditor(borrower);
        creditorsDebt.setValue(value * -1);

        return value;
    }

    public void repayDebt() {
        value = 0.0;
        creditor.findDebtByCreditor(borrower).setValue(0.0);
    }

    @Override
    public String toString() {
        return "\nYou owes " + creditor + " " + value + " simoleons";
    }
}
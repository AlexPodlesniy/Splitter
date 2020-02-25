package splitter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Expense implements Serializable {
    private static final long serialVersionUID = 123123123234234L;
    private String description;
    private String date;
    private Account payer;
    private double value;
    private ArrayList<Debt> debts;


    public Expense(String description, Account payer, double value, ArrayList<Debt> debts) {
        this.description = description;
        this.payer = payer;
        this.value = value;
        this.debts = debts;
        this.date = getDate();
    }

    public String getDescription() {
        return description;
    }

    public Account getPayer() {
        return payer;
    }

    public double getValue() {
        return value;
    }

    public ArrayList<Debt> getDebts() {
        return debts;
    }


    @Override
    public String toString() {

        return date + " " + description + "\n" +
                "\tpayer = " + payer + ", value = " + value + "\n" +
                debts;
    }

    public void printExpense() {
        System.out.println("\n" + date + " " + description + "\n" +
                "\tpayer = " + payer + ", value = " + value);
        for (Debt debt : debts) {
            Account borrower = debt.getBorrower();
            double value = debt.getValue();
            System.out.printf("\t%s owes %.2f simoleons\n", borrower, value);
        }
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yyyy");
        String date = sdf.format(new Date());
        return date;
    }


}

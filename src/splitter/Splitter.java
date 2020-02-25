package splitter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Alexander Podlesiy
 * @version 1.0
 */

public class Splitter implements Serializable {
    private static final long serialVersionUID = 123123123123123L;
    private HashSet<Account> accounts = new HashSet<>(5);
    private ArrayList<Expense> expenses = new ArrayList<>(20);
    private Account user;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Splitter splitter = null;
        try {
            FileInputStream fis = new FileInputStream("splitter.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            splitter = (Splitter)ois.readObject();
        } catch (FileNotFoundException e) {
            splitter = new Splitter();
            try {
                System.out.print("If u want to fill splitter with test accounts press \"1\", else press any other character: ");
                String testingFlag = reader.readLine();
                if (testingFlag.equals("1")) {
                    splitter.fillWithTestAccounts();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (splitter == null) {
            splitter = new Splitter();
        }

        splitter.launch(reader);

    }

    public void fillWithTestAccounts() {
        accounts.add(new Account("Alex", "1", "Alex"));
        accounts.add(new Account("Kate", "2", "Kate"));
        accounts.add(new Account("Lena", "3", "Lena"));
        accounts.add(new Account("John", "4", "John"));
        accounts.add(new Account("Rick", "5", "Rick"));
        System.out.println("Test accounts:");
        for (Account acc : accounts) {
            System.out.printf("login: %s   password:%s\n", acc.getLogin(), acc.getPassword());
        }
    }

    public void launch(BufferedReader reader) {
        user = authorize(reader);
        printHello();
        openMainMenu(reader);
    }

    private void openMainMenu(BufferedReader reader) {
        printMainMenu();
        takeAnAction(reader);
    }

    private Account authorize(BufferedReader reader) {

        String login;
        String password;

        try {
            System.out.print("If you want to create new account press \"1\", else press anny other character: ");
            String registerFlag = reader.readLine();
            if (registerFlag.equals("1")) {
                return register(reader);
            }

            System.out.print("login: ");
            login = reader.readLine();
            Account user = searchByLogin(login);

            while(true) {
                System.out.print("password: ");
                password = reader.readLine();
                if (password.equals(user.getPassword()))
                    break;
                else {
                    System.out.print("Wrong password, if you want to try again enter \"y\", else enter any other character: ");
                    if (!reader.readLine().toLowerCase().equals("y")) {
                        return authorize(reader);
                    }
                }
            }
            return user;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (AuthorizationException e) {
            System.out.println(e.getMessage());
            return authorize(reader);
        }

    }

    private Account register (BufferedReader reader) throws IOException {
        String login;
        String password;
        System.out.print("login: ");
        login = reader.readLine();
        System.out.print("password: ");
        password = reader.readLine();
        Account acc = new Account(login, password, reader);
        accounts.add(acc);
        return acc;
    }

    private Account searchByLogin(String login) throws AuthorizationException {
        for (Account acc : accounts) {
            if (login.equals(acc.getLogin()))
                return acc;
        }
        throw new AuthorizationException("Account doesn't exist");
    }

    private void printHello() {
        System.out.print("\nHello, " + user.getName() + "!");
    }

    private void printMainMenu() {
        System.out.println("\nChoose option:\n");
        System.out.println("Selection\tOption");
        printMany("-", 40);
        System.out.println( "\t1\t\tCheck your balance\n" +
                "\t2\t\tAdd an expense\n" +
                "\t3\t\tSettle up\n" +
                "\t4\t\tHistory\n" +
                "\t5\t\tExit");
        System.out.print("\nType selection number: ");
    }

    private void printMany(String s, int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(s);
        }
        System.out.print("\n");
    }

    private void takeAnAction(BufferedReader reader) {
        try {
            int selection = Integer.parseInt(reader.readLine());
            switch (selection) {
                case 1: {
                    printBalance(user);
                    break;
                }
                case 2: {
                    try {
                        addAnExpense(reader);
                        recalculateDebts();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3: {
                    settleUp(reader);
                    break;
                }
                case 4: {
                    printHistory();
                    break;
                }
                case 5:
                    exit();
                    return;
                default: {
                    System.out.print("Unrecognized option, try again: ");
                    takeAnAction(reader);
                }
            }
            openMainMenu(reader);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.print("Character isn't a number, try again: ");
            takeAnAction(reader);
        }
    }

    private static void printBalance(Account user) {
        if (user.getDebts().isEmpty()) {
            System.out.println("You have no debts.");
        }
        else {
            for (Debt debt : user.getDebts()) {
                System.out.printf("You owe %s %.2f simoleons\n", debt.getCreditor(), debt.getValue());
            }
        }
    }

    //Добавляет трату в историю, и расчитывает займы
    private void addAnExpense(BufferedReader reader) throws IOException {
        String description;
        Account payer = user;
        double value;
        ArrayList<Debt> debts;

        System.out.print("Description: ");
        description = reader.readLine();
        System.out.print("Value: ");
        value = readDouble(reader);
        debts = getDebts(reader, value);
        expenses.add(new Expense(description, payer, value, debts));

        calculateDebts(debts);
    }

    //ввод неотрицательного вещественного числа
    private double readDouble(BufferedReader reader) throws IOException{
        double result;
        try {
            result = Double.parseDouble(reader.readLine());
        } catch (NumberFormatException e) {
            System.out.print("It isn't a number, try again: ");
            return readDouble(reader);
        }
        if (result < 0.0) {
            System.out.print("Amount can't be less than 0, try again: ");
            return readDouble(reader);
        }
        return result;
    }

    //Возвращает лист займов
    private ArrayList<Debt> getDebts(BufferedReader reader, double value) throws IOException {
        ArrayList<Account> borrowers;
        ArrayList<Account> accountList = new ArrayList<>(accounts);

        //определяем пользователей, учавствующих в трате
        System.out.print("If all members are involved in expense type \"1\", else type any other character: ");
        String choosingAccountsFlag = reader.readLine();
        if (choosingAccountsFlag.equals("1")) {
            borrowers = accountList;
        } else {
            printBorrowersMenu(accountList);
            borrowers = getBorrowers(accountList, reader);
        }
        System.out.println("Involved persons: " + borrowers);



        return formDebtsList(borrowers, value, reader);
    }

    //распечатывает меню выбора заемщиков
    private void printBorrowersMenu(ArrayList<Account> accountList) {
        System.out.println("Choose involved in expense users:");
        System.out.println("Selection\tName");
        printMany("-", 40);
        for (int i = 0; i < accountList.size(); i++) {
            System.out.printf("\t%d\t\t%s\n", i + 1, accountList.get(i).getName());
        }
        System.out.print("\nTo add a borrower type his selection number. If u want to add several users, add them one at a time.\nSelection number: ");
    }

    //Возвращает лист аккаунтов, учавствующих в трате
    private ArrayList<Account> getBorrowers(ArrayList<Account> accountList, BufferedReader reader) throws IOException{
        ArrayList<Account> borrowers = new ArrayList<>(accountList.size());
        while(true) {
            String selection = reader.readLine();
            if (selection.equals("")) {
                break;
            }
            int selectionNumber;
            try {
                selectionNumber = Integer.parseInt(selection);
                int index = selectionNumber - 1;
                if (index >= 0 && index < accountList.size()) {
                    Account borrower = accountList.get(index);
                    if (borrowers.contains(borrower)) {
                        System.out.println(borrower.getName() + " has been added already");
                    }
                    else {
                        borrowers.add(borrower);
                        System.out.printf("%s is added\n", borrower.getName());
                    }
                }
                else {
                    System.out.println("Wrong number");
                }
            } catch (NumberFormatException e) {
                System.out.println("It isn't a number");
            }
            System.out.print("If u want to add someone else, type his selection number, else press <enter>: ");

        }
        return borrowers;
    }

    //Возвращает лист займов
    private ArrayList<Debt> formDebtsList (ArrayList<Account> borrowers, double value, BufferedReader reader) throws IOException {
        ArrayList<Debt> debts = new ArrayList<>(borrowers.size());
        double totalAmount = value;
        System.out.print("If you want to split expense equally type \"1\", or type any other character for splitting by exact amounts: ");
        String equalSplittingFlag = reader.readLine();
        if (equalSplittingFlag.equals("1")) {
            double amount = value / borrowers.size();
            for (Account acc : borrowers) {
                if (acc != user) {
                    debts.add(new Debt(acc, user, amount));
                }
            }
        } else {
            System.out.println("Specify how much each person owes.");
            for (Account acc : borrowers) {
                System.out.printf("%.2f simoleons left\n", totalAmount);
                System.out.printf("%s: ", acc.getName());
                double amount = readDouble(reader);
                totalAmount -= amount;
                if (acc != user) {
                    debts.add(new Debt(acc, user, amount));
                }
            }
            if (!equalsEnough(totalAmount, 0.0)) {
                System.out.printf("The amounts do not add up to the total cost of %.2f simoleons. " +
                        "You are short by %.2f simoleons.\nTry again.\n", value, totalAmount);
                return formDebtsList(borrowers, value, reader);
            }
        }
        printDebtsOfExpanse(debts);
        return debts;
    }

    private boolean equalsEnough (double a, double b) {
        return (a - b) * (a - b) < 0.01;
    }

    private void printDebtsOfExpanse(ArrayList<Debt> debts) {
        System.out.println("Payer: " + user.getName());
        for (Debt debt: debts) {
            System.out.printf(debt.getBorrower().getName() + " owe %.2f simoleons\n", debt.getValue());
        }
    }

    //Редактирует балансы аккаунтов после совместных трат
    private void calculateDebts(ArrayList<Debt> srcDebts) {
        for (Debt debt : srcDebts) {

            //Эти строки - попытка сделать код понятнее
            Account borrower = debt.getBorrower();
            Account creditor = debt.getCreditor();
            double value = debt.getValue();
            //------------------------------------

            borrower.findDebtByCreditor(creditor).changeDebt(value);
        }
    }

    //Пересчитывает долги, делегирует то, что вы должны тем, кто должен вам
    private void recalculateDebts() {
        //метод выглядит жутко неэффективно, возможно когда-нибудь я его переделаю
        for (Account acc : accounts) {
            for (Debt srcDebt : acc.getDebts()) {
                if (equalsEnough(srcDebt.getValue(), 0.0))
                    continue;
                else if (srcDebt.getValue() > 0.0) {
                    for (Debt dstDebt : acc.getDebts()) {
                        if (equalsEnough(dstDebt.getValue(), 0.0))
                            continue;
                        else if (dstDebt.getValue() < 0.0) {
                            if (equalsEnough(delegateDebt(srcDebt, dstDebt), 0.0)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private double delegateDebt (Debt srcDebt, Debt dstDebt) {
        if (srcDebt.getValue() <= abs(dstDebt.getValue())) {
            dstDebt.changeDebt(srcDebt.getValue());
            dstDebt.getCreditor().findDebtByCreditor(srcDebt.getCreditor()).changeDebt(srcDebt.getValue());
            srcDebt.repayDebt();
        }
        else {
            srcDebt.changeDebt(dstDebt.getValue());
            dstDebt.getCreditor().findDebtByCreditor(srcDebt.getCreditor()).changeDebt(dstDebt.getValue() * -1);
            dstDebt.repayDebt();
        }
        return srcDebt.getValue();
    }

    private double abs(double x) {
        return (x < 0.0 ? x * -1 : x);
    }

    //Выплатить долг
    private void settleUp(BufferedReader reader) throws IOException{
        System.out.println("\nSelect creditor:");
        System.out.println("Selection\tCreditor\tDebt");
        printMany("-", 50);
        System.out.println("\t0\t\tExit");
        for(int i = 0; i < user.getDebts().size(); i++) {
            Debt debt = user.getDebts().get(i);
            Account creditor = debt.getCreditor();
            System.out.printf("\t%d\t\t%s\t\t%s\n", i+1, creditor.getName(), debt.getValue());
        }
        System.out.print("\nType selection number: ");
        int index;
        while(true) {
            try {
                index = Integer.parseInt(reader.readLine()) - 1;
                if (index == -1) {
                    return;
                } else if (index >= 0 && index < user.getDebts().size())
                    break;
                System.out.print("Wrong selection number, try again: ");
            } catch (NumberFormatException e) {
                System.out.print("It isn't a number, try again: ");
            }
        }
        Debt debt = user.getDebts().get(index);
        if (debt.getValue() <= 0.0)
            System.out.println("You don't owe " + debt.getCreditor());
        else {
            getPayment(debt, reader);
        }

    }

    //Редактирует долги аккаунтов, возвращает оставшуюся сумму долга
    private double getPayment(Debt debt, BufferedReader reader) throws IOException{
        Account creditor = debt.getCreditor();
        Account borrower = debt.getBorrower();
        System.out.print("If you want to repay debt completely type\"1\", else type any other character: ");
        String completeRepaymentFlag = reader.readLine();
        if (completeRepaymentFlag.equals("1")) {
            debt.repayDebt();
        } else {
            System.out.printf("Enter amount of repayment (you owe %.2f simoleons): ", debt.getValue());
            try {
                double repayment = Double.parseDouble(reader.readLine());
                if (equalsEnough(repayment ,debt.getValue())) {
                    debt.repayDebt();
                }
                else {
                    debt.changeDebt(repayment * -1);
                }
            } catch (NumberFormatException e) {
                System.out.println("It isn't a number. Try again.");
                return getPayment(debt, reader);
            }
        }
        System.out.printf("Done. Now you owe %s %.2f", creditor, debt.getValue());
        return debt.getValue();
    }

    //распечатывает все траты
    private void printHistory() {
        for (Expense expense : expenses) {
            expense.printExpense();
        }
    }

    private void exit() throws IOException {
        FileOutputStream fos = new FileOutputStream("splitter.bin");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

}

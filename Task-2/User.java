package task2;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final Portfolio portfolio;
    private final List<Transaction> transactions = new ArrayList<>();

    public User(String name, double initialCash) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.portfolio = new Portfolio(initialCash);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Portfolio getPortfolio() { return portfolio; }
    public List<Transaction> getTransactions() { return transactions; }

    public void recordTransaction(Transaction tx) {
        transactions.add(tx);
    }

    @Override
    public String toString() {
        return String.format("User %s (id=%s) %s", name, id, portfolio.toString());
    }
}


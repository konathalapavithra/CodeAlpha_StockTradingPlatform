package task2;



import java.util.ArrayList;
import java.util.List;

public class TradingEngine {
    private final Market market;
    private final List<User> users = new ArrayList<>();

    public TradingEngine(Market market) {
        this.market = market;
    }

    public Market getMarket() { return market; }

    public void addUser(User u) { users.add(u); }

    public User findUserByName(String name) {
        for (User u : users) if (u.getName().equalsIgnoreCase(name)) return u;
        return null;
    }

    public boolean buy(User user, String symbol, int quantity) {
        Stock s = market.getStock(symbol);
        if (s == null || quantity <= 0) return false;
        double total = s.getPrice() * quantity;
        if (user.getPortfolio().getCash() < total) return false;
        // proceed
        user.getPortfolio().withdraw(total);
        user.getPortfolio().addShares(symbol, quantity);
        Transaction tx = new Transaction(Transaction.Type.BUY, symbol, quantity, s.getPrice());
        user.recordTransaction(tx);
        return true;
    }

    public boolean sell(User user, String symbol, int quantity) {
        Stock s = market.getStock(symbol);
        if (s == null || quantity <= 0) return false;
        if (user.getPortfolio().getQuantity(symbol) < quantity) return false;
        double total = s.getPrice() * quantity;
        user.getPortfolio().addShares(symbol, -quantity); // won't work, so use removeShares
        // remove properly
        boolean removed = user.getPortfolio().removeShares(symbol, quantity);
        if (!removed) return false;
        user.getPortfolio().deposit(total);
        Transaction tx = new Transaction(Transaction.Type.SELL, symbol, quantity, s.getPrice());
        user.recordTransaction(tx);
        return true;
    }

    public List<User> getUsers() { return users; }
}

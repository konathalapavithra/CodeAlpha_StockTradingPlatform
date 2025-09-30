package task2;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> holdings = new HashMap<>();
    private double cash; // available cash balance

    public Portfolio(double initialCash) {
        this.cash = initialCash;
    }

    public double getCash() { return cash; }
    public void deposit(double amount) { cash += amount; }
    public boolean withdraw(double amount) {
        if (amount <= cash) { cash -= amount; return true; }
        return false;
    }

    public Map<String, Integer> getHoldings() {
        return Collections.unmodifiableMap(holdings);
    }

    public int getQuantity(String symbol) {
        return holdings.getOrDefault(symbol.toUpperCase(), 0);
    }

    public void addShares(String symbol, int qty) {
        symbol = symbol.toUpperCase();
        holdings.put(symbol, getQuantity(symbol) + qty);
    }

    public boolean removeShares(String symbol, int qty) {
        symbol = symbol.toUpperCase();
        int have = getQuantity(symbol);
        if (qty > have) return false;
        if (qty == have) holdings.remove(symbol);
        else holdings.put(symbol, have - qty);
        return true;
    }

    public double portfolioValue(Market market) {
        double total = cash;
        for (Map.Entry<String, Integer> e : holdings.entrySet()) {
            Stock s = market.getStock(e.getKey());
            if (s != null) total += s.getPrice() * e.getValue();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Cash: ₹%.2f, Holdings: %s", cash, holdings.toString());
    }
}

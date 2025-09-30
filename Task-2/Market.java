package task2;



import java.io.Serializable;
import java.util.*;

public class Market implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random rnd = new Random();

    public Market() {
        // seed with some sample stocks (symbols and starting prices)
        addStock(new Stock("INFY", "Infosys", 1500));
        addStock(new Stock("TCS", "Tata Consultancy Services", 3300));
        addStock(new Stock("RELI", "Reliance Industries", 2300));
        addStock(new Stock("HDFCBANK", "HDFC Bank", 1400));
        addStock(new Stock("ITC", "ITC Ltd", 350));
    }

    public void addStock(Stock s) {
        stocks.put(s.getSymbol(), s);
    }

    public Stock getStock(String symbol) {
        if (symbol == null) return null;
        return stocks.get(symbol.toUpperCase());
    }

    public List<Stock> listStocks() {
        return new ArrayList<>(stocks.values());
    }

    // simple simulation: randomly change prices by -3% .. +3%
    public void tick() {
        for (Stock s : stocks.values()) {
            double changePct = (rnd.nextDouble() * 6.0) - 3.0; // -3..+3
            double newPrice = s.getPrice() * (1.0 + changePct / 100.0);
            if (newPrice < 1.0) newPrice = 1.0;
            s.setPrice(Math.round(newPrice * 100.0) / 100.0);
        }
    }

    // update market some times
    public void simulateTicks(int n) {
        for (int i = 0; i < n; i++) tick();
    }
}

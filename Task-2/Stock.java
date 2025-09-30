package task2;



import java.io.Serializable;

public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String symbol;
    private final String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("%s (%s): ₹%.2f", name, symbol, price);
    }
}

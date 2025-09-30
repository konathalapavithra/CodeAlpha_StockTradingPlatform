package task2;



import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { BUY, SELL }

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double pricePerShare;
    private final LocalDateTime timestamp;

    public Transaction(Type type, String symbol, int quantity, double pricePerShare) {
        this.type = type;
        this.symbol = symbol.toUpperCase();
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s %d x %s @ ₹%.2f (time: %s)",
                type,
                symbol,
                quantity,
                symbol,
                pricePerShare,
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}

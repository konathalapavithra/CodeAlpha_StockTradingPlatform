package task2;



import java.io.File;
import java.util.List;
import java.util.Scanner;

public class StockTradingPlatform  {
    private static final String USERS_FILE = "data/users.dat";
    private static final String MARKET_FILE = "data/market.dat";

    public static void main(String[] args) {
        // ensure data directory
        new File("data").mkdirs();

        Market market;
        TradingEngine engine;

        // load market or create
        market = new Market();
        engine = new TradingEngine(market);

        // load users if exists
        try {
            File f = new File(USERS_FILE);
            if (f.exists()) {
                List<User> loaded = PersistenceUtil.loadUsers(USERS_FILE);
                for (User u : loaded) engine.addUser(u);
                System.out.println("Loaded " + loaded.size() + " users from disk.");
            } else {
                System.out.println("No users persistence found; starting fresh.");
            }
        } catch (Exception e) {
            System.out.println("Failed to load users: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Welcome to Mini Stock Trading Platform ===");

        boolean quit = false;
        User active = null;

        while (!quit) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) List market");
            System.out.println("2) Simulate market ticks");
            System.out.println("3) Create user");
            System.out.println("4) Login as user");
            System.out.println("5) Save data");
            System.out.println("0) Quit");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1":
                    printMarket(engine.getMarket());
                    break;
                case "2":
                    System.out.print("How many ticks to simulate? ");
                    int ticks = readInt(sc, 1);
                    engine.getMarket().simulateTicks(ticks);
                    System.out.println("Simulated " + ticks + " ticks.");
                    break;
                case "3":
                    System.out.print("Enter user name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Initial cash amount (numeric): ");
                    double cash = readDouble(sc, 10000.0);
                    User u = new User(name, cash);
                    engine.addUser(u);
                    System.out.println("User created: " + u.getName());
                    break;
                case "4":
                    System.out.print("Enter user name to login: ");
                    String nm = sc.nextLine().trim();
                    User found = engine.findUserByName(nm);
                    if (found == null) {
                        System.out.println("User not found.");
                    } else {
                        userMenu(engine, found, sc);
                    }
                    break;
                case "5":
                    saveEngine(engine);
                    break;
                case "0":
                    System.out.println("Saving before exit...");
                    saveEngine(engine);
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        sc.close();
        System.out.println("Goodbye!");
    }

    private static void printMarket(Market market) {
        System.out.println("\n--- Market ---");
        for (Stock s : market.listStocks()) {
            System.out.println(s.getSymbol() + " | " + s.getName() + " | ₹" + String.format("%.2f", s.getPrice()));
        }
    }

    private static void userMenu(TradingEngine engine, User user, Scanner sc) {
        System.out.println("\nLogged in as: " + user.getName());
        boolean back = false;
        while (!back) {
            System.out.println("\nUser Menu:");
            System.out.println("1) Show portfolio");
            System.out.println("2) Buy stock");
            System.out.println("3) Sell stock");
            System.out.println("4) Show transactions");
            System.out.println("5) Deposit cash");
            System.out.println("6) Simulate market (tick once)");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    showPortfolio(user, engine.getMarket());
                    break;
                case "2":
                    doBuy(engine, user, sc);
                    break;
                case "3":
                    doSell(engine, user, sc);
                    break;
                case "4":
                    showTransactions(user);
                    break;
                case "5":
                    System.out.print("Amount to deposit: ");
                    double amt = readDouble(sc, 0.0);
                    user.getPortfolio().deposit(amt);
                    System.out.println("Deposited ₹" + String.format("%.2f", amt));
                    break;
                case "6":
                    engine.getMarket().tick();
                    System.out.println("Market ticked once.");
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static void showPortfolio(User user, Market market) {
        System.out.println("\n--- Portfolio for " + user.getName() + " ---");
        System.out.println("Cash: ₹" + String.format("%.2f", user.getPortfolio().getCash()));
        System.out.println("Holdings:");
        if (user.getPortfolio().getHoldings().isEmpty()) {
            System.out.println("  (no holdings)");
        } else {
            for (var e : user.getPortfolio().getHoldings().entrySet()) {
                Stock s = market.getStock(e.getKey());
                String name = s != null ? s.getName() : "Unknown";
                double price = s != null ? s.getPrice() : 0.0;
                System.out.println(String.format("  %s: %d shares | current ₹%.2f | value ₹%.2f", e.getKey(), e.getValue(), price, price * e.getValue()));
            }
        }
        double total = user.getPortfolio().portfolioValue(market);
        System.out.println("Total portfolio value (including cash): ₹" + String.format("%.2f", total));
    }

    private static void doBuy(TradingEngine engine, User user, Scanner sc) {
        System.out.print("Symbol to buy: ");
        String sym = sc.nextLine().trim().toUpperCase();
        Stock s = engine.getMarket().getStock(sym);
        if (s == null) {
            System.out.println("Symbol not found.");
            return;
        }
        System.out.print("Quantity: ");
        int qty = readInt(sc, 1);
        double cost = s.getPrice() * qty;
        if (user.getPortfolio().getCash() < cost) {
            System.out.println("Insufficient cash. Needed ₹" + String.format("%.2f", cost));
            return;
        }
        boolean ok = engine.buy(user, sym, qty);
        if (ok) System.out.println("Bought " + qty + " of " + sym + " at ₹" + s.getPrice());
        else System.out.println("Buy failed.");
    }

    private static void doSell(TradingEngine engine, User user, Scanner sc) {
        System.out.print("Symbol to sell: ");
        String sym = sc.nextLine().trim().toUpperCase();
        Stock s = engine.getMarket().getStock(sym);
        if (s == null) {
            System.out.println("Symbol not found.");
            return;
        }
        System.out.print("Quantity: ");
        int qty = readInt(sc, 1);
        if (user.getPortfolio().getQuantity(sym) < qty) {
            System.out.println("You don't have enough shares.");
            return;
        }
        boolean ok = engine.sell(user, sym, qty);
        if (ok) System.out.println("Sold " + qty + " of " + sym + " at ₹" + s.getPrice());
        else System.out.println("Sell failed.");
    }

    private static void showTransactions(User user) {
        System.out.println("\n--- Transactions ---");
        if (user.getTransactions().isEmpty()) {
            System.out.println("(no transactions)");
            return;
        }
        for (Transaction t : user.getTransactions()) {
            System.out.println(t);
        }
    }

    private static void saveEngine(TradingEngine engine) {
        try {
            PersistenceUtil.saveUsers(engine.getUsers(), USERS_FILE);
            System.out.println("Saved users to " + USERS_FILE);
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    private static int readInt(Scanner sc, int minDefault) {
        try {
            String in = sc.nextLine().trim();
            return Integer.parseInt(in);
        } catch (Exception e) {
            return minDefault;
        }
    }

    private static double readDouble(Scanner sc, double defaultVal) {
        try {
            String in = sc.nextLine().trim();
            return Double.parseDouble(in);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}


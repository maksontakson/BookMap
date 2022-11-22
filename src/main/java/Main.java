import java.io.*;
import java.util.Comparator;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
        Book book = new Book();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("input.txt"))));
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.startsWith("u")) {
                String[] str = line.split(",");
                if (line.endsWith("bid")) {
                    book.addLimitOrderForBid(new Order(Long.parseLong(str[1]), Long.parseLong(str[2]), str[3]));
                } else if (line.endsWith("ask")) {
                    book.addLimitOrderForAsk(new Order(Long.parseLong(str[1]), Long.parseLong(str[2]), str[3]));
                } /*else book.getCustomWriter().write("Incorrect order notation");*/
                book.getOrders().sort((o1, o2) -> (int) (o1.getPrice() - o2.getPrice()));
            }
            if (line.startsWith("q")) {
                if (line.endsWith("bid")) {
                    if (book.hasBids(book.getOrders()))
                        book.getCustomWriter().write(book.getHighestBid());
//                    else
//                        book.getCustomWriter().write("There is no bids in book to print best bid, need to make a limit order");
                } else if (line.endsWith("ask")) {
                    if (book.hasAsks(book.getOrders()))
                        book.getCustomWriter().write(book.getLowestAsk());
//                    else
//                        book.getCustomWriter().write("There is no asks in book to print best ask, need to make a limit order");
                } else /*if (line.matches("[0-9]+"))*/ {
                    String[] str = line.split(",");
                    Long result = book.getSizeByPrice(Long.parseLong(str[2]));
//                    if (result == -1L) book.getCustomWriter().write("There is no orders by this price");
                    /*else*/ book.getCustomWriter().getBufferedWriter().write(result.toString());
                } /*else book.getCustomWriter().write("Incorrect data");*/
            }
            if (line.startsWith("o")) {
                String[] str = line.split(",");
                if (line.contains("buy")) {
                    book.handleMarketOrder(str[1], Long.parseLong(str[2]));
                } else if (line.contains("sell")) {
                    book.handleMarketOrder(str[1], Long.parseLong(str[2]));
                } else book.getCustomWriter().write("Incorrect order notation");
            }
        }
        book.getCustomWriter().getBufferedWriter().flush();
    }


}

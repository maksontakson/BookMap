import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Book {
    private Order lowestAsk;
    private Order highestBid;
    private List<Order> orders;
    private boolean cameFromLimitMethod;
    private final CustomWriter customWriter;

    public CustomWriter getCustomWriter() {
        return customWriter;
    }

    public Book() throws IOException {
        this.orders = new ArrayList<>();
        this.lowestAsk = null;
        this.highestBid = null;
        this.customWriter = new CustomWriter();
    }

    public void addLimitOrderForBid(Order order) {
        if(order.getSize() == 0) {
//            customWriter.write("Size can not be 0, make new order correctly");
            return;
        }
        if (orders.size() == 0 && highestBid == null && lowestAsk == null) {
            highestBid = order;
            orders.add(order);
        } else if (highestBid != null && lowestAsk == null) {
            if (order.getPrice() <= highestBid.getPrice()) {
                if (order.getPrice() == highestBid.getPrice())
                    highestBid.setSize(highestBid.getSize() + order.getSize());
                if (order.getPrice() < highestBid.getPrice()) {
                    if (bidsContainsPrice(order.getPrice())) {
                        for (Order ord : orders) {
                            if (ord.getPrice() == order.getPrice() && ord.getAction().equals(order.getAction())) {
                                ord.setSize(order.getSize() + ord.getSize());
                                break;
                            }
                        }
                    } else orders.add(order);
                }
            } else if (order.getPrice() > highestBid.getPrice()) {
                highestBid = order;
                orders.add(order);
            }
        } else if (highestBid == null && lowestAsk != null) {
            highestBid = order;
            if (order.getPrice() < lowestAsk.getPrice()) {
                highestBid = order;
                orders.add(order);
            } else if (order.getPrice() >= lowestAsk.getPrice()) {
                cameFromLimitMethod = true;
                handleMarketOrder("buy", order.getSize());
            }
        } else if (highestBid != null) {
            if (order.getPrice() <= highestBid.getPrice()) {
                if (order.getPrice() == highestBid.getPrice())
                    highestBid.setSize(highestBid.getSize() + order.getSize());
                if (order.getPrice() < highestBid.getPrice()) {
                    if (asksContainsPrice(order.getPrice())) {
                        for (Order ord : orders) {
                            if (ord.getPrice() == order.getPrice() && ord.getAction().equals(order.getAction())) {
                                ord.setSize(order.getSize() + ord.getSize());
                                break;
                            }
                        }
                    } else orders.add(order);
                }
            } else if (order.getPrice() > highestBid.getPrice() && order.getPrice() < lowestAsk.getPrice()) {
                highestBid = order;
                orders.add(order);
            } else if (order.getPrice() >= lowestAsk.getPrice()) {
                highestBid = order;
                cameFromLimitMethod = true;
                handleMarketOrder("buy", order.getSize());
            }
        }
    }

    public void addLimitOrderForAsk(Order order) {
        if(order.getSize() == 0) {
//            customWriter.write("Size can not be 0, make new order correctly");
            return;
        }
        if (orders.size() == 0 && highestBid == null && lowestAsk == null) {
            lowestAsk = order;
            orders.add(order);
        } else if (highestBid == null && lowestAsk != null) {
            if (order.getPrice() >= lowestAsk.getPrice()) {
                if (order.getPrice() == lowestAsk.getPrice())
                    lowestAsk.setSize(lowestAsk.getSize() + order.getSize());
                if(order.getPrice() > lowestAsk.getPrice()) {
                    if(asksContainsPrice(order.getPrice())) {
                        for (Order ord : orders) {
                            if (ord.getPrice() == order.getPrice() && ord.getAction().equals(order.getAction())) {
                                ord.setSize(order.getSize() + ord.getSize());
                                break;
                            }
                        }
                    } else orders.add(order);
                }
            } else if (order.getPrice() < lowestAsk.getPrice()) {
                lowestAsk = order;
                orders.add(order);
            }
        } else if (highestBid != null && lowestAsk == null) {
            lowestAsk = order;
            if (order.getPrice() > highestBid.getPrice()) {
                lowestAsk = order;
                orders.add(order);
            } else if (order.getPrice() <= highestBid.getPrice()) {
                cameFromLimitMethod = true;
                handleMarketOrder("sell", order.getSize());
            }
        } else if (highestBid != null) {
            if (order.getPrice() >= lowestAsk.getPrice()) {
                if (order.getPrice() == lowestAsk.getPrice())
                    lowestAsk.setSize(lowestAsk.getSize() + order.getSize());
                if(order.getPrice() > lowestAsk.getPrice()) {
                    if(asksContainsPrice(order.getPrice())) {
                        for (Order ord : orders) {
                            if (ord.getPrice() == order.getPrice() && ord.getAction().equals(order.getAction())) {
                                ord.setSize(order.getSize() + ord.getSize());
                                break;
                            }
                        }
                    } else orders.add(order);
                }
            } else if (order.getPrice() > highestBid.getPrice() && order.getPrice() < lowestAsk.getPrice()) {
                lowestAsk = order;
                orders.add(order);
            } else if (order.getPrice() <= highestBid.getPrice()) {
                lowestAsk = order;
                cameFromLimitMethod = true;
                handleMarketOrder("sell", order.getSize());
            }
        }
    }

    public void handleMarketOrder(String action, Long size) {
        Long startedSize = size;
        if (action.equals("sell")) {
            if(orders.size() == 0) {
//                customWriter.write("Book is empty, try to make limit order");
                return;
            }
            if(hasBids(orders)) {
                if(size < highestBid.getSize()) highestBid.setSize(highestBid.getSize() - size);
                else if(size == highestBid.getSize()) orders.remove(highestBid);
                else if(size > highestBid.getSize()) {
                    List<Order> ordersToRemove = new ArrayList<>();
                    List<Order> listOfBids = getListOfBids(orders);
                    Collections.reverse(listOfBids);
                    for (Order order : listOfBids) {
                        if (cameFromLimitMethod) {
                            if (lowestAsk.getPrice() > highestBid.getPrice()) break;
                        }
                        if (order.getSize() < size) {
                            size = size - order.getSize();
                            ordersToRemove.add(order);
                            int index = getListOfBids(orders).indexOf(order);
                            if(getListOfBids(orders).size() > index + 1)
                                highestBid = getListOfBids(orders).get(index + 1);
                        } else if (order.getSize() == size) {
                            ordersToRemove.add(order);
                            size = 0L;
                            break;
                        } else {
                            order.setSize(order.getSize() - size);
                            size = 0L;
                            break;
                        }
                    }
                    orders.removeAll(ordersToRemove);
                    if(size != 0 && cameFromLimitMethod) {
                        orders.add(new Order(lowestAsk.getPrice(), size, "ask"));
                    }/*else if (size != 0) {
                        customWriter.write("Sold: " + (startedSize - size) + " coins. But there is no more bids in book");
                    }*/
                    cameFromLimitMethod = false;
                }
            } /*else customWriter.write("There is no bids in book to sell using MarketOrder");*/
        }

        if (action.equals("buy")) {
            if(orders.size() == 0) {
//                customWriter.write("Book is empty, try to make limit order");
                return;
            }
            if(hasAsks(orders)) {
                if(size < lowestAsk.getSize()) lowestAsk.setSize(lowestAsk.getSize() - size);
                else if(size == lowestAsk.getSize()) orders.remove(lowestAsk);
                else if(size > lowestAsk.getSize()) {
                    List<Order> ordersToRemove = new ArrayList<>();
                    for (Order order : getListOfAsks(orders)) {
                        if(cameFromLimitMethod) {
                            if (highestBid.getPrice() < lowestAsk.getPrice()) break;
                        }
                            if (order.getSize() < size) {
                                size = size - order.getSize();
                                ordersToRemove.add(order);
                                int index = getListOfAsks(orders).indexOf(order);
                                if(getListOfAsks(orders).size() > index + 1)
                                    lowestAsk = getListOfAsks(orders).get(index + 1);
                            } else if (order.getSize() == size) {
                                ordersToRemove.add(order);
                                size = 0L;
                                break;
                            } else {
                                order.setSize(order.getSize() - size);
                                size = 0L;
                                break;
                            }
                    }
                    orders.removeAll(ordersToRemove);
                    if(size != 0 && cameFromLimitMethod) {
                        orders.add(new Order(highestBid.getPrice(), size, "bid"));
                    } /*else if (size != 0) {
                        customWriter.write("Bought: " + (startedSize - size) + " coins. But there is no more asks in book");
                    }*/
                    cameFromLimitMethod = false;
                }
            } /*else customWriter.write("There is no asks in book to buy using MarketOrder");*/
        }
        if (hasAsks(orders))
            lowestAsk = getListOfAsks(orders).get(0);
        else lowestAsk = null;
        if (hasBids(orders)) {
            List<Order> list = getListOfBids(orders);
            Collections.reverse(list);
            highestBid = list.get(0);
        } else highestBid = null;
    }

    private List<Order> getListOfOrdersBeginningFromTheOrder(Order order) {
        List<Order> list = new ArrayList<>();
        boolean copyAllBeginningFromThisOrder = false;
        if(order.getAction().equals("ask")) {
            for(Order ord : getListOfAsks(orders)) {
                if(copyAllBeginningFromThisOrder)
                    list.add(ord);
                if(ord.getPrice() == order.getPrice())
                    copyAllBeginningFromThisOrder = true;
            }
        } else if(order.getAction().equals("bid")) {
            List<Order> reversedListOfBids = getListOfBids(orders);
            Collections.reverse(reversedListOfBids);
            for(Order ord : reversedListOfBids) {
                if(copyAllBeginningFromThisOrder)
                    list.add(ord);
                if(ord.getPrice() == order.getPrice())
                    copyAllBeginningFromThisOrder = true;
            }
        } /*else throw new RuntimeException("Incorrect type of orders`s action");*/
        return list;
    }

    private boolean asksContainsPrice(Long price) {
        boolean contains = false;
        for(Order order : getListOfAsks(orders)) {
            if (order.getPrice() == price) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private boolean bidsContainsPrice(Long price) {
        boolean contains = false;
        for(Order order : getListOfBids(orders)) {
            if (order.getPrice() == price) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public Long getSizeByPrice(Long price) {
        for (Order order : orders) {
            if(order.getPrice() == price)
                return order.getSize();
        }
        return -1L;
    }

    private List<Order> getListOfBids(List<Order> orders) {
        List<Order> listOfBids = new ArrayList<>();
        for (Order order : orders) {
            if (order.getAction().equals("bid")) {
                listOfBids.add(order);
            }
        }
        return listOfBids;
    }

    private List<Order> getListOfAsks(List<Order> orders) {
        List<Order> listOfAsks = new ArrayList<>();
        for (Order order : orders) {
            if (order.getAction().equals("ask")) {
                listOfAsks.add(order);
            }
        }
        return listOfAsks;
    }

    public boolean hasBids(List<Order> orders) {
        boolean hasBids = false;
        for (Order order : orders) {
            if (order.getAction().equals("bid")) {
                hasBids = true;
                break;
            }
        }
        return hasBids;
    }

    public boolean hasAsks(List<Order> orders) {
        boolean hasAsks = false;
        for (Order order : orders) {
            if (order.getAction().equals("ask")) {
                hasAsks = true;
                break;
            }
        }
        return hasAsks;
    }


    public void printBestBid() {
        System.out.println(highestBid);
    }

    public void printBestAsk() {
        System.out.println(lowestAsk);
    }


    public String getLowestAsk() {
        Order order = lowestAsk;
        return order.getPrice().toString() + "," + order.getSize().toString();
    }

    public void setLowestAsk(Order lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public String getHighestBid() {
        Order order = highestBid;
        return order.getPrice().toString() + "," + order.getSize().toString();
    }

    public void setHighestBid(Order highestBid) {
        this.highestBid = highestBid;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(lowestAsk, book.lowestAsk) && Objects.equals(highestBid, book.highestBid) && Objects.equals(orders, book.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowestAsk, highestBid, orders);
    }

    @Override
    public String toString() {
        return "Book{" +
                "lowestAsk=" + lowestAsk +
                ", highestBid=" + highestBid +
                ", orders=" + orders +
                '}';
    }
}

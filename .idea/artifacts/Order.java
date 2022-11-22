import java.util.Objects;

public class Order {
    private Long price;
    private Long size;
    private String action;

    public Order() {
    }

    public Order(Long price, Long size, String action) {
        this.price = price;
        this.size = size;
        this.action = action;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(price, order.price) && Objects.equals(size, order.size) && Objects.equals(action, order.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, size, action);
    }

    @Override
    public String toString() {
        return "Order{" +
                "price=" + price +
                ", size=" + size +
                ", action='" + action + '\'' +
                '}';
    }
}

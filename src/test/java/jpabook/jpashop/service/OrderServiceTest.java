package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderService orderService;

    @Test
    void 상품주문() {

        Member member = createMember();

        Book book = createBook("bookA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, order.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, order.getOrderItems().size(), "주문한 상품 종류 수는 1");
        Assertions.assertEquals(order.getTotalPrice(), book.getPrice() * orderCount, "주문 가격은 가격 * 수량이다.");
        Assertions.assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test
    void 상품주문_재고수량초과() {

        //given
        int itemQuantity = 10;

        Member member = createMember();
        memberRepository.save(createMember());

        Book book = createBook("bookA", 10000, itemQuantity);
        itemRepository.save(book);

        //then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), itemQuantity + 1));
    }

    @Test
    void 주문_취소() {

        //given
        Member member = createMember();
        memberRepository.save(createMember());
        Book book = createBook("bookA", 10000, 10);
        itemRepository.save(book);
        Order order = Order.createOrder(member, new Delivery(), OrderItem.createOrderItem(book, book.getPrice(), 2));
        orderRepository.save(order);

        //when
        orderService.cancelOrder(order.getId());

        //then
        Assertions.assertEquals(OrderStatus.CANCEL, order.getStatus());
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        itemRepository.save(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("memberA");
        member.setAddress(new Address("서울", "강가", "123-123"));
        memberRepository.save(member);
        return member;
    }
}
package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            Member member1 = createMember("홍길동", "서울시", "1길", "12334");
            em.persist(member1);

            Member member2 = createMember("둘리", "부산시", "3길", "555");
            em.persist(member2);

            Book book1 = createBook("JPA1 BOOK", 10000);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
            OrderItem orderItem3 = OrderItem.createOrderItem(book1, 10000, 5);
            OrderItem orderItem4 = OrderItem.createOrderItem(book2, 20000, 9);

            Delivery delivery1= createDelivery(member1);
            Order order1 = Order.createOrder(member1, delivery1, orderItem1, orderItem2);
            em.persist(order1);

            Delivery delivery2 = createDelivery(member2);
            Order order2 = Order.createOrder(member2, delivery2, orderItem3, orderItem4);
            em.persist(order2);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String JPA1_BOOK, int price) {
            Book book1 = new Book();
            book1.setName(JPA1_BOOK);
            book1.setPrice(price);
            book1.setStockQuantity(100);
            return book1;
        }

        private Member createMember(String name, String region, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(region, street, zipcode));
            return member;
        }
    }
}
package adultdinosaurdooley.threesixnine.order.entity;

import adultdinosaurdooley.threesixnine.user.entity.UserEntity;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @CreatedDate
    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;  // 구매날짜


    @OneToOne(mappedBy = "orderEntity", cascade =  CascadeType.REMOVE, fetch = FetchType.LAZY)
    private DeliveryInformation deliveryInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetailEntity> orderDetailEntityList = new ArrayList<>();




    // orderDetailList에 주문 상품 정보를 담아준다.
    public void addOrderDetail(OrderDetailEntity orderDetailEntity){
        orderDetailEntityList.add(orderDetailEntity);
        orderDetailEntity.setOrderEntity(this);
    }

    public static OrderEntity createOrder(List<OrderDetailEntity> orderDetailEntityList, UserEntity userEntity){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserEntity(userEntity);      // 상품을 주문한 회원정보 가져오기
        for(OrderDetailEntity orderDetailEntity : orderDetailEntityList){
            orderEntity.addOrderDetail(orderDetailEntity);
        }
        orderEntity.setOrderedAt(LocalDateTime.now());
        return orderEntity;
    }

}

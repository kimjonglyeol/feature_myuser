package adultdinosaurdooley.threesixnine.admin.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Table(name = "stock")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //일대일 매핑 -> 즉시로딩을 기본 Fetch전략으로 설정
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "stock_amount",nullable = false)
    private int stockAmount; //상품 재고

    @Column(name = "sell_amount",nullable = false)
    private int sellAmount; //판매 수량


}

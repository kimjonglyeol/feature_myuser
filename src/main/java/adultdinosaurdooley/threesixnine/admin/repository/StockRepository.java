package adultdinosaurdooley.threesixnine.admin.repository;


import adultdinosaurdooley.threesixnine.product.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity,Long> {
}

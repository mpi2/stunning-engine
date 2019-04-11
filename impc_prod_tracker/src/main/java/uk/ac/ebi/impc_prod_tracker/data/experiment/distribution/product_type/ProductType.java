package uk.ac.ebi.impc_prod_tracker.data.experiment.distribution.product_type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.ac.ebi.impc_prod_tracker.data.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
@Getter
@Setter
@Entity
public class ProductType extends BaseEntity
{
    @Id
    @SequenceGenerator(name = "productTypeSeq", sequenceName = "PRODUCT_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productTypeSeq")
    private Long id;

    private String name;
}

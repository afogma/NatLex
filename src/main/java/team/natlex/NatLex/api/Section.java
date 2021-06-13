package team.natlex.NatLex.api;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "SECTIONS")
@TypeDef(
        name = "list-array",
        typeClass = ArrayList.class
)
class Section {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
        private String[] codes;
//    @Type(type = "list-array")
//    @Column(name = "codes",
//            columnDefinition = "text[]")
//    private List<String> codes;
}

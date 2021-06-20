package team.natlex.NatLex.api;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SECTIONS")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
class Section {

    @Id
    private String name;

    @Type(type = "list-array")
    @Column(name = "codes",
            columnDefinition = "text[]")
    private List<String> codes;

}

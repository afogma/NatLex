package team.natlex.NatLex.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "CLASSES")
class GeologicalClass {

    @Id
    private String name;
    private String code;

}
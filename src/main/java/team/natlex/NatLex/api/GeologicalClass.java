package team.natlex.NatLex.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@AllArgsConstructor
@Entity
@Data
@Table(name = "CLASSES")
class GeologicalClass {

    @Id
    private String name;
    private String code;

}
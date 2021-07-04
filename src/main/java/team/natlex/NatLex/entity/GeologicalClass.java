package team.natlex.NatLex.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "CLASSES")
public class GeologicalClass {

    @Id
    private String name;
    private String code;

}
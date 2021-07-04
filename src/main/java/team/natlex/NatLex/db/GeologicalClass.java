package team.natlex.NatLex.db;

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
    @Transient
    private final static int CLASS_NUMBER_PREFIX = 11;

    public String getClassNumber() {
        return name.substring(CLASS_NUMBER_PREFIX);
    }

}
package org.debux.webmotion.jpa;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

/**
 * All entities must extend this class, to be managed by the @see GenericDAO. 
 * The class adds only an uuid identifier.
 * 
 * @author julien
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class IdentifiableEntity implements Serializable {
    
    /** Identifier attribute name */
    public static String ATTRIBUTE_NAME_ID = "id";
    
    /** Identifier */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name ="system-uuid", strategy = "uuid")
    protected String id;

    /**
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Set identifier.
     * @param id identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdentifiableEntity == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        IdentifiableEntity other = (IdentifiableEntity) obj;
        return new EqualsBuilder().append(id, other.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).build();
    }
}

package org.debux.webmotion.jpa;

import javax.persistence.Basic;
import javax.persistence.Id;

/**
 *
 * @author julien
 */
public interface IdentifiableEntity {
    
    @Basic
    @Id
    String getId();
    
}

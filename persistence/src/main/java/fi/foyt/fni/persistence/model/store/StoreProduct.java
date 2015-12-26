package fi.foyt.fni.persistence.model.store;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class StoreProduct extends Publication {

}

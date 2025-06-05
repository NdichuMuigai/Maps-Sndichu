package edu.brown.cs.student.main.Creator;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type. Your parser class constructor should take a second parameter of this
 * generic interface type.
 *
 * @param <T> - the generic type T
 */
public interface CreatorFromRow<T> {

  T create(List<String> row) throws FactoryFailureException;
}

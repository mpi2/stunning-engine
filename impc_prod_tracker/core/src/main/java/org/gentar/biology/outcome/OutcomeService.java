package org.gentar.biology.outcome;

import org.gentar.audit.history.History;
import org.gentar.biology.outcome.type.OutcomeType;
import java.util.List;

/**
 * The methods to control the information of outcomes.
 */
public interface OutcomeService
{
    /**
     * Find all the outcomes.
     * @return A list of {@link Outcome}
     */
    List<Outcome> getOutcomes();

    /**
     * Creates an outcome in the system.
     * @param outcome In memory outcome object. Without ids. Not saved in database yet.
     * @return The outcome saved in the database.
     */
    Outcome create(Outcome outcome);

    /**
     * Updates an existing outcome.
     * @param outcome New data for the outcome.
     * @return
     */
    History update(Outcome outcome);

    /**
     * Gets a {@link OutcomeType} object based in its name.
     * @param name Name of the {@link OutcomeType} object.
     * @return {@link OutcomeType} object.
     */
    OutcomeType getOutcomeTypeByName(String name);

    /**
     * Gets a {@link OutcomeType} object based in its name.
     * @param name Name of the {@link OutcomeType} object.
     * @return {@link OutcomeType} object.
     */
    OutcomeType getOutcomeTypeByNameFailingWhenNull(String name);

    /**
    * Gets an outcome given its tpo. Throws an exception if the tpo is not found.
    * @param tpo TPO
    * @return The Outcome
     */
    Outcome getByTpoFailsIfNotFound(String tpo);
}

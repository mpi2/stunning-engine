package org.gentar.biology.targ_rep.allele.mutation_subtype;

public interface TargRepMutationSubtypeService {
    /**
     * Get a {@link TargRepMutationSubtype} object with the information of the species identified by the given name.
     * @param name
     * @return TargRepMutationSubtype identified by name. Null if not found.
     */
    TargRepMutationSubtype getTargRepMutationSubtypeByName(String name);
}

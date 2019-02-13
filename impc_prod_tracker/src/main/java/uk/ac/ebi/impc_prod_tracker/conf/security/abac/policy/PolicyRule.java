/*******************************************************************************
 * Copyright 2019 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.impc_prod_tracker.conf.security.abac.policy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.expression.Expression;

@NoArgsConstructor
@Data
public class PolicyRule
{
    private String name;
    private String description;
    /*
     * Boolean SpEL expression. If evaluated to true, then this rule is applied to the request access context.
     */
    @NonNull
    private Expression target;

    /*
     * Boolean SpEL expression, if evaluated to true, then access granted.
     */
    @NonNull private Expression  condition;
}

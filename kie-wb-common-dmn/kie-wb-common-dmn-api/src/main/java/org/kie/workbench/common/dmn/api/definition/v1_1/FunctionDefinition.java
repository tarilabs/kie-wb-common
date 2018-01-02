/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class FunctionDefinition extends Expression implements HasExpression {

    private Expression expression;

    private List<InformationItem> formalParameter;

    public FunctionDefinition() {
        this(new Id(),
             new Description(),
             new QName(),
             null);
    }

    public FunctionDefinition(final @MapsTo("id") Id id,
                              final @MapsTo("description") Description description,
                              final @MapsTo("typeRef") QName typeRef,
                              final @MapsTo("expression") Expression expression) {
        super(id,
              description,
              typeRef);
        this.expression = expression;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    public List<InformationItem> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }
}

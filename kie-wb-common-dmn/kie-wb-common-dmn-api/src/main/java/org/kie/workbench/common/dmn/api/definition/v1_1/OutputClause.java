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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class OutputClause extends DMNElement {

    private UnaryTests outputValues;
    private LiteralExpression defaultOutputEntry;
    private String name;
    private QName typeRef;

    public OutputClause() {
        this(new Id(),
             new Description(),
             new UnaryTests(),
             new LiteralExpression(),
             "",
             new QName());
    }

    public OutputClause(final @MapsTo("id") Id id,
                        final @MapsTo("description") Description description,
                        final @MapsTo("outputValues") UnaryTests outputValues,
                        final @MapsTo("defaultOutputEntry") LiteralExpression defaultOutputEntry,
                        final @MapsTo("name") String name,
                        final @MapsTo("typeRef") QName typeRef) {
        super(id,
              description);
        this.outputValues = outputValues;
        this.defaultOutputEntry = defaultOutputEntry;
        this.name = name;
        this.typeRef = typeRef;
    }

    public UnaryTests getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(final UnaryTests value) {
        this.outputValues = value;
    }

    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    public void setDefaultOutputEntry(final LiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
    }

    public QName getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(final QName value) {
        this.typeRef = value;
    }
}

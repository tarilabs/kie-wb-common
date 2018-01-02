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
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class Context extends Expression {

    private List<ContextEntry> contextEntry;

    public Context() {
        this(new Id(),
             new Description(),
             new QName());
    }

    public Context(final @MapsTo("id") Id id,
                   final @MapsTo("description") Description description,
                   final @MapsTo("typeRef") QName typeRef) {
        super(id,
              description,
              typeRef);
    }

    public List<ContextEntry> getContextEntry() {
        if (contextEntry == null) {
            contextEntry = new ArrayList<>();
        }
        return this.contextEntry;
    }
}

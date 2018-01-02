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

@Portable
public class UnaryTests extends DMNElement {

    private String text;
    private String expressionLanguage;

    public UnaryTests() {
        this(new Id(),
             new Description(),
             "",
             "");
    }

    public UnaryTests(final @MapsTo("id") Id id,
                      final @MapsTo("description") Description description,
                      final @MapsTo("text") String text,
                      final @MapsTo("expressionLanguage") String expressionLanguage) {
        super(id,
              description);
        this.text = text;
        this.expressionLanguage = expressionLanguage;
    }

    public String getText() {
        return text;
    }

    public void setText(final String value) {
        this.text = value;
    }

    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final String value) {
        this.expressionLanguage = value;
    }
}

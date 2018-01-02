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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class ItemDefinitionPropertyConverter {

    public static ItemDefinition wbFromDMN(final org.kie.dmn.model.v1_1.ItemDefinition dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        ItemDefinition result = new ItemDefinition();
        result.setId(id);
        result.setName(name);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        result.setTypeLanguage(dmn.getTypeLanguage());
        result.setIsCollection(dmn.isIsCollection());

        UnaryTests utConverted = UnaryTestsPropertyConverter.wbFromDMN(dmn.getAllowedValues());
        result.setAllowedValues(utConverted);

        for (org.kie.dmn.model.v1_1.ItemDefinition child : dmn.getItemComponent()) {
            ItemDefinition convertedChild = ItemDefinitionPropertyConverter.wbFromDMN(child);
            result.getItemComponent().add(convertedChild);
        }

        return result;
    }

    public static org.kie.dmn.model.v1_1.ItemDefinition dmnFromWB(final ItemDefinition wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.v1_1.ItemDefinition result = new org.kie.dmn.model.v1_1.ItemDefinition();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setName(wb.getName().getValue());
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        result.setTypeLanguage(wb.getTypeLanguage());
        result.setIsCollection(wb.isIsCollection());

        result.setAllowedValues(UnaryTestsPropertyConverter.dmnFromWB(wb.getAllowedValues()));

        for (ItemDefinition child : wb.getItemComponent()) {
            org.kie.dmn.model.v1_1.ItemDefinition convertedChild = ItemDefinitionPropertyConverter.dmnFromWB(child);
            result.getItemComponent().add(convertedChild);
        }

        return result;
    }
}
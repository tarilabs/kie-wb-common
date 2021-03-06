/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.eclipse.bpmn2.Definitions;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class DefinitionsConverterTest {

    @Test
    public void JBPM_7526_shouldSetExporter() {
        GraphNodeStoreImpl nodeStore = new GraphNodeStoreImpl();
        NodeImpl x = new NodeImpl("x");
        BPMNDiagramImpl diag = new BPMNDiagramImpl();
        diag.setDiagramSet(new DiagramSet(
                new Name("x"),
                new Documentation("doc"),
                new Id("x"),
                new Package("org.jbpm"),
                new Version("1.0"),
                new AdHoc(false),
                new ProcessInstanceDescription("descr"),
                new Executable(true)
        ));
        x.setContent(new ViewImpl<>(diag, BoundsImpl.build()));
        nodeStore.add(x);
        ConverterFactory f = new ConverterFactory(new DefinitionsBuildingContext(
                new GraphImpl("x", nodeStore)),
                                                  new PropertyWriterFactory());

        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(f, new PropertyWriterFactory());
        Definitions definitions =
                definitionsConverter.toDefinitions();

        assertThat(definitions.getExporter()).isNotBlank();
        assertThat(definitions.getExporterVersion()).isNotBlank();
    }
}
/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.actions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class TextAnnotationTextPropertyProviderImpl implements TextPropertyProvider {

    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private DefinitionUtils definitionUtils;

    public TextAnnotationTextPropertyProviderImpl() {
        //CDI proxy
    }

    @Inject
    public TextAnnotationTextPropertyProviderImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                                  final DefinitionUtils definitionUtils) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean supports(final Element<? extends Definition> element) {
        return element.getContent().getDefinition() instanceof TextAnnotation;
    }

    @Override
    public void setText(final AbstractCanvasHandler canvasHandler,
                        final CanvasCommandManager<AbstractCanvasHandler> commandManager,
                        final Element<? extends Definition> element,
                        final String text) {
        final Object definition = element.getContent().getDefinition();
        final CanvasCommand<AbstractCanvasHandler> command =
                canvasCommandFactory.updatePropertyValue(element,
                                                         definitionUtils.getNameIdentifier(definition),
                                                         text);
        commandManager.execute(canvasHandler,
                               command);
    }

    @Override
    public String getText(final Element<? extends Definition> element) {
        final TextAnnotation ta = (TextAnnotation) element.getContent().getDefinition();
        final String text = ta.getText().getValue();
        return text;
    }
}

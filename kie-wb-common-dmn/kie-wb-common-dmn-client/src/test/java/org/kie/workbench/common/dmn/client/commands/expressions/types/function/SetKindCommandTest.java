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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SetKindCommandTest {

    @Mock
    private GridColumn mockColumn;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private BaseExpressionGrid originalEditor;

    private LiteralExpression originalExpression = new LiteralExpression();

    private FunctionDefinition.Kind originalKind = FunctionDefinition.Kind.FEEL;

    @Mock
    private BaseExpressionGrid newEditor;

    private LiteralExpression newExpression = new LiteralExpression();

    private FunctionDefinition.Kind newKind = FunctionDefinition.Kind.JAVA;

    private FunctionDefinition function;

    private GridData uiModel;

    private SetKindCommand command;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.function.getOtherAttributes().put(FunctionDefinition.KIND_QNAME,
                                               originalKind.code());
        this.function.setExpression(originalExpression);

        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(mockColumn);
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.setCell(0,
                             0,
                             new ExpressionCellValue(Optional.of(originalEditor)));

        doReturn(ruleManager).when(handler).getRuleManager();
    }

    private void setupCommand() {
        final GridCellValueTuple gcv = new GridCellValueTuple<>(0,
                                                                0,
                                                                uiModel,
                                                                new ExpressionCellValue(Optional.of(newEditor)));
        this.command = new SetKindCommand(gcv,
                                          function,
                                          newKind,
                                          Optional.of(newExpression),
                                          canvasOperation);
    }

    @Test
    public void testGraphCommandAllow() {
        setupCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        setupCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertEquals(newKind.code(),
                     function.getOtherAttributes().get(FunctionDefinition.KIND_QNAME));
        assertEquals(newExpression,
                     function.getExpression());
    }

    @Test
    public void testGraphCommandUndo() {
        setupCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Set Kind and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertEquals(originalKind.code(),
                     function.getOtherAttributes().get(FunctionDefinition.KIND_QNAME));
        assertEquals(originalExpression,
                     function.getExpression());
    }

    @Test
    public void testGraphCommandUndoWithNullOriginalExpression() {
        function.setExpression(null);
        setupCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Set Kind and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertEquals(originalKind.code(),
                     function.getOtherAttributes().get(FunctionDefinition.KIND_QNAME));
        assertNull(function.getExpression());
    }

    @Test
    public void testCanvasCommandAllow() {
        setupCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        setupCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));

        assertEquals(newEditor,
                     ((ExpressionCellValue) uiModel.getCell(0, 0).getValue()).getValue().get());

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        setupCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        //Set Kind and then undo
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.undo(handler));

        assertEquals(originalEditor,
                     ((ExpressionCellValue) uiModel.getCell(0, 0).getValue()).getValue().get());

        //Once for execute, once for undo
        verify(canvasOperation, times(2)).execute();
    }
}

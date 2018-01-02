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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DescriptionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddDecisionRuleCommandTest {

    private DecisionTable dtable;

    private DecisionRule rule;

    private GridData uiModel;

    private DMNGridRow uiModelRow;

    private DecisionTableUIModelMapper uiModelMapper;

    private AddDecisionRuleCommand command;

    @Mock
    private DMNGridLayer selectionManager;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private DescriptionColumn uiDescriptionColumn;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
        this.rule = new DecisionRule();
        this.uiModel = new DMNGridData(selectionManager);
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModelRow = new DMNGridRow();
        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable));

        this.command = new AddDecisionRuleCommand(dtable, rule, uiModel, uiModelRow, uiModelMapper, canvasOperation);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();
        doReturn(3).when(uiDescriptionColumn).getIndex();
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecuteConstructedDescription() {
        assertEquals(0, dtable.getRule().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));
        assertTrue(rule.getDescription() != null);
        assertTrue(rule.getDescription().getValue() != null);
        assertFalse(rule.getDescription().getValue().isEmpty());
    }

    @Test
    public void testGraphCommandExecuteConstructedRuleInputs() {
        assertEquals(0, dtable.getRule().size());
        final int inputsCount = 2;

        for (int i = 0; i < inputsCount; i++) {
            dtable.getInput().add(new InputClause());
        }

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));

        assertEquals(inputsCount, rule.getInputEntry().size());
        assertEquals(0, rule.getOutputEntry().size());

        for (int inputIndex = 0; inputIndex < inputsCount; inputIndex++) {
            assertTrue(rule.getInputEntry().get(inputIndex).getText() != null);
            assertFalse(rule.getInputEntry().get(inputIndex).getText().isEmpty());
        }
    }

    @Test
    public void testGraphCommandExecuteConstructedRuleOutputs() {
        assertEquals(0, dtable.getRule().size());
        final int outputsCount = 2;

        for (int i = 0; i < outputsCount; i++) {
            dtable.getOutput().add(new OutputClause());
        }

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));

        assertEquals(0, rule.getInputEntry().size());
        assertEquals(outputsCount, rule.getOutputEntry().size());

        for (int outputIndex = 0; outputIndex < outputsCount; outputIndex++) {
            assertTrue(rule.getOutputEntry().get(outputIndex).getText() != null);
            assertFalse(rule.getOutputEntry().get(outputIndex).getText().isEmpty());
        }
    }

    @Test
    public void testGraphCommandUndo() {
        assertEquals(0, dtable.getRule().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(1, dtable.getRule().size());

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(0, dtable.getRule().size());
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandAddRuleAndThenUndo() throws Exception {
        dtable.getInput().add(new InputClause());
        dtable.getOutput().add(new OutputClause());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendColumn(uiOutputClauseColumn);
        uiModel.appendColumn(uiDescriptionColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        canvasAddRuleCommand.execute(canvasHandler);

        assertEquals(1, uiModel.getRowCount());
        assertEquals(1, uiModel.getRow(0).getCells().get(0).getValue().getValue());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, uiModel.getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(AddOutputClauseCommand.OUTPUT_CLAUSE_DEFAULT_VALUE, uiModel.getRow(0).getCells().get(2).getValue().getValue());
        assertEquals(AddDecisionRuleCommand.DESCRIPTION_DEFAULT_VALUE, uiModel.getRow(0).getCells().get(3).getValue().getValue());

        canvasAddRuleCommand.undo(canvasHandler);
        assertEquals(0, uiModel.getRowCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
    }
}

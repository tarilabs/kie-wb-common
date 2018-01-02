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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class MoveColumnsCommandTest {

    @Mock
    private DMNGridLayer selectionManager;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumnOne;

    @Mock
    private InputClauseColumn uiInputClauseColumnTwo;

    @Mock
    private InputClauseColumn uiInputClauseColumnThree;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnOne;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnTwo;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnThree;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    @Mock
    private InputClause inputClauseOne;

    @Mock
    private InputClause inputClauseTwo;

    @Mock
    private InputClause inputClauseThree;

    @Mock
    private OutputClause outputClauseOne;

    @Mock
    private OutputClause outputClauseTwo;

    @Mock
    private OutputClause outputClauseThree;

    private DMNGridData uiModel;

    private MoveColumnsCommand command;

    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    private Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setUp() throws Exception {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData(selectionManager);

        dtable.getInput().add(inputClauseOne);
        dtable.getInput().add(inputClauseTwo);
        dtable.getInput().add(inputClauseThree);
        dtable.getOutput().add(outputClauseOne);
        dtable.getOutput().add(outputClauseTwo);
        dtable.getOutput().add(outputClauseThree);

        dtable.getRule().add(new DecisionRule() {{
            getInputEntry().add(new UnaryTests());
            getInputEntry().add(new UnaryTests());
            getInputEntry().add(new UnaryTests());
            getOutputEntry().add(new LiteralExpression());
            getOutputEntry().add(new LiteralExpression());
            getOutputEntry().add(new LiteralExpression());
        }});

        uiModel.appendColumn(uiRowNumberColumn);
        uiModel.appendColumn(uiInputClauseColumnOne);
        uiModel.appendColumn(uiInputClauseColumnTwo);
        uiModel.appendColumn(uiInputClauseColumnThree);
        uiModel.appendColumn(uiOutputClauseColumnOne);
        uiModel.appendColumn(uiOutputClauseColumnTwo);
        uiModel.appendColumn(uiOutputClauseColumnThree);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumnOne).getIndex();
        doReturn(2).when(uiInputClauseColumnTwo).getIndex();
        doReturn(3).when(uiInputClauseColumnThree).getIndex();
        doReturn(4).when(uiOutputClauseColumnOne).getIndex();
        doReturn(5).when(uiOutputClauseColumnTwo).getIndex();
        doReturn(6).when(uiOutputClauseColumnThree).getIndex();
    }

    @Test
    public void testCommandAllow() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testMoveSingleInputColumnLeft() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(1, 2, 0, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(2, 3, 1, 4, 5, 6);
    }

    @Test
    public void testMoveMultipleInputColumnsLeft() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnTwo, uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(2, 0, 1, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(3, 1, 2, 4, 5, 6);
    }

    @Test
    public void testMoveSingleInputColumnRight() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnOne), 3);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(2, 0, 1, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(3, 1, 2, 4, 5, 6);
    }

    @Test
    public void testMoveMultipleInputColumnsRight() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnOne, uiInputClauseColumnTwo), 3);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(1, 2, 0, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(2, 3, 1, 4, 5, 6);
    }

    @Test
    public void testMoveSingleOutputColumnLeft() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnThree), 4);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 1, 2, 0);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 5, 6, 4);
    }

    @Test
    public void testMoveMultipleOutputColumnsLeft() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnTwo, uiOutputClauseColumnThree), 4);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 2, 0, 1);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 6, 4, 5);
    }

    @Test
    public void testMoveSingleOutputColumnRight() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnOne), 6);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 2, 0, 1);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 6, 4, 5);
    }

    @Test
    public void testMoveMultipleOutputColumnsRight() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnOne, uiOutputClauseColumnTwo), 6);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 1, 2, 0);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 5, 6, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleInputToOutputs() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnOne), 4);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleOutputToInputs() throws Exception {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnOne), 1);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMixedToInputs() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnOne, uiOutputClauseColumnOne), 3);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMixedToOutputs() throws Exception {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnOne, uiInputClauseColumnOne), 6);

        graphCommand.execute(graphCommandExecutionContext);
    }

    private void assertClauses(final int... clausesIndexes) {
        assertEquals(inputClauseOne, dtable.getInput().get(clausesIndexes[0]));
        assertEquals(inputClauseTwo, dtable.getInput().get(clausesIndexes[1]));
        assertEquals(inputClauseThree, dtable.getInput().get(clausesIndexes[2]));
        assertEquals(outputClauseOne, dtable.getOutput().get(clausesIndexes[3]));
        assertEquals(outputClauseTwo, dtable.getOutput().get(clausesIndexes[4]));
        assertEquals(outputClauseThree, dtable.getOutput().get(clausesIndexes[5]));
    }

    private void assertColumns(final int... columnIndexes) {
        assertEquals(uiInputClauseColumnOne, uiModel.getColumns().get(columnIndexes[0]));
        assertEquals(uiInputClauseColumnTwo, uiModel.getColumns().get(columnIndexes[1]));
        assertEquals(uiInputClauseColumnThree, uiModel.getColumns().get(columnIndexes[2]));
        assertEquals(uiOutputClauseColumnOne, uiModel.getColumns().get(columnIndexes[3]));
        assertEquals(uiOutputClauseColumnTwo, uiModel.getColumns().get(columnIndexes[4]));
        assertEquals(uiOutputClauseColumnThree, uiModel.getColumns().get(columnIndexes[5]));
    }

    private void moveColumnsToPositionCommand(final List<GridColumn<?>> columns, int position) {
        command = new MoveColumnsCommand(dtable, uiModel, position, columns, canvasOperation);

        graphCommand = command.newGraphCommand(canvasHandler);
        canvasCommand = command.newCanvasCommand(canvasHandler);
    }
}

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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class MoveColumnsCommandTest extends BaseMoveCommandsTest<MoveColumnsCommand> {

    @Before
    public void setup() {
        this.relation = new Relation();
        this.uiModel = new DMNGridData(gridLayer);
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn1).getIndex();
        doReturn(2).when(uiModelColumn2).getIndex();

        addRelationColumn(II1);
        addRelationColumn(II2);
        addRelationRow(II1);
        addRelationRow(II2);

        addUiModelColumn(uiRowNumberColumn);
        addUiModelColumn(uiModelColumn1);
        addUiModelColumn(uiModelColumn2);
        addUiModelRow(0);
        addUiModelRow(1);
    }

    @Override
    protected void addUiModelRow(final int rowIndex) {
        final DMNGridRow uiRow = new DMNGridRow();
        uiModel.appendRow(uiRow);
        uiModel.setCell(rowIndex, 0, new BaseGridCellValue<>(rowIndex + 1));
        uiModel.setCell(rowIndex, 1, new BaseGridCellValue<>("value0"));
        uiModel.setCell(rowIndex, 2, new BaseGridCellValue<>("value1"));
    }

    private void setupCommand(final int index,
                              final GridColumn<?> uiModelColumn) {
        this.command = new MoveColumnsCommand(relation,
                                              uiModel,
                                              index,
                                              Collections.singletonList(uiModelColumn),
                                              canvasOperation);
    }

    @Test
    public void testGraphCommandAllow() {
        //Arbitrary command setup
        setupCommand(0,
                     uiModel.getColumns().get(0));

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteMoveLeft() {
        setupCommand(1,
                     uiModel.getColumns().get(2));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertRelationDefinition(1, 0);
    }

    @Test
    public void testGraphCommandExecuteMoveRight() {
        setupCommand(2,
                     uiModel.getColumns().get(1));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertRelationDefinition(1, 0);
    }

    @Test
    public void testGraphCommandUndoMoveLeft() {
        setupCommand(1,
                     uiModel.getColumns().get(2));

        //Move column and then undo
        final Command<GraphCommandExecutionContext, RuleViolation> gc = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.execute(gce));
        //Move UI columns as MoveColumnsCommand.undo() relies on the UiModel being updated
        command.newCanvasCommand(handler).execute(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.undo(gce));

        assertRelationDefinition(0, 1);
    }

    @Test
    public void testGraphCommandUndoMoveRight() {
        setupCommand(2,
                     uiModel.getColumns().get(1));

        //Move column and then undo
        final Command<GraphCommandExecutionContext, RuleViolation> gc = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.execute(gce));
        //Move UI columns as MoveColumnsCommand.undo() relies on the UiModel being updated
        command.newCanvasCommand(handler).execute(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.undo(gce));

        assertRelationDefinition(0, 1);
    }

    private void assertRelationDefinition(final int ii1ColumnIndex,
                                          final int ii2ColumnIndex) {
        assertEquals(makeIdentifier(II1, 0),
                     relation.getRow().get(0).getExpression().get(ii1ColumnIndex).getId().getValue());
        assertEquals(makeIdentifier(II2, 0),
                     relation.getRow().get(1).getExpression().get(ii1ColumnIndex).getId().getValue());
        assertEquals(makeIdentifier(II1, 1),
                     relation.getRow().get(0).getExpression().get(ii2ColumnIndex).getId().getValue());
        assertEquals(makeIdentifier(II2, 1),
                     relation.getRow().get(1).getExpression().get(ii2ColumnIndex).getId().getValue());
    }

    @Test
    public void testCanvasCommandAllow() {
        //Arbitrary command setup
        setupCommand(0,
                     uiModel.getColumns().get(0));

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteMoveLeft() {
        setupCommand(1,
                     uiModel.getColumns().get(2));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        assertUiModelDefinition(new int[]{1, 0});
    }

    @Test
    public void testCanvasCommandExecuteMoveRight() {
        setupCommand(2,
                     uiModel.getColumns().get(1));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        assertUiModelDefinition(new int[]{1, 0});
    }

    @Test
    public void testCanvasCommandUndoMoveUp() {
        setupCommand(1,
                     uiModel.getColumns().get(2));

        //Move column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertUiModelDefinition(new int[]{0, 1});
    }

    @Test
    public void testCanvasCommandUndoMoveRight() {
        setupCommand(2,
                     uiModel.getColumns().get(1));

        //Move row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertUiModelDefinition(new int[]{0, 1});
    }

    private void assertUiModelDefinition(final int[] columnIndexes) {
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("value" + columnIndexes[0],
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals("value" + columnIndexes[1],
                     uiModel.getCell(0, 2).getValue().getValue());
        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals("value" + columnIndexes[0],
                     uiModel.getCell(1, 1).getValue().getValue());
        assertEquals("value" + columnIndexes[1],
                     uiModel.getCell(1, 2).getValue().getValue());
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableRowNumberColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class MoveRowsCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                           VetoUndoCommand {

    private final DecisionTable dtable;
    private final DMNGridData uiModel;
    private final int index;
    private final List<GridRow> rows;
    private final org.uberfire.mvp.Command canvasOperation;

    private final int oldIndex;

    public MoveRowsCommand(final DecisionTable dtable,
                           final DMNGridData uiModel,
                           final int index,
                           final List<GridRow> rows,
                           final org.uberfire.mvp.Command canvasOperation) {
        this.dtable = dtable;
        this.uiModel = uiModel;
        this.index = index;
        this.rows = new ArrayList<>(rows);
        this.canvasOperation = canvasOperation;

        this.oldIndex = uiModel.getRows().indexOf(rows.get(0));
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
                moveDecisionRules(index);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                moveDecisionRules(oldIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            private void moveDecisionRules(final int index) {
                final int oldIndex = uiModel.getRows().indexOf(rows.get(0));
                final List<DecisionRule> rulesToMove = rows
                        .stream()
                        .map(r -> uiModel.getRows().indexOf(r))
                        .map(i -> dtable.getRule().get(i))
                        .collect(Collectors.toList());

                final List<DecisionRule> rules = dtable.getRule();

                rules.removeAll(rulesToMove);

                if (index < oldIndex) {
                    rules.addAll(index,
                                 rulesToMove);
                } else if (index > oldIndex) {
                    rules.addAll(index - rulesToMove.size() + 1,
                                 rulesToMove);
                }
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                uiModel.moveRowsTo(index,
                                   rows);
                updateRowNumbers();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModel.moveRowsTo(oldIndex,
                                   rows);
                updateRowNumbers();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            private void updateRowNumbers() {
                final Optional<DecisionTableRowNumberColumn> rowNumberColumn = uiModel
                        .getColumns()
                        .stream()
                        .filter(c -> c instanceof DecisionTableRowNumberColumn)
                        .map(c -> (DecisionTableRowNumberColumn) c)
                        .findFirst();

                rowNumberColumn.ifPresent(c -> {
                    final int columnIndex = uiModel.getColumns().indexOf(c);
                    for (int rowIndex = 0; rowIndex < uiModel.getRowCount(); rowIndex++) {
                        uiModel.setCell(rowIndex,
                                        columnIndex,
                                        new BaseGridCellValue<>(rowIndex + 1));
                    }
                });
            }
        };
    }
}

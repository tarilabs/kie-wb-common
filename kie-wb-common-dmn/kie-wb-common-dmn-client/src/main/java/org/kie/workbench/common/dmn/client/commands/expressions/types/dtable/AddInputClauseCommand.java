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

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class AddInputClauseCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                 VetoUndoCommand {

    public static final String INPUT_CLAUSE_DEFAULT_VALUE = "unary test";

    private final DecisionTable dtable;
    private final InputClause inputClause;
    private final GridData uiModel;
    private final InputClauseColumn uiModelColumn;
    private final DecisionTableUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;

    public AddInputClauseCommand(final DecisionTable dtable,
                                 final InputClause inputClause,
                                 final GridData uiModel,
                                 final InputClauseColumn uiModelColumn,
                                 final DecisionTableUIModelMapper uiModelMapper,
                                 final org.uberfire.mvp.Command canvasOperation) {
        this.dtable = dtable;
        this.inputClause = inputClause;
        this.uiModel = uiModel;
        this.uiModelColumn = uiModelColumn;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;
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
                dtable.getInput().add(inputClause);

                dtable.getRule().forEach(rule -> {
                    final UnaryTests ut = new UnaryTests();
                    ut.setText(INPUT_CLAUSE_DEFAULT_VALUE);
                    rule.getInputEntry().add(ut);
                });

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                final int clauseIndex = dtable.getInput().indexOf(inputClause);
                dtable.getRule().forEach(rule -> rule.getInputEntry().remove(clauseIndex));
                dtable.getInput().remove(inputClause);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                final int columnIndex = DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + dtable.getInput().indexOf(inputClause);
                uiModel.insertColumn(columnIndex,
                                     uiModelColumn);

                for (int rowIndex = 0; rowIndex < dtable.getRule().size(); rowIndex++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               columnIndex);
                }

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModel.deleteColumn(uiModelColumn);

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}

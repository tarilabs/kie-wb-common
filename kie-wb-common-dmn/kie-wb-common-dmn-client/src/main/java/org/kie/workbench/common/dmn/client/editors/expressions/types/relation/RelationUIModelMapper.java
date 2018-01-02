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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class RelationUIModelMapper extends BaseUIModelMapper<Relation> {

    public RelationUIModelMapper(final Supplier<GridData> uiModel,
                                 final Supplier<Optional<Relation>> dmnModel) {
        super(uiModel,
              dmnModel);
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(relation -> {
            final RelationUIModelMapperHelper.RelationSection section = RelationUIModelMapperHelper.getSection(relation, columnIndex);
            switch (section) {
                case ROW_INDEX:
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(rowIndex + 1));
                    break;
                case INFORMATION_ITEM:
                    final org.kie.workbench.common.dmn.api.definition.v1_1.List row = relation.getRow().get(rowIndex);
                    final int iiIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, columnIndex);
                    final Expression e = row.getExpression().get(iiIndex);
                    final Optional<Expression> expression = Optional.ofNullable(e);

                    expression.ifPresent(ex -> {
                        // Whilst the DMN 1.1 specification allows for ANY expression to be used we have made the simplification
                        // to limit ourselves to LiteralExpressions. Our Grid-system supports ANY (nested) expression too; however
                        // the simplification has been made for the benefit of USERS.
                        final LiteralExpression le = (LiteralExpression) ex;
                        uiModel.get().setCell(rowIndex,
                                              columnIndex,
                                              new BaseGridCellValue<>(le.getText()));
                    });
            }
        });
    }

    @Override
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(relation -> {
            final RelationUIModelMapperHelper.RelationSection section = RelationUIModelMapperHelper.getSection(relation, columnIndex);
            switch (section) {
                case ROW_INDEX:
                    break;
                case INFORMATION_ITEM:
                    final org.kie.workbench.common.dmn.api.definition.v1_1.List row = relation.getRow().get(rowIndex);
                    final int iiIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, columnIndex);
                    final Expression e = row.getExpression().get(iiIndex);
                    final Optional<Expression> expression = Optional.ofNullable(e);

                    expression.ifPresent(ex -> {
                        // Whilst the DMN 1.1 specification allows for ANY expression to be used we have made the simplification
                        // to limit ourselves to LiteralExpressions. Our Grid-system supports ANY (nested) expression too; however
                        // the simplification has been made for the benefit of USERS.
                        final LiteralExpression le = (LiteralExpression) ex;
                        le.setText(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    });
            }
        });
    }
}

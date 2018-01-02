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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper.DecisionTableSection;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class DecisionTableUIModelMapper extends BaseUIModelMapper<DecisionTable> {

    public DecisionTableUIModelMapper(final Supplier<GridData> uiModel,
                                      final Supplier<Optional<DecisionTable>> dmnModel) {
        super(uiModel,
              dmnModel);
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(dtable -> {
            final DecisionRule rule = dtable.getRule().get(rowIndex);
            final DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, columnIndex);
            switch (section) {
                case ROW_INDEX:
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(rowIndex + 1));
                    break;
                case INPUT_CLAUSES:
                    final int iei = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, columnIndex);
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(rule.getInputEntry().get(iei).getText()));
                    break;
                case OUTPUT_CLAUSES:
                    final int oei = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, columnIndex);
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(rule.getOutputEntry().get(oei).getText()));
                    break;
                case DESCRIPTION:
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(rule.getDescription().getValue()));
                    break;
            }
        });
    }

    @Override
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(dtable -> {
            final DecisionRule rule = dtable.getRule().get(rowIndex);
            final DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, columnIndex);
            switch (section) {
                case ROW_INDEX:
                    break;
                case INPUT_CLAUSES:
                    final int iei = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, columnIndex);
                    rule.getInputEntry().get(iei).setText(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    break;
                case OUTPUT_CLAUSES:
                    final int oei = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, columnIndex);
                    rule.getOutputEntry().get(oei).setText(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    break;
                case DESCRIPTION:
                    rule.getDescription().setValue(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    break;
            }
        });
    }
}

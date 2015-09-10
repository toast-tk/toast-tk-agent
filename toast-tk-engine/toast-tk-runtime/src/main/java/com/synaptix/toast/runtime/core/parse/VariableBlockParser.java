package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parser for vaiable blocks.
 * Parse all lines beginning with $
 * Created by Nicolas Sauvage on 06/08/2015.
 */
public class VariableBlockParser implements IBlockParser {

    private static String VARIABLE_ASSIGNATION_SEPARATOR = ":=";

    @Override
    public BlockType getBlockType() {
        return BlockType.VARIABLE;
    }

    @Override
    public IBlock digest(List<String> strings, String path) {
        VariableBlock variableBlock = new VariableBlock();

        int parsedLines = 0;

        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();

            if (!isFirstLineOfBlock(string)) { // line is parsable
                variableBlock.setNumber0fLines(parsedLines);
                return variableBlock;
            }

            parsedLines++;

            String[] split = string.split(VARIABLE_ASSIGNATION_SEPARATOR);

            List<String> cells = new ArrayList<>();
            cells.add(split[0].trim());

            if (isVarMultiLine(string)) {
                StringBuilder stringBuilder = new StringBuilder();

                while (iterator.hasNext()) {
                    string = iterator.next();
                    parsedLines++;

                    if (!string.startsWith("\"\"\"")) {
                        stringBuilder.append(string.replace("\n", " ").replace("\t", " ")).append(" ");
                    } else {
                        break;
                    }
                }

                cells.add(stringBuilder.toString());
            } else if (isVarLine(string)) {
                cells.add(split[1].trim());
            }

            BlockLine line = new BlockLine();
            line.setCells(cells);

            variableBlock.addline(line);
        }

        variableBlock.setNumber0fLines(parsedLines);
        return variableBlock;
    }

    @Override
    public boolean isFirstLineOfBlock(String line) {
        return isVarLine(line) || isVarMultiLine(line);
    }

    private boolean isVarMultiLine(String line) {
        return line != null && line.startsWith("$")
                && line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
                && line.contains("\"\"\"");
    }

    private boolean isVarLine(String line) {
        return line != null && line.startsWith("$")
                && line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
                && !line.contains("\"\"\"");
    }
}
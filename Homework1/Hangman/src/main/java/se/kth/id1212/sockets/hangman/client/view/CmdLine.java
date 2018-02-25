package se.kth.id1212.sockets.hangman.client.view;

import java.util.ArrayList;
import java.util.List;

public class CmdLine {
    private static final String PARAM_DELIMITER = " ";
    private String[] params;
    private final String enteredLine;
    private Command cmd;

    public CmdLine(String enteredLine) {
        this.enteredLine = enteredLine;
        parseCmd(enteredLine);
        extractParams(enteredLine);
    }

    private void extractParams(String enteredLine) {
        if (enteredLine == null) {
            return;
        }
        String readyForParsing = removeExtraSpaces(removeCmd(enteredLine));
        List<String> params = new ArrayList<>();
        int start = 0;
        boolean inQuotes = false;
        for (int index = 0; index < readyForParsing.length(); index++) {
            if (currentCharIsQuote(readyForParsing, index)) {
                inQuotes = !inQuotes;
            }
            if (reachedEndOfString(readyForParsing, index)) {
                addParam(params, readyForParsing, start, index);
            } else if (timeToSplit(readyForParsing, index, inQuotes)) {
                addParam(params, readyForParsing, start, index);
                start = index + 1;
            }
        }
        this.params = params.toArray(new String[0]);
    }

    private void addParam(List<String> params, String paramSource, int start, int index) {
        if (reachedEndOfString(paramSource, index)) {
            params.add(removeQuotes(paramSource.substring(start)));
        } else {
            params.add(removeQuotes(paramSource.substring(start, index)));
        }
    }

    private String removeQuotes(String source) {
        return source.replaceAll("\"", "");
    }

    private boolean timeToSplit(String source, int index, boolean dontSplit) {
        return source.charAt(index) == PARAM_DELIMITER.charAt(0) && !dontSplit;
    }

    private boolean reachedEndOfString(String source, int index) {
        return index == (source.length() - 1);
    }

    private boolean currentCharIsQuote(String readyForParsing, int index) {
        return readyForParsing.charAt(index) == '\"';
    }

    private String removeCmd(String enteredLine) {
        if (cmd == Command.INPUT) {
            return enteredLine;
        }
        int indexAfterCmd = enteredLine.toUpperCase().indexOf(cmd.name()) + cmd.name().length();
        String withoutCmd = enteredLine.substring(indexAfterCmd, enteredLine.length());
        return withoutCmd.trim();
    }

    private void parseCmd(String enteredLine) {
        int cmdNameIndex = 0;
        try {
            String[] enteredTokens = removeExtraSpaces(enteredLine).split(PARAM_DELIMITER);
            cmd = Command.valueOf(enteredTokens[cmdNameIndex].toUpperCase());
        } catch (Throwable failedToReadCmd) {
            cmd = Command.INPUT;
        }
    }

    private String removeExtraSpaces(String source) {
        if (source == null)
        {
            return source;
        }
        String oneOrMoreOccurences = "+";
        return source.trim().replaceAll(PARAM_DELIMITER + oneOrMoreOccurences, PARAM_DELIMITER);
    }

    Command getCmd() {
        return cmd;
    }

    String getUserInput() {
        return enteredLine;
    }

    String getParameter(int index) {
        if (params == null) {
            return null;
        }
        if (index >= params.length) {
            return null;
        }
        return params[index];
    }
}

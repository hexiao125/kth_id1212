package se.kth.id1212.filemanage.client.view;

import java.util.ArrayList;
import java.util.List;

class UserConsole {
    private static final String PARAM_DELIMETER = " ";
    private String[] params;
    private UserCmd cmd;
    private final String enteredLine;

    UserConsole(String enteredLine) {
        this.enteredLine = enteredLine;
        parseCmd(enteredLine);
        extractParams(enteredLine);
    }

    UserCmd getCmd() {
        return cmd;
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

    private String removeExtraSpaces(String source) {
        if (source == null) {
            return source;
        }
        String oneOrMoreOccurences = "+";
        return source.trim().replaceAll(PARAM_DELIMETER + oneOrMoreOccurences, PARAM_DELIMETER);
    }

    private void parseCmd(String enteredLine) {
        int cmdNameIndex = 0;
        try {
            String[] enteredTokens = removeExtraSpaces(enteredLine).split(PARAM_DELIMETER);
            cmd = UserCmd.valueOf(enteredTokens[cmdNameIndex].toUpperCase());
        } catch (Throwable failedToReadCmd) {
            cmd = UserCmd.NO_COMMAND;
        }
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

    private boolean currentCharIsQuote(String readyForParsing, int index) {
        return readyForParsing.charAt(index) == '\"';
    }

    private String removeCmd(String enteredLine) {
        if (cmd == UserCmd.NO_COMMAND) {
            return enteredLine;
        }
        int indexAfterCmd = enteredLine.toUpperCase().indexOf(cmd.name()) + cmd.name().length();
        String withoutCmd = enteredLine.substring(indexAfterCmd, enteredLine.length());
        return withoutCmd.trim();
    }

    private boolean timeToSplit(String source, int index, boolean dontSplit) {
        return source.charAt(index) == PARAM_DELIMETER.charAt(0) && !dontSplit;
    }

    private boolean reachedEndOfString(String source, int index) {
        return index == (source.length() - 1);
    }

    private String removeQuotes(String source) {
        return source.replaceAll("\"", "");
    }
}

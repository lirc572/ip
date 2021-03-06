package com.lirc572.ip;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains static methods for parsing commands.
 */
public class Parser {

    /**
     * All exit commands are listed here.
     */
    private static final Set<String> EXIT_COMMANDS = Set.of(
        "bye",
        "exit",
        "quit",
        "sayonara",
        "\u3055\u3088\u306A\u3089"
    );

    /**
     * Tokenizes a command.
     *
     * @param command A line of command.
     * @return The tokenized command as a String array.
     */
    public static String[] tokenizeCommand(String command) {
        ArrayList<String> tokens = new ArrayList<>();
        boolean isNewToken = true;
        boolean unmatchedQuote = false;
        char quotationMark = ' ';
        for (char ch : command.toCharArray()) {
            if (!unmatchedQuote) {
                // if not a whitespace character
                if (ch != ' ' && ch != '\t') {
                    // if right after a whitespace character
                    if (isNewToken) {
                        // start of a quoted token
                        if (ch == '"' || ch == '\'') {
                            quotationMark = ch;
                            unmatchedQuote = true;
                            tokens.add("");
                        } else {
                            tokens.add(String.valueOf(ch));
                            isNewToken = false;
                        }
                    } else {
                        tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 1) + ch);
                    }
                } else {
                    isNewToken = true;
                }
            } else {
                if (ch != quotationMark) {
                    tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 1) + ch);
                } else {
                    unmatchedQuote = false;
                    isNewToken = true;
                }
            }
        }
        String[] result = new String[tokens.size()];
        return tokens.toArray(result);
    }

    private static String processExitCommand(String[] tokens, TaskList tasks) throws Exception {
        return Ui.printLine("Bye. Hope to see you again soon!");
    }

    private static String processClearCommand(String[] tokens, TaskList tasks) throws Exception {
        return Ui.printLine("clear");
    }

    private static String processHelpCommand(String[] tokens, TaskList tasks) throws Exception {
        return Ui.printLine(
            "For a detailed user guide, "
            + "please refer to https://elaina.lirc572.com/pages/user-guide.html"
        );
    }

    private static String processListCommand(String[] tokens, TaskList tasks) throws Exception {
        return Ui.printLine("Here are the tasks in your list:") + tasks.printAll();
    }

    private static String processDoneCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a valid task number!");
        }
        try {
            int taskNumber = Integer.parseInt(tokens[1]);
            if (taskNumber < 1) {
                throw new Exception("Please provide a valid task number!");
            }
            if (taskNumber <= tasks.getSize()) {
                tasks.markAsDone(taskNumber);
                response += Ui.printLine("Nice! I've marked this task as done:");
                response += Ui.printLine(String.format("  %s", tasks.getTaskString(taskNumber)));
            } else {
                throw new Exception("Task does not exist!");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Please provide a valid task number!");
        }
        return response;
    }

    private static String processDeleteCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a valid task number!");
        }
        try {
            int taskNumber = Integer.parseInt(tokens[1]);
            if (taskNumber < 1) {
                throw new Exception("Please provide a valid task number!");
            }
            if (taskNumber <= tasks.getSize()) {
                String taskString = tasks.delete(taskNumber);
                response += Ui.printLine("Noted. I've removed this task:");
                response += Ui.printLine(String.format("  %s", taskString));
                response += Ui.printLine(String.format("Now you have %d tasks in the list.", tasks.getSize()));
            } else {
                throw new Exception("Task does not exist!");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Please provide a valid task number!");
        }
        return response;
    }

    private static String processTodoCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a task name!");
        }
        Task task = new TodoTask(tokens[1]);
        tasks.add(task);
        response += Ui.printLine("Got it! I've added this task:");
        response += Ui.printLine("  " + task);
        response += Ui.printLine(String.format("Now you have %d tasks in the list.", tasks.getSize()));
        return response;
    }

    private static String processDeadlineCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a task name!");
        }
        boolean dueTimeNotProvided = true;
        for (int index = 2; index < tokens.length; index++) {
            if (tokens[index].equals("/by")) {
                dueTimeNotProvided = false;
                if (index + 1 < tokens.length) {
                    try {
                        Task task = new DeadlineTask(tokens[1], tokens[index + 1]);
                        tasks.add(task);
                        response += Ui.printLine("Got it! I've added this task:");
                        response += Ui.printLine("  " + task);
                        response += Ui.printLine(
                                String.format("Now you have %d tasks in the list.", tasks.getSize()));
                    } catch (DateTimeParseException e) {
                        throw new Exception("Datetime in the wrong format!");
                    }
                } else {
                    throw new Exception("Please provide a valid due time!");
                }
                break;
            }
        }
        if (dueTimeNotProvided) {
            Task task = new DeadlineTask(tokens[1]);
            tasks.add(task);
            response += Ui.printLine("Got it! I've added this task:");
            response += Ui.printLine("  " + task);
            response += Ui.printLine(String.format("Now you have %d tasks in the list.", tasks.getSize()));
        }
        return response;
    }

    private static String processEventCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a task name!");
        }
        boolean eventTimeNotProvided = true;
        for (int index = 2; index < tokens.length; index++) {
            if (tokens[index].equals("/at")) {
                eventTimeNotProvided = false;
                if (index + 1 < tokens.length) {
                    try {
                        Task task = new EventTask(tokens[1], tokens[index + 1]);
                        tasks.add(task);
                        response += Ui.printLine("Got it! I've added this task:");
                        response += Ui.printLine("  " + task);
                        response += Ui.printLine(
                                String.format("Now you have %d tasks in the list.", tasks.getSize()));
                    } catch (DateTimeParseException e) {
                        throw new Exception("Datetime in the wrong format!");
                    }
                } else {
                    throw new Exception("Please provide a valid event time!");
                }
                break;
            }
        }
        if (eventTimeNotProvided) {
            Task task = new EventTask(tokens[1]);
            tasks.add(task);
            response += Ui.printLine("Got it! I've added this task:");
            response += Ui.printLine("  " + task);
            response += Ui.printLine(String.format("Now you have %d tasks in the list.", tasks.getSize()));
        }
        return response;
    }

    private static String processFindCommand(String[] tokens, TaskList tasks) throws Exception {
        String response = "";
        if (tokens.length < 2) {
            throw new Exception("Please provide a valid regex expression!");
        }
        ArrayList<String> matchedTaskStrings = new ArrayList<>();
        ArrayList<Integer> matchedTaskIndices = new ArrayList<>();
        Pattern pattern = Pattern.compile(tokens[1], Pattern.CASE_INSENSITIVE);
        for (int i = 1; i <= tasks.getSize(); i++) { // i is one-based!
            Matcher matcher = pattern.matcher(tasks.getTaskName(i));
            if (matcher.find()) {
                matchedTaskStrings.add(tasks.getTaskString(i));
                matchedTaskIndices.add(i);
            }
        }
        response += Ui.printLine("Here are the matching tasks in your list:");
        for (int i = 0; i < matchedTaskIndices.size(); i++) {
            response += Ui
                    .printLine(String.format("%d.%s", matchedTaskIndices.get(i), matchedTaskStrings.get(i)));
        }
        return response;
    }

    /**
     * Processes a command.
     *
     * @param command The command to process.
     * @return The response.
     */
    public static String processCommand(String command, TaskList tasks) throws Exception {
        assert command.length() > 0 : "Command should not be empty";
        String[] tokens = tokenizeCommand(command);
        String response = "";
        if (EXIT_COMMANDS.contains(command)) {
            response += processExitCommand(tokens, tasks);
        } else {
            if (tokens[0].equals("clear")) {
                response += processClearCommand(tokens, tasks);
            } else if (tokens[0].equals("help")) {
                response += processHelpCommand(tokens, tasks);
            } else if (tokens[0].equals("list")) {
                response += processListCommand(tokens, tasks);
            } else if (tokens[0].equals("done")) {
                response += processDoneCommand(tokens, tasks);
            } else if (tokens[0].equals("delete")) {
                response += processDeleteCommand(tokens, tasks);
            } else if (tokens[0].equals("todo")) {
                response += processTodoCommand(tokens, tasks);
            } else if (tokens[0].equals("deadline")) {
                response += processDeadlineCommand(tokens, tasks);
            } else if (tokens[0].equals("event")) {
                response += processEventCommand(tokens, tasks);
            } else if (tokens[0].equals("find")) {
                response += processFindCommand(tokens, tasks);
            } else {
                throw new Exception("Unknown command!");
            }
        }
        Storage.saveToFile(tasks);
        return response;
    }
}
